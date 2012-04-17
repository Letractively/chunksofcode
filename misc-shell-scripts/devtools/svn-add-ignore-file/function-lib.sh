#!/bin/bash

# variables used by these functions: {{{
#
# $propertyName
#   the svn:ignore entry value to add/remove in the directory
#   e.g: 'target' or '"*.dat"' or '.project'
#
# $applyDir
#   the path of the directory to change the property for.
#
# $tempFile
#   the file where the resulting property entries are collected
#
# }}}

function appendIgnoreProperty() {
    _init
    echo "will now add '$propertyName' to the ignored entries in '$applyDir'..."

    {   
        svn propget svn:ignore "$applyDir"
        echo "$propertyName"
    } \
    | sort | uniq \
    | grep -vE '^\s*$' \
    > "$tempFile"

    _finish
}

function removeIgnoreProperty() {
    _init
    echo "will now remove '$propertyName' from the ignored entries in '$applyDir'..."
    
    svn propget svn:ignore "$applyDir" \
    | grep -vE "\<${propertyName}\>" \
    | sort | uniq \
    | grep -vE '^\s*$' \
    > "$tempFile"

    _finish
}


function _init() {
    _validateArgs
    tempFile="/tmp/svn-propedit-tempfile-$(whoami)-$(basename "$1")-$RANDOM.txt"

    if [ $DEBUG ]; then # show current properties {{{
        _logDebug "tempFile = '$tempFile'"
        _logDebug svn propget svn:ignore "$applyDir"
        _logDebug '###### CURRENT PROPERTIES ########'
        svn propget svn:ignore "$applyDir"
        _logDebug '##################################'
    fi # }}}

}

function _finish() {
    
    if [ $DEBUG ]; then # show temp file content {{{
        _logDebug cat "$tempFile"
        _logDebug '###### TEMP FILE CONTENT: ######'
        cat "$tempFile"
        _logDebug '################################'
    fi # }}}

    svn propset svn:ignore -F "$tempFile" "$applyDir"
    rm "$tempFile"

    if [ $DEBUG ]; then # show new properties{{{
        _logDebug svn propget svn:ignore "$applyDir"
        _logDebug '####### NEW PROPERTIES #########'
        svn propget svn:ignore "$applyDir"
        _logDebug '################################'
    fi # }}}

}

function _validateArgs() { 
    # determine proper command usage {{{
    
    _logDebug "applyDir:     $applyDir"
    _logDebug "propertyName: $propertyName"
    
    theError=

    if [ ! -d "$applyDir" ]; then 
        theError="failure: not a directory: '$applyDir'"
    elif [ -z "$propertyName" ]; then 
        theError="failure: no ignore property name/pattern given!"
    fi

    if [ ! -z "$theError" ]; then
        _printUsage
        echo
        echo $theError
        exit 1
    fi 

    #}}}
}

function _printUsage() { 
    # print brief usage text {{{
    echo "***************"
    echo "* this tool help you modifying svn:ignore properties of subversioned dirs."
    echo
    echo "* USAGE:"
    echo "* append an entry to the directories' svn:ignore entries:"
    echo "* svn-ignore-append '/path/of/parent' 'file.txt'"
    echo
    echo "* remove an entry from the directories' ignore-settings:"
    echo "* svn-ignore-remove '/path/of/parent' 'file.txt'"
    echo "***************"
    # }}}
}

function _logDebug() {
    if [ $DEBUG ]; then
        echo "[DEBUG]: $*" 1>&2
    fi
}

# vim: set foldmethod=marker:
