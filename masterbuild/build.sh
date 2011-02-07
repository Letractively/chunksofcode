#!/bin/bash

SCRIPT="$0";
HERE="$(dirname "$SCRIPT")"

LOG="$HERE/fullbuild.log"
ERR="$HERE/fullbuild.err"


rm "$LOG"


{
    date
    echo mvn "$@" "-f" "$HERE"/pom.xml
    time mvn "$@" "-f" "$HERE"/pom.xml

} 2> "$ERR" | tee "$LOG"
