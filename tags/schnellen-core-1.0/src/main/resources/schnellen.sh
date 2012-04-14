#!/bin/bash

# assuming this file is in target dir:
PARENT="`dirname $0`/.."
HERE="$(cd "$PARENT";  pwd)" # this will NOT change the dir for THIS process
# HERE="${basedir}" # using pom property


echo "HERE=$HERE"
echo

MY_CLASSPATH="$(cat $HERE/target/classpath.txt)"
MY_CLASSPATH="$MY_CLASSPATH:$HERE/target/classes"
echo "MY_CLASSPATH=$MY_CLASSPATH" 
echo
echo "TODO: you should disable assertions when tested properly!"
echo
java -ea -cp "$MY_CLASSPATH" ${game.main.class} $1
