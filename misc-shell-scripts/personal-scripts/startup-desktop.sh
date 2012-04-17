#!/bin/bash



# $1 : pattern
# $2 : grepOptions
function isWindowVisible() {
    pattern=$1
    grepOptions=$2
    
    if [ $# -lt 1 ] ; then
        echo "must provide a pattern!"
        exit 1
    fi

    if ( wmctrl -l -p -G | grep -q $grepOptions "$pattern" ) ; then
        echo "window is visible: '$pattern'"
        return 0
    fi

    echo "window is NOT visible: '$pattern'"
    return 1
}


function isProcessStarted() {
    pattern=$1
    grepOptions=$2

    if [ $# -lt 1 ] ; then
        echo "must provide a pattern!"
        exit 1
    fi

    if ( ps auxwww | grep -v grep | grep "$(whoami)" | grep -q $grepOptions "$pattern" ) ; then
        echo "process is started: '$pattern'"
        return 0
    fi

    echo "process is NOT started: '$pattern'"
    return 1
}


function printWmctrlState() {
    echo "current wmctrl-header output:"
    cat ~/scripts/wmctrl-header.txt
    wmctrl -l -p -G
}

function startProcesses() {
        echo "will now start apps..."
        firefox &
        skype &
        empathy &
        xterm -bg black -fg white -fn 7x13 -e /bin/zsh &
        transmission &
        liferea &
        thunderbird &
        gmpc &
}

function waitForProcessGuis() {
    while [ 1 ] 
    do
        echo "waiting for initialize..."
        sleep 2
        echo 
        echo "***********************************************************"
        echo 
        echo "will now lookup if all apps were loaded: `date '+%H.%M.%S'` "
        echo
        echo "wait for the started apps' processes to start..."
        echo
        
        isProcessStarted firefox      -i || continue
        isProcessStarted skype        -i || continue
        isProcessStarted empathy      -i || continue
        isProcessStarted xterm        -i || continue
        isProcessStarted transmission -i || continue
        isProcessStarted liferea      -i || continue
        isProcessStarted thunderbird  -i || continue
        isProcessStarted gmpc         -i || continue
        
        echo
        echo "wait for the started apps' guis to become visible..."
        printWmctrlState 
        echo
        
        # gmpc is not being tested here, because it's title differs when playing during login 
        isWindowVisible 'x-nautilus-desktop'                                 -i    || continue
        isWindowVisible '(Oberes Kanten-Panel|Top Expanded Edge Panel)'      -iE   || continue
        isWindowVisible '(Unteres Kanten-Panel|Bottom Expanded Edge Panel)'  -iE   || continue
        isWindowVisible "$(whoami)@$(hostname): ~"                           -i    || continue
        isWindowVisible firefox                                              -i    || continue
        isWindowVisible skype                                                -i    || continue
        isWindowVisible "(contact list|Kontaktliste)"                        -iE   || continue
        isWindowVisible transmission                                         -i    || continue
        isWindowVisible liferea                                              -i    || continue
        isWindowVisible thunderbird                                          -i    || continue

        break
    done
    
    echo
    echo "all apps are started and visible!"
}


function main() {
    date '+%Y-%m-%d_%H.%M.%S'

    local DEBUG_MODE=0
    local START_APPS=1


    ########################################
    # start the programs you want and wait 
    # for their windows to get visible
    ########################################


    if [ "$DEBUG_MODE" != "0" ] ; then
        echo DEBUG_MODE is on !
        echo will not start apps!
    fi

    if [ "$START_APPS" == "1" -a "$DEBUG_MODE" == "0" ] ; then
        startProcesses
        waitForProcessGuis
    fi


    #################################
    # log the current windows' state
    #################################
    printWmctrlState



    ########################################
    # parse wmctrl output:
    ########################################

    wmctrl -l -p -G | while read line
    do
        
        DEBUG=0
       
        simpleLine=$(echo $line | tr -s " ")
        windowId=$(echo $simpleLine | cut -d " " -f 1)
        procId=$(echo $simpleLine | cut -d " " -f 3)
        psOutput=$(ps auxww | grep -v grep | grep $procId | tr -s ' ')
        procOwner=$(echo $psOutput | cut -d " " -f 1)
        title=$(echo $simpleLine | cut -d " " -f 9-)
        proccmd=$(echo $psOutput | cut -d " " -f 11-)
        x="$(echo $simpleLine | cut -d ' ' -f 4)"
        y="$(echo $simpleLine | cut -d ' ' -f 5)"
        width="$(echo $simpleLine | cut -d ' ' -f 6)"
        height="$(echo $simpleLine | cut -d ' ' -f 7)"
        
        # you may modify these coords here:
        ########################
        
        currentXywh="$x,$y,$width,$height"

        if [ "$procOwner" != "$(whoami)" ] ; then
            echo "WARNING: process owner $procOwner not equal to executor $(whoami)"
            continue
        fi

        desktop=0

        #########################
        # determine windows of current wmctrl outputline and map coords to them
        #########################

        if (echo "$proccmd" | grep -q "skype"); then 
            echo
            echo " -------    found skype instance! ------------------"
            xywh="1645 98   266  495 "


        elif (echo "$proccmd" | grep -q "empathy"); then 
            echo;echo " -------    found emphaty instance! ------------------"
            xywh="1335 98   286  495"
        

        elif (echo "$proccmd" | grep -q "transmission"); then 
            echo " -------    found transmission instance! ------------------"
            xywh="1335 642  576  409"
        

        elif (echo "$proccmd" | grep -q "/usr/lib/thunderbird" && echo "$title" | grep -qi "thunderbird"); then 
            echo " -------    found thunderbird instance! ------------------"
            xywh="25   98   1286 953"


        elif (echo "$proccmd" | grep -q "liferea"); then 
            echo " -------    found liferea instance! ------------------"
            desktop=1
            xywh="1067 98   844  953"
        

        elif (echo "$proccmd" | grep -q "firefox"); then 
            echo " -------    found firefox instance! ------------------"
            desktop=1
            xywh="25   98   1014 953"
        
        
        elif (echo "$proccmd" | grep -q "xterm" && echo "$title" | grep -qE "^andre@buenosaires: ~$"); then 
            echo " -------    found xterm instance! ------------------"
            xywh="23   98   844  953 "
            desktop=2


#        elif (echo "$proccmd" | grep -q "nautilus" && echo "$title" | grep -q "File Browser"); then 
#            echo " -------    found nautilus file browser instance! ------------------"
#            xywh=""
#            desktop=2


        elif (echo "$proccmd" | grep -q "gmpc"); then 
            echo " -------    found gmpc instance! ------------------"
            xywh=" 892  98   1019 953 "
            desktop=2

        else
            continue
        fi

        ######################################################
        # parse coords with sed:
        #
        # the pattern will look for the first 4 integers in a string and
        # there may be any non-number characters to separate two integers
        # group1=x group2=y, group3=height, group4=width
        ######################################################

        #               \([0-9]*\)       \([0-9]*\)       \([0-9]*\)       \([0-9]*\)  
        sedexpr="[^0-9]*\([0-9]*\)[^0-9]*\([0-9]*\)[^0-9]*\([0-9]*\)[^0-9]*\([0-9]*\).*"
        newXYWH="$(echo $xywh | sed "s/${sedexpr}/\1,\2,\3,\4/")"



        # XXX hack START: apply offset to x and y: XXX

        hackX="$(echo $xywh | sed "s/$sedexpr/\1/")"
        hackY="$(echo $xywh | sed "s/$sedexpr/\2/")"
        hackWH="$(echo $xywh | sed "s/$sedexpr/\3,\4/")"
        hackX=$(echo "$hackX - 10" | bc)
        hackY=$(echo "$hackY - 60" | bc)
        hackXYWH="$hackX,$hackY,$hackWH"
        newXYWH=$hackXYWH

        # XXX hack END: apply offset to x and y: XXX



        echo "newXYWH  :      $newXYWH"
        echo "will now exec:  wmctrl -i -r $windowId -e '0,$newXYWH'"
        if [ $desktop -ne 0 ] ; then
            echo "will now exec:  wmctrl -i -r $windowId -t $desktop"
        fi

        ##########################################
        # do not exec anything when debug mode:
        ##########################################

        if [ "$DEBUG_MODE" == "1" ] ; then
            echo "debug mode is on"
            echo
            continue
        fi

        if [ $desktop -ne 0 ] ; then
            echo "executing: wmctrl -i -r $windowId -t $desktop"
            wmctrl -i -r $windowId -t $desktop || exit 222
        fi

        echo "executing: wmctrl -i -r $windowId -e '0,$newXYWH'"
        wmctrl -i -r $windowId -e "0,$newXYWH" || exit 333
        
        echo
    done

    echo "will now start conky..."
    conky &
    sleep 2

    exit 0
}

rm -f /tmp/startup-desktop.sh.log

main 2>&1 | tee /tmp/startup-desktop.sh.log
