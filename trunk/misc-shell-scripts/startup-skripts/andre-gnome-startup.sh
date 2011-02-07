#!/bin/bash

function main() {
    date

    local DEBUG_MODE=0
    local START_APPS=1


    ########################################
    # start the programs you want and wait 
    # for their windows to get visible
    ########################################


    if [ "$DEBUG_MODE" == "1" ] ; then
        echo DEBUG_MODE is on !

    else

        if [ "$START_APPS" == "1" ] ; then
            firefox &
            skype &
            empathy &
            xterm -bg black -fg white -fn 7x13 -e /bin/zsh &
            transmission &
            liferea &
            thunderbird &
            gmpc &

            ls -1 /var/log/auth.log \
                  /var/log/boot.log \
                  /var/log/daemon.log \
                  /var/log/kern.log \
                  /var/log/messages \
                  /var/log/syslog \
            | while read line; do
                xterm -title "$line" -geometry "120x20" -e tail -fn20 "$line" &
            done
            
            while [ 1 ] ; do
                sleep 1

                # wait for gnome panel to initialize...
                wmctrl -l -p -G | grep -iq 'x-nautilus-desktop' || continue
                wmctrl -l -p -G | grep -iq 'Top Expanded Edge Panel' || continue
                wmctrl -l -p -G | grep -iq 'Bottom Expanded Edge Panel' || continue
          

                wmctrl -l -p -G | grep -iq firefox        || continue
                wmctrl -l -p -G | grep -iq skype          || continue
                wmctrl -l -p -G | grep -iq "contact list" || continue
                wmctrl -l -p -G | grep -iq transmission   || continue
                wmctrl -l -p -G | grep -iq liferea        || continue
                wmctrl -l -p -G | grep -iq thunderbird    || continue
                wmctrl -l -p -G | grep -iq 'andre@buenosaires: ~' || continue
               
              # commented because title different when mpc is not stopped:
              # wmctrl -l -p -G | grep -iq 'gmpc'                 || continue
                
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q firefox      || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q skype        || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q empathy      || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q xterm        || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q transmission || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q liferea      || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q thunderbird  || continue
                ps auxwww | grep -v grep | grep "$(whoami)" | grep -q gmpc         || continue

                break
            done
       fi
    fi


    #################################
    # log the current windows' state
    #################################
    cat ~/scripts/wmctrl-header.txt
    wmctrl -l -p -G




    ########################################
    # parse wmctrl output:
    ########################################

    wmctrl -l -p -G \
    | while read line; do
        
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
            xywh="1650 50   252,495"


        elif (echo "$proccmd" | grep -q "empathy"); then 
            echo
            echo " -------    found emphaty instance! ------------------"
            xywh="1370 50   257  495"
        

        elif (echo "$proccmd" | grep -q "transmission"); then 
            echo
            echo " -------    found transmission instance! ------------------"
            xywh="1370,594,533,409"
        

        elif (echo "$proccmd" | grep -q "/usr/lib/thunderbird" && echo "$title" | grep -qi "thunderbird"); then 
            echo
            echo " -------    found thunderbird instance! ------------------"
            xywh=" 17 50 1327,953"


        elif (echo "$proccmd" | grep -q "liferea"); then 
            echo
            echo " -------    found liferea instance! ------------------"
            desktop=1
            xywh="1059,50,844,953"
        

        elif (echo "$proccmd" | grep -q "firefox"); then 
            echo
            echo " -------    found firefox instance! ------------------"
            desktop=1
            xywh="  17,50,1014,953 "
        
        
        elif (echo "$proccmd" | grep -q "xterm" && echo "$title" | grep -qE "^andre@buenosaires: ~$"); then 
            echo
            echo " -------    found xterm instance! ------------------"
            xywh="17,50,844,953"
            desktop=2


        elif (echo "$proccmd" | grep -q "nautilus" && echo "$title" | grep -q "File Browser"); then 
            echo
            echo " -------    found nautilus file browser instance! ------------------"
            xywh="887,50,1016,953"
            desktop=2


        elif (echo "$proccmd" | grep -q "gmpc"); then 
            echo
            echo " -------    found gmpc instance! ------------------"
            xywh=",888,50,1018,953"
            desktop=2

        else
            echo "- uninteresting: $simpleLine"
            continue
        fi

        ######################################################
        # parse coords with sed:
        #
        # the pattern will look for the first 4 integers in a string and
        # there may be any non-number characters to separate two integers
        # group1=x group2=y, group3=height, group4=width
        ######################################################

        sedexpr="s/"
        sedexpr="${sedexpr}[^0-9]*\([0-9]*\)"
        sedexpr="${sedexpr}[^0-9]*\([0-9]*\)"
        sedexpr="${sedexpr}[^0-9]*\([0-9]*\)"
        sedexpr="${sedexpr}[^0-9]*\([0-9]*\)"
        sedexpr="${sedexpr}.*/"

        if [ -z "$( echo "$xywh"  | sed  "${sedexpr}nasen/" )" ] ; then
            echo "WARNING: did not match: $xywh"
            echo "sedexpr:   $sedexpr"
            exit 231
        fi
            
#       echo "simpleLine:    $simpleLine"
#       echo "windowId:      $windowId"
#       echo "procId:        $procId"
#       echo "psOutput:      $psOutput"
#       echo "procOwner:     $procOwner"
#       echo "title :        $title"
#       echo "proccmd:       $proccmd"
#       echo "x      :       $x"
#       echo "y      :       $y"
#       echo "width  :       $width"
#       echo "height :       $height"

        newX=$(echo $xywh  | sed "${sedexpr}\1/" )
        newY=$(echo $xywh  | sed "${sedexpr}\2/" )
        newW=$(echo $xywh  | sed "${sedexpr}\3/" )
        newH=$(echo $xywh  | sed "${sedexpr}\4/" )
        newXYWH="$newX,$newY,$newW,$newH"

        echo "currentXywh :   $currentXywh"
        echo "newXYWH  :      $newXYWH"
        echo "will now exec:  wmctrl -i -r $windowId -e '0,$newXYWH'"

        if [ $desktop -ne 0 ] ; then
            echo "will now exec:  wmctrl -i -r $windowId -t $desktop"
        fi

        ##########################################
        # do not exec anything when debug mode:
        ##########################################

        if [ "$DEBUG_MODE" == "1" ] ; then
            echo dbg
            echo
            continue
        fi

        if [ $desktop -ne 0 ] ; then
            wmctrl -i -r $windowId -t $desktop
            echo success: $?
        fi

        wmctrl -i -r $windowId -e "0,$newXYWH"
        echo success: $?
        
        echo
    done



    ################################################
    ################################################
    ######      show logfiles on desktop 4:   ######
    ################################################
    ################################################

    wmctrl -l -p -G \
    | grep /var/log \
    | grep -v cat \
    | while read window; do
        echo "$window"
        simpleLine=$(echo $window | tr -s " ")
        procId=$(echo $simpleLine | cut -d " " -f 3)
        windowId=$(echo $simpleLine | cut -d " " -f 1)
        psOutput=$(ps auxww | grep -v grep | grep $procId | tr -s ' ')
        procOwner=$(echo $psOutput | cut -d " " -f 1)
        title=$(echo $simpleLine | cut -d " " -f 9-)

#        echo "simpleLine:    $simpleLine"
#        echo "windowId:      $windowId"
#        echo "procId:        $procId"
#        echo "psOutput:      $psOutput"
#        echo "procOwner:     $procOwner"
#        echo "title :        $title"
#        echo "window:        $window"

        case $title in
            /var/log/kern.log)
                xywh=" 235  160  724  264 "
            ;;
            /var/log/daemon.log)
                xywh="235  452  724  264 "
            ;;
            /var/log/auth.log)
                xywh="235  744  724  264"
            ;;
            /var/log/boot.log)
                xywh=" 967  160  724  264 "
            ;;
            /var/log/syslog)
                xywh=" 967  452  724  264 "
            ;;
            /var/log/messages)
                xywh=" 967  744  724  264"
            ;;
        esac

        x=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f1)
        y=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f2)
        w=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f3)
        h=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f4)

        # insert some offsets:
        y=$(echo "$y - 110" | bc)
        x=$(echo "$x + 120" | bc)

        newXYWH="$x,$y,$w,$h"
        echo;echo wmctrl -i -r $windowId -e "0,$newXYWH"
        wmctrl -i -r $windowId -e "0,$newXYWH"
        wmctrl -i -r $windowId -t 3
        echo;echo
    done

    conky &
}

rm -f /tmp/startup-desktop.sh.log

main 2>&1 | tee /tmp/startup-desktop.sh.log
