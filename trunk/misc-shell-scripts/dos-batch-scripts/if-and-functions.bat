@echo off

set debug_output=1

call:logDbg das is ne coole message




echo.&pause&goto:eof

::--------------------------------------------------------
::-- Function section starts below here
::--------------------------------------------------------

:logDbg

    if %debug_output%'==1' echo.%*

goto:eof


::--------------------------------------------------------
::-- Function section ends here
::--------------------------------------------------------