#!/bin/bash

# create some files with random data for testing uploads:
TRG=target/upload-test-files
rm -fr $TRG
mkdir -p $TRG


for i in 0 1 1024 1048576 2097152 3145728 4194304 10485760 20971520; 
do 
    tmpfile="$TRG/tmp.dat"
    cat /dev/urandom | head --bytes=$i > $tmpfile
    name=$(ls -lh $tmpfile | awk '{print $5}' | sed -e 's/,0\{1,\}//');
    mv $tmpfile $TRG/${name}"B.txt";
done
