
#!/bin/bash


#####################################################################
# this file is designed to be copied into the nautilus skripts dir
# to enable creating big pictures in nautius' context menu.
#####################################################################

TMP_FILE="/tmp/videotool_tmp_`date +%Y-%m-%d_%H:%M:%S`.log"

{
    echo "************* START EXECUTION OF FILE $0 ****************************"
    echo

    HERE="$(cd "`dirname "$0"`"; pwd)"
    
    # you need to set the following variable to match the binary starting the tool:
    EXECUTABLE="/home/andre/bin/videotool/videotool.sh"
    DEFAULT_ARGS=( --rows 4 --columns 3 --height 220 --width 270 )
    echo "\${DEFAULT_ARGS[@]}: ${DEFAULT_ARGS[@]}"
    

    echo "******************************************"
    echo "** starting with execution of skript:   **"
    echo "** $0"
    echo "**                                      **"
    echo "** printing relevant environment:       **"
    echo "******************************************"
    echo "\$which java : $(which java)"
    echo "\$HERE       : $HERE"
    echo "\$SHELL      : $SHELL"
    echo "\$TEMP_FILE  : $TMP_FILE"
    echo "\$EXECUTABLE : $EXECUTABLE"
    echo "\$#          : $#"
    echo "\$1          : $1"
    echo "\$@          : $@"
    echo "\$0          : $0"
    echo "\$(pwd)      : $(pwd)"
    echo "******************************************"
    echo;echo

    if [ $# -eq 0 ] ; then
        echo "ERROR: no args given!"
        zenity --title='ERROR: no args given!' --error --text='Sie haben keine Argumente angegeben!'
        exit 2
    fi

    if [ $# -eq 1 -a -d "$(pwd)/$1" ] ; then
        # execute recursively if ONE dir:
        RECURSE_INTO="$(pwd)/$1"
        echo "execute recursively for ONE dir: "
        echo "\$RECURSE_INTO: $RECURSE_INTO"

        echo
        echo calling: '/bin/bash "$EXECUTABLE" --create-big-picture -R --video-root-dir $RECURSE_INTO" "${DEFAULT_ARGS[@]}"'
        echo calling: "/bin/bash  $EXECUTABLE  --create-big-picture -R --video-root-dir $RECURSE_INTO" "${DEFAULT_ARGS[@]}"
        
        echo;echo
        /bin/bash "$EXECUTABLE" --create-big-picture -R --video-root-dir "$RECURSE_INTO" "${DEFAULT_ARGS[@]}"
        RETVAL=$?
        echo;echo
        echo "RETVAL: $RETVAL"
    fi


    for i in "$@"    # all the files that are selected in nautilus
    do
        echo "entering loop over element: $i"

        if [ -f "$i" ] ; then
            # execute for one file: 
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

    echo "************* END EXECUTION OF FILE $0 ****************************"
    echo

} > "$TMP_FILE"
