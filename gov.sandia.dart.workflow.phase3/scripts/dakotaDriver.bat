@echo off

setlocal
set WFLIB=%~dp0
set PATH=%WFLIB%:%PATH%

java -XX:CICompilerCount=2 -XX:+ReduceSignalUsage -XX:+DisableAttachMechanism -XX:+UseSerialGC -cp "%WFLIB%\*";"%WFLIB%\plugins\*" gov.sandia.dart.workflow.runtime.Main %1 %2 %3 %4 %5 %6 %7 >STDOUT 2>STDERR
set exitcode=%ERRORLEVEL%
echo %exitcode%