@ECHO OFF

ECHO.this file will start video-tools java application.

:: the base dir in one string for easy configuring:
set BIN_DIR=${basedir}\target
ECHO.BIN_DIR: %BIN_DIR%


:: the args given to this script, they will be passed to java main method:
set CLI_ARGS=%*
ECHO.CLI_ARGS: %CLI_ARGS%


:: the name of the jar file:
set JAR_FILE_NAME=${artifactId}-${version}.jar
ECHO.JAR_FILE_NAME: %JAR_FILE_NAME%


:: construct classpath:
set CLASSPATH=%BIN_DIR%\%JAR_FILE_NAME%
set CLASSPATH=%CLASSPATH%;%BIN_DIR%\conf
set CLASSPATH=%CLASSPATH%;%BIN_DIR%\lib\*

ECHO.CLASSPATH: %CLASSPATH%


::define main class:
set MAINCLASS=com.myapp.videotools.VideoCommandLineTool

java -cp %CLASSPATH% %MAINCLASS% %CLI_ARGS%