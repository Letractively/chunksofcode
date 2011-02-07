#!/bin/bash

LOGFILE=/tmp/foo

date > $LOGFILE

echo "pwd : $(pwd)" >> $LOGFILE
echo "\$# : $#" >> $LOGFILE
echo "\$1 : $1" >> $LOGFILE
echo "\$@ : $@" >> $LOGFILE
echo "\$0 : $0" >> $LOGFILE
echo "\$* : $*" >> $LOGFILE

if [ $# -ne 1 ] ; then
    echo "ERROR: must be one argument!" >> $LOGFILE
    exit 1
fi

if [ ! -f "$(pwd)/$1" ] ; then
    echo "ERROR: must be a file: $1" >> $LOGFILE
    exit 2
fi

EXTENSION=$(echo "$1" | grep -iEo '\.[_a-z0-9]*$')
echo "\$EXTENSION : $EXTENSION" >> $LOGFILE


PARENT_NAME=$(basename "$(pwd)")
echo "\$PARENT_NAME : $PARENT_NAME" >> $LOGFILE

TARGET_PATH="$(pwd)"/"$PARENT_NAME""$EXTENSION"
echo "\$TARGET_PATH : $TARGET_PATH" >> $LOGFILE

if [ -e "$TARGET_PATH" ] ; then
    echo "ERROR: target file exists!"
    exit 3
fi

mv -v "$(pwd)/$1" "$TARGET_PATH" >> $LOGFILE


