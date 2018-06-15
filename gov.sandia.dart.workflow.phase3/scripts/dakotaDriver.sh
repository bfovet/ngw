#!/bin/sh

export WFLIB=`dirname $BASH_SOURCE`
PATH=${WFLIB}:${PATH}

if [ -f ${WFLIB}/environment ] ; then
. ${WFLIB}/environment
fi 

java -XX:CICompilerCount=2 -XX:+ReduceSignalUsage -XX:+DisableAttachMechanism -XX:+UseSerialGC -cp ${WFLIB}/\*:${WFLIB}/plugins/\* gov.sandia.dart.workflow.runtime.Main $*
exit $?
