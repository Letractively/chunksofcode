@ECHO OFF

ECHO.this file will start video-tools java application.
ECHO.

:: the base dir in one string for easy configuring:
:: ends with a '\':
::  set APP_BIN_DIR=${basedir}\target\
    set APP_BIN_DIR=%~dp0
    ECHO.APP_BIN_DIR: %APP_BIN_DIR%
    ECHO.


:: the name of the jar file:
    set JAR_FILE_NAME=${artifactId}-${version}.jar
    ECHO.JAR_FILE_NAME: %JAR_FILE_NAME%
    ECHO.


:: calculate the classpath needed to start application:
    :: add jarfile
    set CLASSPATH=%APP_BIN_DIR%%JAR_FILE_NAME%

    :: ###### USEFUL WHEN DEBUGGING / DEVELOPING ############
    :: use classes instead of jarfile :
    :: set CLASSPATH=%APP_BIN_DIR%classes
    :: ######################################################
    
    :: add configuration dir:
    set CLASSPATH=%CLASSPATH%;%APP_BIN_DIR%conf

    :: add libraries:
    set CLASSPATH=%CLASSPATH%;%APP_BIN_DIR%lib\*

    ECHO.CLASSPATH: %CLASSPATH%
    ECHO.


:: application entry point:
    set MAINCLASS=${commandline.mainclass}
    ECHO.MAINCLASS: %MAINCLASS%
    ECHO.


:: print out what is being executed:
    ECHO.will now execute:
    ECHO.java -cp %CLASSPATH% %MAINCLASS% %*
    ECHO.

java -cp %CLASSPATH% %MAINCLASS% %*
