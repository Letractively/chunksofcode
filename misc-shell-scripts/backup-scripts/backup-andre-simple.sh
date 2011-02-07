#!/bin/bash

	#
	# this script consists of 5 sections:
	# 
	#	CONFIGURATION / SETUP SECTION:
	#  
	#	DEFINITION OF FUNCTIONS:
	#
	#	PREPARE SYNC PROCESS:
	#
	#	EXECUTE BACKUPS SECTION
	#
	#	FINAL SECTION
	#

	#
	# USAGE:
	# 
	# you have to define the variables in the
	#     CONFIGURATION / SETUP SECTION,
	#
	# and call the backup-functions for the directories you wish to back up at the 
	#     EXECUTE BACKUPS SECTION.
	#
	


############################################################################

	##################################
	#
	#	CONFIGURATION / SETUP SECTION:
	#
	##################################
	

	
	#
	#
	# the target destination dir where all data will be stored:
	# source folders will be backed up below this destination target directory:
	#
	# be careful: the trailing slash is important! (see rsync manual)
	#
BACKUP_DESTINATION="/media/disk/backup/";


	#
	#
	# additional arguments passed to rsync that will start a dry-run
	# to preview what would be done by the script without writing anything
	# this line must be commented or deleted to perform a "real" backup ! 
	#
DRY_RUN_ARGS=" --dry-run --human-readable";


	#
	#
	# the arguments that will be passed to the rsync command
	#
BACKUP_ARGS="--archive --delete --verbose ${DRY_RUN_ARGS}";


	#
	#
	# a logfile where all activities will be logged:
	# each run will be logged in a new file.
	#
LOGFILE="${BACKUP_DESTINATION}""backup_$(date +%Y-%m-%d_%H-%M-%S).log";


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
		printItOut "ERROR:";
		printItOut "  rsync did not exit properly (exit status = ${EXIT_VAL} )";
		printItOut;
		exit 231;
	fi;

	printEndMessage;
}



############################################################################

	##################################
	#
	#	PREPARE SYNC PROCESS:
	#
	##################################

	#
	# fail if backup destination is not existing or not writeable:
	#
if [ ! -d $BACKUP_DESTINATION -o ! -w $BACKUP_DESTINATION ] ; then
	printItOut "ERROR:";
	printItOut "  backup dir is not existing or writeable: '${BACKUP_DESTINATION}'";
	printItOut;
	exit 234;
fi;

	#
	# create the logfile
	#
touch "$LOGFILE";





############################################################################

	##################################
	#
	#	EXECUTE BACKUPS SECTION
	#
	##################################


START_TIME_STAMP=$(date);



	#
	# you have to call the backup function for each directory
	# seperately here:
	#
	
backItUp "/media/datadisk/games";
backItUp "/media/datadisk/software";
backItUp "/media/datadisk/sound";
backItUp "/media/datadisk/videos";
backItUp "/media/datadisk/vm";

backItUp "/media/personal/books";
backItUp "/media/personal/job";
backItUp "/media/personal/personal";
backItUp "/media/personal/porn";
backItUp "/media/personal/schule";
backItUp "/media/personal/system-backup";
backItUp "/media/personal/workspace";


FINISH_TIME_STAMP=$(date);





############################################################################

	##################################
	#
	#	FINAL SECTION
	#
	##################################

printItOut "done with executing all directories!";
printItOut;
printItOut "    start time  :  $START_TIME_STAMP";
printItOut "    end   time  :  $FINISH_TIME_STAMP";

echo;
echo "    you may run following command to view details:";
echo "less $LOGFILE";



exit 0;

###########################################################################
