#!/bin/bash

TEMPLIST=/tmp/sort-by-timestamp.`date +%F`.txt
rm "$TEMPLIST"
TARGET="/tmp/chronosort-`date +%F`-$RANDOM"
mkdir "$TARGET"
echo TARGET: "$TARGET"

find . -type f | while read $PIC; do
    if [ -f "$PIC" ] # see if PIC is a file
    then
        FILETYPE=

        if [[ "$(file -b --mime-type "$PIC" )" == "image/jpeg" ]]; then
            TIMESTAMP="$(exiv2 "$PIC" | grep -i timestamp | grep -Eo "[0-9].*[0-9]" | tr ':' '-' | tr ' ' '_' )"
            cp -v "$PIC" "$TARGET/${TIMESTAMP}__${PIC}.jpg"
        else
            echo "Skipping file $PIC....."
        fi
    fi
done

