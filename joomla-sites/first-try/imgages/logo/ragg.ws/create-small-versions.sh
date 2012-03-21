#!/bin/bash

# "here" is the location of this script file:
here=$(cd "`dirname "$0"`"; pwd)


target="$here/target"
if [ -d "$target" ]; then
    rm -r "$target"/* 
else
    mkdir "$target"
fi

# create small versions in 16 pixel steps:
for i in 256 240 224 208 192 176 160 144 128 112 96 80 64 48 32 16; do
    shrinkFactor=$(echo "scale=5; ($i/256)*100" | bc)
    echo "$i - $shrinkFactor"
    
    # look for the orig files (XXX: recognized by "256" in the filename)
    find "$here" -maxdepth 1 -type f -name "*256*" -not -ipath "*/.svn/*" \
    | while read image; do
        # shrink image file
        imgName=$(basename $image)
        newName=$(echo $imgName | sed s/256/$i/g)
        convert -resize "$shrinkFactor%" "$image" "$target/$newName"
    done
done


