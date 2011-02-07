#!/bin/bash

sum=0;

find src test resources pom.xml -type f \
    | grep -vE "*\.svn*" \
    | grep -vE "*\.gif" \
    | grep -vE "*thirdparty*" \
    | grep -vE "*doxygen*" | \
while read line;
do
    lines_of_file="$(wc -l $line)";
    echo $lines_of_file;
    num="$(echo $lines_of_file | cut -d' ' -f 1)";
    sum=$(echo "$num + $sum" | bc);
	echo "       so far: $sum";
done;