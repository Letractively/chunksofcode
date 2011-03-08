#!/bin/bash


#####################################################################
# this file is designed to be copied into the nautilus skripts dir
# to enable creating big pictures in nautius' context menu.
#####################################################################

# TMP_FILE="/tmp/videotool_tmp_`date +%Y-%m-%d_%H:%M:%S`.log"
TMP_FILE="/tmp/videotool.log"

{
    echo "************* START EXECUTION OF FILE $0 ****************************"
    echo

    HERE="$(cd "`dirname "$0"`"; pwd)"
    
    # you need to set the following variable to match the binary starting the tool:
    EXECUTABLE="/home/andre/bin/videotool/videotool.sh"
    DEFAULT_ARGS=( --rows 4 --columns 3 --height 220 --width 270 --debug)
    

    # ***************************************************************
    # ************* debug stuff *************************************
    # ***************************************************************

    echo "******************************************"
    echo "** starting with execution of skript    **"
    echo "**" 
    echo "** $(date)"
    echo "**" 
    echo "******************************************"
    echo "** printing relevant environment:       **"
    echo "**" 
    echo "** \$which java         : $(which java)"
    echo "** \$HERE               : $HERE"
    echo "** \$SHELL              : $SHELL"
    echo "** \$TEMP_FILE          : $TMP_FILE"
    echo "** \$EXECUTABLE         : $EXECUTABLE"
    echo "** \$#                  : $#"
    echo "** \$1                  : $1"
    echo "** \$@                  : $@"
    echo "** \$0                  : $0"
    echo "** \$(pwd)              : $(pwd)"
    echo "** \${DEFAULT_ARGS[@]}  : ${DEFAULT_ARGS[@]}"
    echo "******************************************"
    echo;echo


    if [ $# -eq 0 ] ; then
        echo "ERROR: no args given!"
        zenity --title='ERROR: no args given!' --error --text='Sie haben keine Argumente angegeben!'
        exit 2
    fi


    if [ $# -eq 1 ] ; then
        if [ -d "$1" ] ; then # execute recursively if ONE dir:
            echo "execute recursively for ONE dir: "
            echo
            echo calling: '/bin/bash "$EXECUTABLE" --create-big-picture -R --video-root-dir $1   ${DEFAULT_ARGS[@]}"'
            echo calling: "/bin/bash  $EXECUTABLE  --create-big-picture -R --video-root-dir $1   ${DEFAULT_ARGS[@]}"
            echo;echo
            /bin/bash "$EXECUTABLE" --create-big-picture -R --video-root-dir "$1"   "${DEFAULT_ARGS[@]}"
            RETVAL=$?
            echo;echo
            echo "RETVAL: $RETVAL"

        elif [ ! -f "$1" ] ; then # execute thumbnailer for one file:
            echo "ERROR: no such file : $1"
            zenity --title='ERROR: File not found' --error --text="No such file or directory: $1 !"
            exit 2
        fi

    else

        for i in "$@" ; do # generate thumbnail for each file selected in nautilus:
            echo "entering loop over element: $i"
            RETVAL=0

            if [ -f "$i" ] ; then # execute for one file: 
                echo handling single file:
                echo "\$i: $i"
                echo
                echo calling: '/bin/bash "$EXECUTABLE" --create-big-picture --input-file "$i" --output-file "${i}.jpeg" "${DEFAULT_ARGS[@]}"'
                echo calling: /bin/bash "$EXECUTABLE" --create-big-picture --input-file "$i" --output-file "${i}.jpeg" "${DEFAULT_ARGS[@]}"
                echo;echo
                /bin/bash "$EXECUTABLE" --create-big-picture --input-file "$i" --output-file "${i}.jpeg" "${DEFAULT_ARGS[@]}"
                RETVAL=$?
                echo;echo
                echo "RETVAL: $RETVAL"
            else
                echo "Warning: SKIP entry, not a file: $i"
            fi
                

            # when not successful, show errormsg and exit with errorcode 2
            # (note this will run when the first and only argument was a 
            # directory before the loop!)
            if [ $RETVAL -ne 0 ] ; then
                echo "RETVAL: $RETVAL"
                zenity --title="Da ist etwas schiefgegangen..." --text-info --filename="$TMP_FILE" --width=1000 --height=500
                exit 2
            fi
        done

    fi


    echo "******************************************"
    echo "**" 
    echo "** $(date)"
    echo "**" 
    echo "************* END EXECUTION OF FILE $0 ****************************"
    echo


    # when not successful, show errormsg and exit with errorcode 2
    # (note this will run when the first and only argument was a 
    # directory before the loop!)
    if [ $RETVAL -ne 0 ] ; then
        echo "RETVAL: $RETVAL"
        zenity --title="Da ist etwas schiefgegangen..." --text-info --filename="$TMP_FILE" --width=1000 --height=500
        exit 2
    fi


} > "$TMP_FILE"

