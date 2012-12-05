#!/bin/bash


# author: andre ragg


# "here" is the location of this script file:
here=$(cd "`dirname "$0"`"; pwd)

# "target" is the place where the resized images will be saved:
target="$here/small-versions"


# first, purge or create the target dir:

if [ -d "$target" ]; then
    echo "wiping everything inside $target ..."
    count=$(rm -vr "$target"/* 2>/dev/null | wc -l)
    echo "$count elements removed."
    
else
    echo "creating $target ..."
    mkdir "$target"
    
    if [ "$?" != "0" ]; then
        echo "ERROR: $? could not create $target"
        exit 101
    fi
fi

echo


# this function encapsulates the decision if the image will be scaled
# to an given size, hopefully fine granulated.
function worthToScale() {
    size=$1

    if [ $size -le 128 ]; then
        return 0
        
    elif [ $size -le 256 ]; then
        mod32=$(echo "$size % 32" | bc)
        
        if [ $mod32 -eq 0 ]; then
            return 0
        fi
        
    elif [ $size -le 512 ]; then
        mod64=$(echo "$size % 64" | bc)
        
        if [ $mod64 -eq 0 ]; then
            return 0
        fi
    fi
    
    return 1
}


# look for the orig files (XXX: recognized by ".origin." in the filename)
find "$here" -name "*.origin.*" -type f -not -path "*/$target/*" | while read image; do
    echo "generating resized versions for: $image";

    # generate a small image from the orignial, using 16 pixels per step
    for (( i=1; i * 16 <= 512; i++ )); do
        newSize=$(echo "scale=4; $i * 16" | bc)
        
        if ! worthToScale $newSize; then
            continue
        fi

        # how many percent do we want to zoom: (XXX: 256 is the original size)
        shrinkFactor=$(echo "scale=4; ($newSize / 256) * 100" | bc)
        
        # compute the target file name by replacing "256" with the new size:
        newName=$(basename $image | sed -e "s/\.origin\./.$newSize./g")
        
        echo "step $i - ${newSize}px - $shrinkFactor % - $newName"
        
        # shrink image file by calling convert:
        convert -resize "$shrinkFactor%" "$image" "$target/$newName"
    done

    echo
done


