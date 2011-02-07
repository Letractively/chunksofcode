#!/bin/bash

alltray --no-alltray --notray --show --large_icons transmission                                --geometry 521x300+30+765 &

#alltray --no-alltray --show --large_icons xterm -foreground white -background black -fn "7x14" --geometry 564x900+430+100 &
alltray --no-alltray --show --large_icons xterm -foreground white -background black -fn "7x14" --geometry 564x900+280+35 &
sleep 1;

#alltray --no-alltray --notray --show --large_icons nautilus /                                  --geometry 583x900+280+35 &
alltray --no-alltray --notray --show --large_icons nautilus /                                  --geometry 583x900+430+100 &
sleep 2;



alltray --no-alltray --show --large_icons firefox -p default                                   --geometry 1043x900+580+165 &
