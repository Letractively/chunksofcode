#/bin/bash

ls -1 /var/log/auth.log \
      /var/log/boot.log \
      /var/log/daemon.log \
      /var/log/kern.log \
      /var/log/messages \
      /var/log/syslog \
| while read line do
    xterm -title "$line" -geometry "120x20" -e tail -fn20 "$line" &
done

sleep 2 # wait some time to give the windows time to display

wmctrl -l -p -G \
| grep /var/log \
| while read window do

        simpleLine=$(echo $window | tr -s " ")
        procId=$(echo $simpleLine | cut -d " " -f 3)
        windowId=$(echo $simpleLine | cut -d " " -f 1)
        psOutput=$(ps auxww | grep -v grep | grep $procId | tr -s ' ')
        procOwner=$(echo $psOutput | cut -d " " -f 1)
        title=$(echo $simpleLine | cut -d " " -f 9-)

#        echo "window:        $window"
#        echo "simpleLine:    $simpleLine"
#        echo "procId:        $procId"
#        echo "windowId:      $windowId"
#        echo "psOutput:      $psOutput"
#        echo "procOwner:     $procOwner"
#        echo "title :        $title"

        case $title in
            /var/log/kern.log)
                xywh=" 235  160  724  264 " ;;
            /var/log/daemon.log)
                xywh="235  452  724  264 " ;;
            /var/log/auth.log)
                xywh="235  744  724  264" ;;
            /var/log/boot.log)
                xywh=" 967  160  724  264 " ;;
            /var/log/syslog)
                xywh=" 967  452  724  264 " ;;
            /var/log/messages)
                xywh=" 967  744  724  264" ;;
        esac

        x=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f1)
        y=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f2)
        w=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f3)
        h=$(echo "$xywh" | grep -Eo "\<.*\>" | tr -s " " | cut -d" " -f4)

        # apply some offsets:
        y=$(echo "$y - 110" | bc)
        x=$(echo "$x + 120" | bc)

        newXYWH="$x,$y,$w,$h"

        echo wmctrl -i -r $windowId -e "0,$newXYWH"
             wmctrl -i -r $windowId -e "0,$newXYWH"
        
        echo wmctrl -i -r $windowId -t 4
             wmctrl -i -r $windowId -t 4

        echo
done

