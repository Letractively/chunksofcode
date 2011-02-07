#!/bin/bash


SKRIPT_FILE="$0"
BASE_DIR="$(dirname $SKRIPT_FILE)"
JAR_FILE_NAME="${artifactId}-${version}.jar"



# calculate the classpath needed to start application:
    # add jarfile
    CLASSPATH="$BASE_DIR/$JAR_FILE_NAME"
    
    # add configuration dir
    CLASSPATH="${CLASSPATH}:$BASE_DIR/conf"
    
    # add jar files depending on
    CLASSPATH="${CLASSPATH}:$BASE_DIR/lib/*"

    #######################################################
    #        USEFUL WHEN DEBUGGING / DEVELOPING
    #
    # bind classes to path instead of jarfile :
    #             (don't forget to unset jarfile above)
    # CLASSPATH="$BASE_DIR/classes"
    #
    # maybe you want to use config files of source dir:
    #             (don't forget to unset config dir above)
    # BUILD_DIR="${basedir}/target"
    # CLASSPATH="${CLASSPATH}:${BUILD_DIR}/src/main/resources/config"
    #
    #
	########################################################




# application entry point:
MAINCLASS=com.myapp.videotools.cli.CommandLineInterface



# print out what is being executed:
    # echo SKRIPT_FILE: $SKRIPT_FILE
    # echo BASE_DIR: $BASE_DIR
    # echo CLASSPATH: $CLASSPATH
    echo will now execute:
    echo java -cp $CLASSPATH $MAINCLASS $@
    echo


java -cp "$CLASSPATH" $MAINCLASS "$@"
