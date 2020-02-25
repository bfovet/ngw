#!/bin/bash

cygwin=false
darwin=false
os400=false
hpux=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
HP-UX*) hpux=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=$(dirname "$PRG")

export WFLIB=${WFLIB:-$PRGDIR}
PATH=${WFLIB}:${PATH}

# If there is an "environment" script in the installation, run it
if [ -f ${WFLIB}/environment ] ; then
  . ${WFLIB}/environment
fi

# Set JAVA_HOME to point to a Java 8 (or later) installation
if [ -z "${JAVA_HOME}" ]; then
   JAVA=java
else
   JAVA=${JAVA_HOME}/bin/java
fi

JAVA_WFLIB="${WFLIB}/*:${WFLIB}/lib/*:${WFLIB}/plugins/*"

if $cygwin; then
    WINLIB=$(cygpath --windows $WFLIB)
    JAVA_WFLIB="${WINLIB}\*;${WINLIB}\lib\*;${WINLIB}\plugins\*"
fi

${JAVA} -XX:CICompilerCount=2 -XX:+ReduceSignalUsage -XX:+DisableAttachMechanism -XX:+UseSerialGC -cp "${JAVA_WFLIB}" gov.sandia.dart.workflow.runtime.Main $*
exit $?
