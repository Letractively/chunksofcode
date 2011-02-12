#!/bin/bash
########################################################################

	##################################
	#
	#	CONFIGURATION OF SYNC PROCESS:
	#
	#	customize backup process in this file.
	#
	##################################


BACKUP_ROOT="/media/disk/backup"

	#
	#
	# the target destination dir where all data will be stored:
	# source folders will be backed up below this destination target directory:
	#
	# be careful: the trailing slash is important! (see rsync manual)
	#
BACKUP_DESTINATION="${BACKUP_ROOT}/data/"


	#
	#
	# the arguments that will be passed to the rsync command
	#
BACKUP_ARGS="--archive --delete --verbose"


	#
	# 0 or 1; if 0, the files will be copied to the backup destinantion:
        # if 1, a dry-run will be performed to
        # display what would have been done on syncing.
        #
        # a logfile will be created in both cases
	#
DRY_RUN=1


	#
	#
	# a logfile where all activities will be logged:
	# each run will be logged in a new file.
	#
LOGFILE="/media/disk/backup/logs/backup_$(date +%Y-%m-%d_%H-%M-%S).log"


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
	"/media/datadisk/verena"
	"/media/datadisk/software"
	"/media/datadisk/sound"
	"/media/datadisk/books"
	"/media/warez/games"
	"/media/datadisk/vm"
	"/media/datadisk/system-backup"
	"/media/warez/videos"
	"/media/warez/porn"
)
