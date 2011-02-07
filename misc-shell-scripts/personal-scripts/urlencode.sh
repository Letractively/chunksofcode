#!/bin/bash

value=$1
#echo $value

encoded="$(perl -MURI::Escape -e 'print uri_escape($ARGV[0]);' "$value")"
echo $encoded
