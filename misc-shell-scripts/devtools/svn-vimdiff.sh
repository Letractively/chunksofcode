#!/bin/sh

# this will make svn diff use vimdiff 

# XXX to install this script properly, edit the ~/.subversion/config file
# XXX and set the "diff-cmd" variable to run _this_ script:
# XXX diff-cmd = /home/andrer/bin/svn-vimdiff.sh


# Configure your favorite diff program here.
DIFF="/usr/bin/vimdiff"

# Subversion provides the paths we need as the sixth and seventh 
# parameters.
REMOTE=${6}
echo "remote: $REMOTE" 1>&2

LOCAL=${7}
echo "local:  $LOCAL" 1>&2

# Call the diff command (change the following line to make sense for
# your merge program).
"$DIFF" "$LOCAL" "$REMOTE"

# Return an errorcode of 0 if no differences were detected, 1 if some were.
# Any other errorcode will be treated as fatal.
