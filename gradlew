#!/bin/sh

# Gradle startup script for Unix

DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"
GRADLE_HOME=`dirname "$0"`/gradle

# Attempt to set APP_HOME
APP_HOME=`dirname "$0"`

# Add default JVM options
if [ -n "$JAVA_OPTS" ] ; then
    DEFAULT_JVM_OPTS="$DEFAULT_JVM_OPTS $JAVA_OPTS"
fi

# Determine the Java command to use
if [ -z "$JAVA_HOME" ] ; then
    JAVACMD="java"
else
    JAVACMD="$JAVA_HOME/bin/java"
fi

# Run Gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
