#!/bin/bash


# check args count
if [[ $# -ne "1" ]] ; then
    echo "args count: $#";
    echo "argument must be ONE dir which subdirs containing the svn projects, e.g. your workspace";
    exit 1;
fi

DIR_ARG=$1;


# check arg file exists and is a dir
if [[ -d $DIR_ARG ]] ; then
    echo "not an existing directory: $DIR_ARG";
    exit 2;
fi


# all subdirs have to contain a .svn directory
for i in `find "$DIR_ARG" -maxdepth 2 -mindepth 1 -type d -name ".svn"`; do
    SVN_DIR=$( print "$i" | sed 's/\/\.svn//g' );
    echo "\n\n\n############### $SVN_DIR ################\n";

    svn status "$SVN_DIR";
    svn update "$SVN_DIR";
    echo $?;

done;
