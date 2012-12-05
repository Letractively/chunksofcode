#!/bin/bash
########################################################################

   ##################################
   #
   #   CONFIGURATION OF SYNC PROCESS:
   #
   #   customize backup process in this file.
   #
   ##################################

HERE="$(cd "`dirname "$0"`"; pwd)"
BACKUP_ROOT="$HERE/.."

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
#BACKUP_ARGS="--archive --delete --verbose"
BACKUP_ARGS="-rlptD --delete --verbose"



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
LOGFILE="${BACKUP_ROOT}/logs/backup_$(date +%Y-%m-%d_%H-%M-%S).log"


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
    "/home/andrer/workspace"
    "/media/datadisk/verena"
    "/media/warez/virtualbox"
    "/media/datadisk/books"
    "/media/datadisk/sound"
    "/media/datadisk/software"
    "/media/warez/games"
    "/media/movies/misc"
    "/media/movies/doku"
    "/media/movies/movies"
    "/media/movies/series"
    "/media/movies/music"
)


