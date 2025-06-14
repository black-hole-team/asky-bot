#!/bin/bash

if [ -n "$JAVA_HOME" ]; then
    JAVA=$JAVA_HOME/bin/java
else
    if [ -n "$JRE_HOME" ]; then
        JAVA=$JRE_HOME/bin/java
    else
        echo "The JAVA_HOME environment variable is not defined. Use java from the PATH variable"
        JAVA=java
    fi
fi

CP=$PWD/lib/*:$CP

$JAVA -classpath $CP -Dlog4j2.configurationFile=$PWD/config/log4j2.xml -DhomePath=$PWD team.blackhole.bot.asky.Application