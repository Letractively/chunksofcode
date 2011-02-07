#!/bin/bash

STUFF=(
	"foo"
	"bar"
	"baz"
);

for (( i=0; i < "${#STUFF[*]}"; i++ )) ; do
	ELEMENT="${STUFF[$i]}";
    echo "element: $ELEMENT";
done;
