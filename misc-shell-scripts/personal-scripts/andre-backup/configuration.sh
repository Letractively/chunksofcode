#!/bin/bash
########################################################################

	##################################
	#
	#	CONFIGURATION OF SYNC PROCESS:
	#
	#	customize backup process in this file.
	#
	##################################



    #
    #
    # the folder where the backup is being stored
    #
    #
# e.g. ROOT="/media/c971f423-5b14-43a9-a41c-8b9ea89ad94e/backup"
ROOT="/media/c971f423-5b14-43a9-a41c-8b9ea89ad94e/backup"
	#
	#
	# the target destination dir where all data will be stored:
	# source folders will be backed up below this destination target directory:
	#
	# be careful: the trailing slash is important! (see rsync manual)
	#
BACKUP_DESTINATION="${ROOT}/data/";

	#
	#
	# the arguments that will be passed to the rsync command
	#
BACKUP_ARGS="--archive --delete --verbose";


	#
	# if 0 or "", the files will be copied to the backup destinantion:
    # if set and not equal 0, a dry-run will be performed to
    # display what would have been done on syncing.
    #
    # a logfile will be created too
	#
DRY_RUN=0;



	#
	#
	# a logfile where all activities will be logged:
	# each run will be logged in a new file.
	#
LOGFILE="${ROOT}/logs/backup_$(date +%Y-%m-%d_%H-%M-%S).log";


	#
	# specify the dirs that should be synchronized with the backup
	# 
	# !!! WARNING: the names of the dirs must be unique. multiple, different
	#              dirs with the same name will overwrite each other.
	#
	# !!! NOTE:    do NOT define dirs with a trailing slash, otherwise the
	#              contents of the dir will be copied under the
	#              backup root dir instead of the dir itself!
	#
BACKUP_SOURCE_DIRS=(
	"/media/datadisk/personal"
	"/media/datadisk/books"
	"/media/datadisk/software"
	"/media/datadisk/vm"
	"/media/warez/games"
	"/media/datadisk/sound"
	"/media/warez/videos"
);
