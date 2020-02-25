@echo off
setlocal

set WFLIB=%~dp0
set PATH=%WFLIB%;%PATH%

java -XX:CICompilerCount=2 -XX:+ReduceSignalUsage -XX:+DisableAttachMechanism -XX:+UseSerialGC -cp %WFLIB%\*;%WFLIB%\lib\*;%WFLIB%\plugins\* gov.sandia.dart.workflow.runtime.Main %*


