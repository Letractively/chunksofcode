#!/bin/bash



##############################################################################
#                                                                            #
#                             BRIEF DOC                                      #
#                                                                            #
##############################################################################
#                                                                            #
# this file is designed to be copied into the nautilus skripts dir           #
# to enable creating big pictures in nautius' context menu.                  #
#                                                                            #
# $@ contains all paths that were selected by the user in nautilus.          #
#                                                                            #
# if there are multiple items selected by nautilus, i'll create a thumbnail  #
# image for each selected element of type file. note that directories are    #
# being skipped when they are part of a multiple selection.                  #
#                                                                            #
# if there is only one argument and it is a directory, i'll create           #
# thumbnails recursively for each video file in it.                          #
# note that existing thumbnail files won't be overwritten in this mode.      #
#                                                                            #
##############################################################################




############# definition of functions ########################################


function createSingleThumb() {
    echo "entering function createSingleThumb for file: $1"
    echo calling: /bin/bash "$EXECUTABLE" --create-big-picture --input-file "$1" --output-file "${1}.jpeg" "${DEFAULT_ARGS[@]}";echo;echo
    
    /bin/bash "$EXECUTABLE" --create-big-picture --input-file "$1" --output-file "${1}.jpeg" "${DEFAULT_ARGS[@]}"
    STATUS=$?

    echo;echo;echo "exiting function createSingleThumb with status: $STATUS"
    return $STATUS
}



function createRecursiveThumb() {
    echo "entering function createRecursiveThumb for file: $1"
    echo calling: "/bin/bash  $EXECUTABLE  --create-big-picture -R --video-root-dir $1   ${DEFAULT_ARGS[@]}";echo;echo

    /bin/bash "$EXECUTABLE" --create-big-picture -R --video-root-dir "$1" "${DEFAULT_ARGS[@]}"
    STATUS=$?
    
    echo;echo;echo "exiting function createRecursiveThumb with status: $STATUS"
    return $STATUS
}



function showErrorLog() {
    zenity --title="Something bad happened..." --text-info --filename="$TMP_FILE" --width=1000 --height=500
}







TMP_FILE="/tmp/videotool.log" # where all the output from program below will be written to



################ execute thumbnail call ########################################


{
    echo "************* START EXECUTION OF FILE $0 ****************************"
    echo

    HERE="$(cd "`dirname "$0"`"; pwd)"
    
    # you need to set the following variable to match the binary starting the tool:
    # TODO: make a nice external config file for these two values below!
    EXECUTABLE="/home/andre/bin/videotool/videotool.sh"
    DEFAULT_ARGS=( --rows 4 --columns 3 --height 220 --width 270 --debug)
    

    echo "******************************************"
    echo "** starting with execution of skript    **"
    echo "**" 
    echo "** $(date)"
    echo "**" 
    echo "******************************************"
    
#    echo "************** debug output **************"
#    echo "** printing relevant environment:       **"
#    echo "**" 
#    echo "** \$which java         : $(which java)"
#    echo "** \$HERE               : $HERE"
#    echo "** \$SHELL              : $SHELL"
#    echo "** \$TEMP_FILE          : $TMP_FILE"
#    echo "** \$EXECUTABLE         : $EXECUTABLE"
#    echo "** \$#                  : $#"
#    echo "** \$1                  : $1"
#    echo "** \$@                  : $@"
#    echo "** \$0                  : $0"
#    echo "** \$(pwd)              : $(pwd)"
#    echo "** \${DEFAULT_ARGS[@]}  : ${DEFAULT_ARGS[@]}"
#    echo "******************************************"
    echo;echo


    if [ $# -eq 0 ] ; then
        echo "ERROR: no args given!"
        zenity --title='ERROR: no args given!' --error --text='Sie haben keine Argumente angegeben!'
        exit 2
    fi


    if [ $# -eq 1 ] ; then # only ONE path was received from nautilus
        STATUSCODE=0

        if [ -d "$1" ] ; then # single path is a directory
            # generate thumbnails recursively for this directory:
            createRecursiveThumb "$1"
            STATUSCODE=$?

        elif [ -f "$1" ] ; then # single path is a file
            # generate a thumbnail image for this file:
            createSingleThumb "$1"
            STATUSCODE=$?

        else
            # invalid argument: path is neither a file nor a directory!
            echo "ERROR: no such file : $1"
            zenity --title='ERROR: File not found' --error --text="No such file or directory: $1 !"
            exit 102
        fi

        if [ $STATUSCODE -ne 0 ] ; then showErrorLog; exit 103; fi

    else # multiple paths received from nautilus

        for i in "$@" ; do # for each specified argument

            echo "entering loop over element: $i"
            STATUSCODE=0

            if [ -f "$i" ] ; then # path is a file
                # generate thumbnails for this file:
                createSingleThumb "$i"
                STATUSCODE=$?
            else
                echo "Warning: SKIP entry, not a file: $i"
            fi
                
            if [ $STATUSCODE -ne 0 ] ; then showErrorLog; exit 104; fi
        done
    fi

    echo "******************************************"
    echo "**" 
    echo "** $(date)"
    echo "**" 
    echo "************* END EXECUTION OF FILE $0 ****************************"
    echo


} > "$TMP_FILE"



