#!/bin/bash

#--------------------------------------------------------------------------
#------ parse parameters and initialize variables
#--------------------------------------------------------------------------
#
# the first parameter is the search root directory.
# output option flags are expected after the root dir.
# all additional parameters are interpreted as regex patterns
# applied on the files during the search.
#

# arg 1: the place we will start our search
searchRoot=

# the set of regexes used for searching
searchPatterns=( )

VERBOSE=
DEBUG=
SHOW_HITS=
IGNORE_CASE=
EMBEDDED_TEXT_SIZE=60


#
# next args are flags to control the program behaviour and output
#
for param in "$@"; do
    case "$param" in
        --verbose)
            VERBOSE=1 ;;
        -v)
            VERBOSE=1 ;;
        --debug)
            DEBUG=1   ;;
        --show-hits)
            SHOW_HITS=1 ;;
        -i)
            IGNORE_CASE=1
            ;;
        *)
            if [ "x" == "x$searchRoot" ]; then
                # this must be the search root:
                searchRoot="$param"
            else
                searchPatterns=( "${searchPatterns[@]}" "$param" )
            fi
            ;;
    esac
done

#
# these files are used as result lists
#
tempFile1=/tmp/chaingrep-`whoami`-$RANDOM.tmp
tempFile2=/tmp/chaingrep-`whoami`-$RANDOM.tmp


#--------------------------------------------------------------------------
#------ utilities
#--------------------------------------------------------------------------

# Some color codes: 
# http://www.cyberciti.biz/faq/bash-shell-change-the-color-of-my-shell-prompt-under-linux-or-unix/
txtred=$'\e[0;31m' # Red
txtylw=$'\e[0;33m' # Yellow
txtblu=$'\e[0;34m' # Blue
txtgrn=$'\e[0;32m' # Greeb
txtrst=$'\e[0m'    # Text Reset

function redoutput() {
    echo -e -n ""$txtred"${@}$txtrst"
}
function yellowoutput() {
    echo -e -n ""$txtylw"${@}$txtrst"
}
function greenoutput() {
    echo -e -n ""$txtgrn"${@}$txtrst"
}
function blueoutput() {
    echo -e -n ""$txtblu"${@}$txtrst"
}
function print_debug() {
    if [ $DEBUG ]; then
        yellowoutput "[DEBUG]  $@" 1>&2
        echo '' 1>&2
    fi
}
function print_error() {
    redoutput "[ERROR]  $@" 1>&2
    echo '' 1>&2
}
function print_green() {
    greenoutput  "$@"  1>&2
    echo ''  1>&2
}
function print_verbose() {
    if [ $VERBOSE ]; then
        print_green "[INFO]   $@"
    fi
}
#--------------------------------------------------------------------------
function countLines() {
    if [ ! -s "$1" ]; then
        echo 0
    else
        wc -l "$1" | sed 's!^\s*\([0-9]*\).*$!\1!g' # just the number, not the path
    fi
}
#--------------------------------------------------------------------------
function getTime() {
    date "+%s.%N"
}
function getTimeDiff() {
    now=$(getTime)
    # $1 is the value from getTime()
    diff=$(echo "$now - $1" | bc)
    echo "$diff" | grep -Eo "^[0-9]*(|\.[0-9]{0,3})"
}
#--------------------------------------------------------------------------

#
# do some validation and debug output
#
print_debug "Debug output is enabled."
if [ ! -d "$searchRoot" ]; then
    print_error "Not a directory: '$searchRoot'. Exiting."
    exit 1
fi
if [ $VERBOSE ]; then
    print_debug "Verbose mode is on."
fi
if [ $IGNORE_CASE ]; then
    print_debug "Search is case-insensitive."
fi
if [ $SHOW_HITS ]; then
    print_debug "Printing highlighted results is enabled."
fi
print_debug "searchRoot: $searchRoot"
print_debug "searchPatterns: ${searchPatterns[@]}"

# either '-i' or empty
GREP_I_FLAG=
if [ $IGNORE_CASE ]; then
    GREP_I_FLAG="-i"
fi

#
#   invoke grep -l the first expression and pipe the result into
#   the first tempfile
#
regex="${searchPatterns[0]}"
print_verbose "Searching recursively for '$regex' in '$searchRoot' ..."
rm -f $tempFile1 $tempFile2 # paranoid :)
grep -Erl $GREP_I_FLAG --color=never "$regex" "$searchRoot" > "$tempFile1"

grepReturnStatus=$?
lineCount=$(countLines $tempFile1)
print_debug "grepReturnStatus: $grepReturnStatus, files found: $lineCount"

if [ $lineCount -lt 1 ]; then
    print_error "Nothing found."
    rm -f "$tempFile1" "$tempFile2"
    exit 1
fi
if [ $grepReturnStatus -ne 0 ]; then
    print_error "User Cancelled Operation."
    exit 3
fi

#
#   test each of the remaining expressions against the collected files:
#
for (( i=1; i<${#searchPatterns[@]}; i++ )); do
    regex="${searchPatterns[$i]}"
    print_verbose "OK, $lineCount files found. Searching for '$regex' in collected files ..."

    cat $tempFile1 | while read line; do
        grep -E -l $GREP_I_FLAG --color=never "$regex" "$line" || true
    done > $tempFile2
    returnStatus=$?
    lineCount=$(countLines $tempFile2)
    print_debug "done with search for '$regex'. status=$returnStatus, files found: $lineCount"

    if [ $lineCount -lt 1 ]; then
        print_error "Nothing found."
        rm -f "$tempFile1" "$tempFile2"
        exit 1
    fi
    if [ $returnStatus -ne 0 ]; then
        cat $tempFile2
        print_error "User Cancelled Operation."
        exit 3
    fi


    # swap lists; tempFile1 will hold the result afterwards again.
    swapVal="$tempFile1"; tempFile1="$tempFile2"; tempFile2="$swapVal"
done

#
# print results to stdout (the paths of the matching files)
#
print_verbose "Result: $lineCount files found containing '${searchPatterns[@]}':"
cat "$tempFile1"

if [ ! $SHOW_HITS ]; then
    exit 0
fi

#
# show highlighted matches:
# we want to see the occurences of this file highlighted 
# embedded in its context
#
print_verbose "Will now display highlighted search results ..."
embedRegex=".{0,$EMBEDDED_TEXT_SIZE}"

cat "$tempFile1" | while read hitFile; do
    print_green "$hitFile"

    # embedding implementation using 'grep -o':
    # first prepare and cache the input file, because it is intendet
    # to be used several times. (convert file to a oneliner to 
    # simply workaround grep's multiline issues)
    # XXX re-use existing tempFile2
    cat "$hitFile" \
    | tr '\r' '\n'  | tr -s '\n' | tr '\t' ' '| tr -s ' ' \
    > "$tempFile2" 

    for regex in "${searchPatterns[@]}"; do
        mergedRegex="$embedRegex""$regex""$embedRegex"
        # print_debug "regex: $regex"
        # print_debug "grep -Eo  $GREP_I_FLAG --color=never "$mergedRegex" "$tempFile2""
        cycleStart=$(getTime)
        grep -E $GREP_I_FLAG --color=never "$regex" "$tempFile2" \
        | grep -Eo  $GREP_I_FLAG --color=never "$mergedRegex" \
        | head -n 5 \
        | grep -E  $GREP_I_FLAG --color=always "$regex" \
        | sed -e 's!^\s*!    !g' \
        1>&2
        
        if [ $? -ne 0 ]; then
            print_error "User Cancelled Operation."
            exit 3
        fi

        cycleDuration=$(getTimeDiff "$cycleStart")
        print_debug "grep cycle: $_TOTAL_GREP_CYCLES, time: $cycleDuration"
    done
done





