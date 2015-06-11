#!/bin/bash

# chkconfig: 2345 20 80
# description: 

#Profile under which daemon operates.
PROFILE=development

#Name of daemon jar file.
DAEMON=dtbprofiler

# Directory where the application resides
DIR=/home/george/Downloads/Mocha_Wallet_Profiler

# Application JAR file (may be in a sub folder)
JAR_FILE=$DIR/target/$DAEMON.jar:$DIR/target/dependency-jars/*

# The PID file location
PID=/var/run/hub$DAEMON-$PROFILE.pid

# JVM in use
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
#JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.3.x86_64

# JSVC path
JSVC=/usr/bin/jsvc

# Name of the daemon (will be displayed during start/stop)
NAME=$PROFILE" Mocha_Wallet Poller Daemon "
# Main class implementing the Daemon interface
MAIN_CLASS=com.cellulant.profiler.Mocha_profilerDaemon
# Logging properties file

MIN_MEMORY=-Xms256m
MAX_MEMORY=-Xmx512m

# Set to 1 to enable debugging
DEBUG=1
DEBUG_OUTPUT_FILE=/var/log/applications/kq/Dtboutput.txt
DEBUG_ERROR_FILE=/var/log/applications/kq/Dtberror.txt

#Configs Folder location...
CONFIGLOCATION=$DIR

# DO NOT EDIT BELOW THIS LINE

usage() {
	echo $"Usage: $0 {start|stop|restart} "
	return 0
}

start() {
    echo $"Starting the $NAME..."

    cd $DIR

    if [[ $DEBUG -eq 1 ]]; then
        $JSVC -debug -pidfile $PID -home $JAVA_HOME  -outfile $DEBUG_OUTPUT_FILE -errfile $DEBUG_ERROR_FILE $MIN_MEMORY $MAX_MEMORY -cp $JAR_FILE $MAIN_CLASS $PROFILE  $CONFIGLOCATION
    else
        $JSVC -pidfile $PID -home $JAVA_HOME  $MIN_MEMORY $MAX_MEMORY -cp $JAR_FILE $MAIN_CLASS $PROFILE $CONFIGLOCATION
    fi

    # Check status of the application
    if [[ $? -eq 0 ]]; then
        echo $"$NAME Successfully STARTED"
        echo
        return 0
    else
        echo $"Failed to START $NAME"
        echo
        return 1
    fi
}

stop() {
    echo $"Stopping the $NAME..."

    cd $DIR

    if [[ $DEBUG -eq 1 ]]; then
        $JSVC -debug -stop -home $JAVA_HOME -pidfile $PID  -outfile $DEBUG_OUTPUT_FILE -errfile $DEBUG_ERROR_FILE $MIN_MEMORY $MAX_MEMORY -cp $JAR_FILE $MAIN_CLASS 
    else
        $JSVC -stop -home $JAVA_HOME -pidfile $PID  $MIN_MEMORY $MAX_MEMORY -cp $JAR_FILE $MAIN_CLASS
    fi

    if [[ -e $PID ]]; then
        # Kill the process (so we are sure that it has stopped)
        KPID=`cat $PID`
        KPID1=$(($KPID - 1))
        kill -9 $KPID $KPID1
        rm -f $PID
    fi

    # Check status of the application
    if [[ $? -eq 0 ]]; then
        echo $"$NAME Successfully STOPPED"
        echo
        return 0
    else
        echo $"Failed to STOP $NAME"
        echo
        return 1
    fi
    echo
}

restart() {
    cd $DIR

    stop

    sleep 10

    if [[ -e $PID ]]; then
        # Kill the process (so we are sure that it has stopped)
        KPID=`cat $PID`
        KPID1=$(($KPID - 1))
        kill -9 $KPID $KPID1
        rm -f $PID
    fi

    sleep 2

    start

    # Check status of the application
    if [[ $? -eq 0 ]]; then
        echo $"$NAME Successfully RESTARTED"
        echo
        return 0
    else
        echo $"Failed to RESTART $NAME"
        echo
        return 1
    fi

    echo
}

case "$1" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        restart
    ;;
    *)
        echo $"Usage: $0 {start|stop|restart}"
        exit 1

esac

exit $?



