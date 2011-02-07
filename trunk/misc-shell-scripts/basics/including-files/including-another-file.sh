#!/bin/bash


# include another file:
. "included-file.txt"


# accessing a variable from included file:
echo "variable value is  : $SOMETHING";


# print array content from included file:
for (( i=0; i < "${#SOME_ARRAY[*]}"; i++ )) ; do
	ELEMENT="${SOME_ARRAY[$i]}";
    echo "array element $i    : $ELEMENT";
done


# call a function defined in another file:
someFunction "sepp";

