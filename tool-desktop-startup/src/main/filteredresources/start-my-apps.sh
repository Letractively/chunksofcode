#!/bin/bash

echo "************* START EXECUTION OF FILE $0 ****************************"
echo

echo this file will start the ${artifactId} tool.
echo

# the base dir (usually the "target" folder after build):
# does NOT end with a '/':
    APP_BIN_DIR="$(cd `dirname "$0"`; pwd)"
    echo APP_BIN_DIR: $APP_BIN_DIR
    echo


# the name of the jar file:
    JAR_FILE_NAME="${artifactId}-${version}.jar"
    echo JAR_FILE_NAME: $JAR_FILE_NAME
    echo


# calculate the classpath needed to start application:
    # add jarfile
    CLASSPATH="$APP_BIN_DIR/$JAR_FILE_NAME"

    ######## USEFUL WHEN DEBUGGING / DEVELOPING ############
    # use classes instead of jarfile :
    # CLASSPATH="$APP_BIN_DIR/classes"
    ########################################################
    
    # add libraries:
    CLASSPATH="${CLASSPATH}:$APP_BIN_DIR/lib/*"

    echo CLASSPATH: $CLASSPATH
    echo


# application entry point:
    MAINCLASS=${commandline.mainclass}
    echo MAINCLASS: $MAINCLASS
    echo 


# print out what is being executed:
    echo will now execute:
    echo java -cp $CLASSPATH $MAINCLASS $@
    echo


java -cp "$CLASSPATH" $MAINCLASS "$@"


echo
echo "************* END EXECUTION OF FILE $0 ****************************"
