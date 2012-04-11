#!/bin/bash


# assuming this script file is located at the
# project dir root:

HERE="$(
    thisDir=`dirname "$0"`
    cd $thisDir
    pwd
)"
SRC_DIR="$HERE/src/main"
TRG_DIR="$HERE/target/file-exchange-1.0-SNAPSHOT"
SERVLET_API_DIR="/home/andre/tomcat/lib/" # avoid "~" here, because it's part of the classpath



echo "Deploy resources to $TRG_DIR ..."
find "$SRC_DIR" -type f -iregex '.*\(js\|html\|gif\|png\|css\|properties\|xml\)' \
| while read file
do 
    target=$(
        echo "$file" | \
        sed -e "s|$SRC_DIR/webapp/|$TRG_DIR/|" \
            -e "s|$SRC_DIR/java/|$TRG_DIR/WEB-INF/classes/|" \
            -e "s|$SRC_DIR/resources/|$TRG_DIR/WEB-INF/classes/|"
    );
    cp "$file" "$target"
    echo "  $target"
done
echo



tmpBinTarget=$(mktemp -d)
echo "Compiling java files to $tmpBinTarget ..."

# collect java file names in array:
javaFiles=( $(find "$SRC_DIR/java" -type f -iname "*.java") )

# assuming the project is packaged correctly the target dir
# and the tomcat libs contain the servlet api:
javac -classpath "$TRG_DIR/WEB-INF/lib/*:$SERVLET_API_DIR/*" \
      "${javaFiles[@]}" \
      -d "$tmpBinTarget/" \
      -target 1.5
echo



echo "Deploy classes to $TRG_DIR/WEB-INF/classes/ ..."
find "$tmpBinTarget" -type f -name '*.class' \
| while read file
do
    target=$(echo "$file" | sed -e "s|$tmpBinTarget/|$TRG_DIR/WEB-INF/classes/|")
    cp "$file" "$target"
    echo "  $target"
done

rm -r "$tmpBinTarget"

echo
echo "Finished."
