#!/bin/bash
HERE=$(dirname "$0")

java -jar $HERE/tool-multigrep-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
