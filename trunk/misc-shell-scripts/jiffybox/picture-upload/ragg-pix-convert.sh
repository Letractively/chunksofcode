#!/bin/bash

TARGET_DIMENSION="1024x1024" # maximum dim

thisDir=$( cd `dirname "$0"`; pwd  )

# the place where the source files are stored
sourceCopy="$thisDir/SOURCE"
convertTarget="$thisDir/CONVERTED"
mkdir $convertTarget

tmpDir="$thisDir/TMP"
mkdir $convertTarget


# copy the files from external hdd:
#rsync -va /media/My\ Passport/ragg\ 3000\ media/ragg*pics $workCopy/


# build regex:
# the regex will look like: "[^\/]*\.(jpg|jpeg|gif)$"
regex="[^\/]*\.(firstdummyentry"
for pattern in $(cat "$thisDir/file-patterns.txt"); do
    if [ "x$regex" == "x" ]; then
        regex="[^\/]*\.(firstdummyentry"
    else
        regex="$regex|$pattern"
    fi
done
regex="$regex)\$"
echo "\$regex : '$regex'"



results=$(mktemp --tmpdir=$tmpDir)

find "$sourceCopy" -type f -follow | grep -iE "$regex" > "$results"

totalCount="$(cat $results | wc -l)"
echo "$totalCount files found. now resize them to: $convertTarget ..."

i=0
cat "$results" | while read img; do
    i=$(( $i + 1 ))

    targetFile=$(echo "$img" | sed -e "s|$sourceCopy|$convertTarget|g")
    #echo "\$targetFile   : '$targetFile'"

    if [ -f "$targetFile" ]; then
        continue;
    fi
    
    targetParent="$(dirname "$targetFile")"
    #echo "\$targetParent : '$targetParent'"
    
    if [ ! -d "$targetParent" ]; then
        mkdir -p "$targetParent"
        # echo "created dir: '$targetParent'"
    fi

    # XXX note the ">", see: http://www.imagemagick.org/script/command-line-processing.php#geometry
    convert -resize "$TARGET_DIMENSION>" "$img" "$targetFile"
    
    if [ "$(echo "$i % 100" | bc)" == "0" ]; then
        echo "$i / $totalCount files converted."
    fi

done





