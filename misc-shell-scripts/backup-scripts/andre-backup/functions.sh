#!/bin/bash
############################################################################

	##################################
	#
	#	DEFINITION OF FUNCTIONS:
	#
	##################################




	#
	#
	# prints an argument to std-out and also appends it to the logfile:
	#
printItOut() {
	echo "$1";

            #
            # if there is such a logfile, append message to it:
            #
        if [ -w $LOGFILE ] ; then
            echo "$1" >> $LOGFILE;
        fi
}




	#
	#
	# prints a message displaying the sync process is about to start:
	#
printStartMessage() {
	printItOut; printItOut; printItOut; printItOut;
	printItOut "###################################################";
	printItOut "#########";
	printItOut "#########      will now start backup process... ";
	printItOut "#########";
	printItOut "#########            from SOURCE directory  : $SOURCE_DIR";
	printItOut "#########            into backup directory  : $BACKUP_DESTINATION";
	printItOut "#########";
	printItOut "#########            rsync arguments        : $BACKUP_ARGS";
	printItOut "#########            logfile                : $LOGFILE";
	printItOut "#########";
    echo "#########            you may run following command to view details:";
    echo "                     tail -f $LOGFILE";
}




	#
	#
	# prints a message displaying the sync process has finished:
	#
printEndMessage() {
	printItOut "#########";
	printItOut "#########         done with backup process!";
	printItOut "#########";
	printItOut "#########            from SOURCE directory  : $SOURCE_DIR";
	printItOut "#########            into backup directory  : $BACKUP_DESTINATION";
	printItOut "#########";
	printItOut "#########            rsync arguments        : $BACKUP_ARGS";
	printItOut "#########            logfile                : $LOGFILE";
	printItOut "#########";
	printItOut "###################################################";
	printItOut; printItOut; printItOut; printItOut;
}




	#
	#
	# executes the rsync command for a given directory:
	# this will run rsync with the BACKUP_ARGS and the source dir as source.
	# all rsync output will be written to the logfile.
	# information needed to take a look at the progress will be written to
	# std out.
	#
	#    @argument $1  the source directory that will be copied 
	#	 			   into the backup root dir
	#
backItUp() {

	SOURCE_DIR="$1";

	printStartMessage;
	
		#
		# fail if source dir is not existing or not readable:
		#
	if [ ! -d $SOURCE_DIR -o ! -r $SOURCE_DIR ] ; then
		printItOut "#########";
		printItOut "ERROR:";
		printItOut "   source dir is not existing or not readable: '${SOURCE_DIR}'";
		printItOut;
		exit 133;
	fi;
	

		#
		# run rsync and store the exit value of its process
		#
	EXIT_VAL="1234567";
	rsync $BACKUP_ARGS "$SOURCE_DIR" "$BACKUP_DESTINATION" >> $LOGFILE;
	EXIT_VAL="$?";


		#
		# fail if rsync did not exit properly (exit status != 0):
		#
	if [ $EXIT_VAL -ne 0 ] ; then
		printItOut "#########";
		printItOut "ERROR:";
		printItOut "  rsync did not exit properly (exit status = ${EXIT_VAL} )";
		printItOut;
		exit 231;
	fi;

	printEndMessage;
}





    #
    # checks the configuration if every source dir is existing and readable,
    # checks if the backup destination is existing and readable.
    # checks for name conflicts with source directories.
    # checks if destination has a trailing slash and all source dirs do not.
    #
checkBeforeExecuting() {

	    #
	    # fail if backup destination is not existing or not writeable:
	    #
    if [ ! -d $BACKUP_DESTINATION -o ! -w $BACKUP_DESTINATION ] ; then
	    printItOut "#########";
	    printItOut "ERROR:";
	    printItOut "  backup dir is not existing or writeable: '${BACKUP_DESTINATION}'";
	    printItOut;
	    exit 234;
    fi;

	    #
	    # fail if backup destination is not defined with trailing slash:
	    #
    if [ -z "$(echo $BACKUP_DESTINATION | grep '^.*/$')" ] ; then
	    printItOut "#########";
	    printItOut "ERROR:";
	    printItOut "  you have to specify BACKUP_DESTINATION with trailing slash ! ${BACKUP_DESTINATION}";
	    printItOut;
	    exit 764;
    fi;


	    #
	    # fail if there are locations that not readable 
	    # or different locations with the same name:
	    #
    UNIQ_CHECK_ARR=( );

    for (( i=0; i < "${#BACKUP_SOURCE_DIRS[*]}"; i++ )) ; do
	
	    DIR_PATH="${BACKUP_SOURCE_DIRS[$i]}";
	

		    #
		    # fail if source dir is not existing or not readable:
		    #
	    if [ ! -d $DIR_PATH -o ! -r $DIR_PATH ] ; then
		    printItOut "#########";
		    printItOut "ERROR:";
		    printItOut "   not an existing directory or not readable: '${DIR_PATH}'";
		    printItOut;
		    exit 633;
	    fi;
	
	    DIR_NAME="$(basename $DIR_PATH)";
		

	        #
	        # fail if $DIR_PATH is defined with trailing slash:
	        #
        if [ ! -z "$(echo $DIR_PATH | grep '^.*/$')" ] ; then
	        printItOut "#########";
	        printItOut "ERROR:";
	        printItOut "  source directories must not have a trailing slash: ${DIR_PATH}";
	        printItOut;
	        exit 764;
        fi;


		    #
		    # fail if this dir is already listed in source directories:
		    #
	    for (( j=0; j < "${#UNIQ_CHECK_ARR[*]}"; j++ )) ; do
                    ELEMENT_DIR_NAME="${UNIQ_CHECK_ARR[$j]}";
                    
                    # echo "uniq check: $ELEMENT_DIR_NAME   ==  $DIR_NAME";
                    # echo "uniq check array: ${UNIQ_CHECK_ARR[@]}"; echo;

		    if [ "$ELEMENT_DIR_NAME" == "$DIR_NAME" ] ; then 
			    printItOut "#########";
			    printItOut "ERROR:";
			    printItOut "   duplicate dir name given: '${DIR_NAME}'";
			    printItOut;
			    exit 303;
		    fi;
	    done;
	
	    UNIQ_CHECK_ARR=( "${UNIQ_CHECK_ARR[@]}" "$DIR_NAME" );
	
    done;
}





############################################################################
