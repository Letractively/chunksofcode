#!/bin/zsh

find src/main/ -type f -iregex '.*\(js\|html\|gif\|png\|css\|properties\)' \
    | while read file; 
    do 
        target=$(
            echo "$file" | \
            sed -e 's|src/main/webapp/|target/file-exchange-1.0-SNAPSHOT/|' \
                -e 's|src/main/java/|target/file-exchange-1.0-SNAPSHOT/WEB-INF/classes/|'
        ); 
        cp -v "$file" "$target"; 
    done

mkdir TEMP_COMPILE_DIR;
javac -classpath 'target/file-exchange-1.0-SNAPSHOT/WEB-INF/lib/*:/home/andre/tomcat/lib/*' \
      src/main/**/*.java \
      -d TEMP_COMPILE_DIR/ \
      -target 1.5

find TEMP_COMPILE_DIR -type f -name '*.class' \
    | while read file;
    do
        target=$(
            echo "$file" | sed -e 's|TEMP_COMPILE_DIR/|target/file-exchange-1.0-SNAPSHOT/WEB-INF/classes/|'
        );
        cp -v "$file" "$target"; 
    done

rm -r TEMP_COMPILE_DIR;
