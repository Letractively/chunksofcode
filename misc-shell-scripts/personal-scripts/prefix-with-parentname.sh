#!/bin/bash

echo current working dir: "$(pwd)";

for i in "$@" ; 
do 
    parent="$(dirname "$i")";
    name="$(basename "$i")";
    
    parent_fullpath="$(cd "$parent"; pwd)";
    parent_name="$(basename "$parent_fullpath")";
    new_name="$parent_name - $name";
    
    echo "\$parent : '$parent'"
    echo "\$name : '$name'"
    
    echo "\$parent_fullpath : '$parent_fullpath'"
    echo "\$parent_name : '$parent_name'"
    echo "\$new_name : '$new_name'"
   
    echo mv -v "$i" "$parent/$new_name"
    mv -v "$i" "$parent/$new_name"
    echo;
done
