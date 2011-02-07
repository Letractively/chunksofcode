#!/bin/bash
	#
	# returns true (1) if argument 1 is containing the 
	# string from argument 2, otherwise false.
	#
	#
containsElement () {
	
	ARRAY=$1;
	#echo "ARRAY: ${ARRAY[*]}";
	
	ELEMENT=$2;
	#echo "ELEMENT: ${ELEMENT}";

	if [ -z "$ARRAY" ] ; then
		echo "error: first argument must not be empty!";
		exit 34221;
	fi
	
	if [ -z "$ELEMENT" ] ; then
		echo "error: second argument must not be empty!";
		exit 34222;
	fi
	
	MATCH="$( echo ${ARRAY[*]} | grep -iE $ELEMENT )";
	
	if [ -z "$MATCH" ] ; then
		# echo "$ELEMENT NOT contained in ${ARRAY[*]}";
		return 0;
	fi;
	
	# echo "$ELEMENT is  contained in ${ARRAY[*]}";
	return 1;
}




ARR=("a" "b" "c" "d" "e" "f");

echo "arr: ${ARR[*]}";

containsElement "${ARR[*]}" c; echo "c: $?";
containsElement "${ARR[*]}" b; echo "b: $?";
containsElement "${ARR[*]}" d; echo "d: $?";
containsElement "${ARR[*]}" x; echo "x: $?";

