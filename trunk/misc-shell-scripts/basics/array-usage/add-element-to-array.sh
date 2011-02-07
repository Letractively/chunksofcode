#!/bin/bash

PEOPLE=("fritz" "sepp" "franz" "hugo");

F_GROUP=( );

echo;
echo "grouping PEOPLE by their names...";

for (( i=0; i < "${#PEOPLE[*]}"; i++ )) ; do
    PERSON="${PEOPLE[$i]}";

    # test if name starts with f: (if the string through the grep is not empty)
    if [ ! -z "$( echo $PERSON | grep -Ei '^[f].*$' )" ] ; then
        echo "begins with f        : $PERSON";

        # if yes, add the person to the F_GROUP:
        F_GROUP=( "${F_GROUP[@]}" "$PERSON" );
        
    else
        echo "does not begin with f: $PERSON";
    fi;
done;

echo;
echo "print members of F_GROUP:"

for (( i=0; i < "${#F_GROUP[*]}"; i++ )) ; do
    echo "in f group: ${F_GROUP[$i]}";
done;