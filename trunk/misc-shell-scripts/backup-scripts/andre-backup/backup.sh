#!/bin/bash

	#
	# USAGE:
	# 
	# you have to define the variables in the
	# config file for your settings.
	#
	# and start this shell script in the directory where
	# it is stored, assuming there are the files for configuration
	# and a functions library in the same directory.
	#
############################################################################

	##################################
	#
	#	CONFIGURATION
	#  
	#  
	# 	include configuration from config-file:
	#
	##################################

HERE="$(cd "`dirname "$0"`"; pwd)"
echo "HERE='$HERE'"

CONFIG_FILE="$HERE/configuration.sh";
echo "Using config file: ${CONFIG_FILE}"

	#
	# fail if CONFIG_FILE is not existing or not readable:
	#
if [ ! -f "$CONFIG_FILE" -o ! -r "$CONFIG_FILE" ] ; then
	echo "#########";
	echo "ERROR:";
	echo "   ${CONFIG_FILE} is not existing or not readable!";
	exit 567;
else
        echo "Using config file: ${CONFIG_FILE}" 
fi;


	#
	# include config file:
	#
. "$CONFIG_FILE";
	
	
	
    #
    # check if a dry run should be executed
    #
if [ -z "$DRY_RUN" -o ! "0" == "$DRY_RUN" ] ; then
    BACKUP_ARGS="$BACKUP_ARGS --dry-run --human-readable";
    LOGFILE="${LOGFILE}.DRY-RUN";
fi;







############################################################################

	##################################
	#
	#	DEFINITION OF FUNCTIONS:
	#
	#  
	# 	include function definitions from library file:
	#
	##################################
	
FUNCTION_LIBRARY_FILE="$HERE/functions.sh";
echo "Using library file: ${FUNCTION_LIBRARY_FILE}" 


	#
	# fail if FUNCTION_LIBRARY_FILE is not existing or not readable:
	#
if [ ! -f "$FUNCTION_LIBRARY_FILE" -o ! -r "$FUNCTION_LIBRARY_FILE" ] ; then
	echo "#########";
        echo "ERROR:";
        echo "   ${FUNCTION_LIBRARY_FILE} is not existing or not readable!";
	exit 133;
fi;


	#
	# include library file:
	#
. "$FUNCTION_LIBRARY_FILE"



############################################################################

	##################################
	#
	#	PREPARE SYNC PROCESS, CHECK SETUP:
	#
	##################################



    #
    # fail if anything is obviously configured incorrectly:
    #
checkBeforeExecuting;


	#
	#	ASK USER TO CONFIRM PROCESS:
	#
printItOut "###########################################";
printItOut "#";
printItOut "# READY TO START BACKUP PROCESS:";
printItOut "#";
printItOut "#    source folders that will be backed up:";

for (( i=0; i < "${#BACKUP_SOURCE_DIRS[*]}"; i++ )) ; do
	DIR_PATH="${BACKUP_SOURCE_DIRS[$i]}";
    printItOut "#        $i    $DIR_PATH";
done;

printItOut "#";
printItOut "#    destination directory:";
printItOut "#        $BACKUP_DESTINATION";
printItOut "#";
printItOut "#    logfile:";
printItOut "#        $LOGFILE";
printItOut "#";

if [ -z "$DRY_RUN" -o ! "0" == "$DRY_RUN" ] ; then
    printItOut "#    THIS WILL PERFORM A DRY-RUN:     rsync $BACKUP_ARGS";
    printItOut "#       nothing will be written, but logfile will be created.";
    printItOut "#       (see configuration.sh)";
else
    printItOut "#    THIS RUN WILL SYNC DIRS REALLY";
    printItOut "#       no dry-run !";
    printItOut "#       files will be written to backup-destination !";
fi;

printItOut "#";
printItOut "#";
printItOut "###########################################";
echo;
echo -n " ARE YOU SURE YOU WANT TO START? (Y/N) : > ";

read LINE;
printItOut "USER ENTERED $LINE";



	#
	# exit if user chose no:
	#
if [ ! "$LINE" == "Y" -a ! "$LINE" == "y" ] ; then
    echo "exiting...";
    exit;
fi;

printItOut "###########################################";





	#
	# create the logfile
	#
touch "$LOGFILE";



	#
	# fail if logfile could not be created:
	#
if [ $? -ne 0 -o ! -f "$LOGFILE" ] ; then
	printItOut "#########";
	printItOut "ERROR:";
	printItOut "  logfile could not be created ! ${LOGFILE}";
	printItOut;
	exit 794;
fi






############################################################################

	##################################
	#
	#	!!!   EXECUTE BACKUPS   !!!
	#
	##################################



	#
	# storing the timestamps when the single processes were
	# started and finished in the same order as $BACKUP_SOURCE_DIRS
	#
EACH_START_TIME_STAMPS=( );
EACH_FINSISH_TIME_STAMPS=( );
START_TIME_STAMP="$(date +%s)"; # (seconds between 1970 and now)

for (( i=0; i < "${#BACKUP_SOURCE_DIRS[*]}"; i++ )) ; do
	DIR="${BACKUP_SOURCE_DIRS[$i]}";
	
	EACH_START_TIME_STAMPS=( "${EACH_START_TIME_STAMPS[@]}" "$(date +%s)" );
	
		#
		# EXECUTE the backup process:
		#
    echo "$DIR";
    backItUp "$DIR";
    
	EACH_FINSISH_TIME_STAMPS=( "${EACH_FINSISH_TIME_STAMPS[@]}" "$(date +%s)" );
done;


FINISH_TIME_STAMP=$(date +%s); # (seconds between 1970 and now)








############################################################################

	##################################
	#
	#	FINAL SECTION
	#
	#   show some useful information
	#
	##################################

printItOut;
printItOut "######################################################";
printItOut "#";
printItOut "#    done with syncing all directories!";
printItOut "#";

# uncomment if block to see detail information:
#if [ "a" == "b" ] ; then
    for (( i=0; i < "${#BACKUP_SOURCE_DIRS[*]}"; i++ )) ; do
    
        DIR="${BACKUP_SOURCE_DIRS[$i]}";
        START="${EACH_START_TIME_STAMPS[$i]}";
        FINISH="${EACH_FINSISH_TIME_STAMPS[$i]}";

        DIFFERENCE="$(echo $FINISH - $START | bc)";
        printItOut "#   synced dir  : ${DIR}";
        printItOut "#   time needed : $DIFFERENCE seconds";
        printItOut "#";
    done;
#fi;

DIFFERENCE="$(echo $FINISH_TIME_STAMP - $START_TIME_STAMP | bc)";
	
printItOut "#";
printItOut "#    total time  :  $DIFFERENCE seconds";
printItOut "#";
printItOut "######################################################";

echo;
echo "you may run following command to view details:";
echo "less $LOGFILE";

exit 0;

###########################################################################
