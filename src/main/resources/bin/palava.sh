#!/bin/sh

# change current working directory to application root
# script has to be under bin/
cd $(dirname $0)/..

# state file
if [ -z "$APPLICATION_STATE_FILE" ]; then
    APPLICATION_STATE_FILE=run/application.state
fi

# pid file
if [ -z "$APPLICATION_PID_FILE" ]; then
    APPLICATION_PID_FILE=run/application.pid
fi

echo_n() {
	if [ "$(echo -n)" = "$(echo)" ]; then
		echo -n $*
	else
		echo $*
	fi
}

palava_start() {
    echo_n "Starting palava framework...   "

    palava_status
    if [ $? -eq 0 ]; then
        echo "FAILED"
        echo "palava framework is already running" >&2
        return 1
    fi

    # check vm arguments
    if [ -z "$JVM_ARGS" ]; then
        JVM_ARGS="-Dlog4j.configuration=file:conf/log4j.xml -Xms256m -Xmx1024m -cp lib/*"
    fi

    # configuration file
    if [ -z "$APPLICATION_CONFIG" ]; then
        APPLICATION_CONFIG=conf/application.properties
    fi

    # computed arguments
    APPLICATION_ARGS="--config $APPLICATION_CONFIG --state-file $APPLICATION_STATE_FILE"

    # where is java
    if [ -z "$JAVA_HOME" ] && [ -z "$JRE_HOME" ]; then
        echo "FAILED"
        echo "No Java available, set JAVA_HOME or JRE_HOME" >&2
        return 1
    fi
    if [ ! -z "$JAVA_HOME" ]; then
        JAVA=$JAVA_HOME/bin/java
    else
        JAVA=$JRE_HOME/bin/java
    fi

    # check that our state and pid file is writeable
    mkdir -p $(dirname $APPLICATION_STATE_FILE)
    mkdir -p $(dirname $APPLICATION_PID_FILE)
    if [ ! -w $APPLICATION_STATE_FILE ]; then
        echo "FAILED"
        echo "State file [$APPLICATION_STATE_FILE] not writeable" >&2
        return 1
    fi
    if [ ! -w $APPLICATION_PID_FILE ]; then
        echo "FAILED"
        echo "PID file [$APPLICATION_PID_FILE] not writeable" >&2
        return 1
    fi

    # check that nohup is installed
    which nohup >/dev/null 2>/dev/null
    if [ $? -ne 0 ]; then
        echo "FAILED"
        echo "'nohup' not in PATH" >&2
        return 1
    fi

    # set STARTING state
    echo "STARTING" > $APPLICATION_STATE_FILE

    # start java in the background
    nohup $JAVA $JVM_ARGS de.cosmocode.palava.core.Main $APPLICATION_ARGS 2>&1 >logs/out.log
    PID=$!

    # save pid
    echo $PID > $APPLICATION_PID_FILE

    while [ true ]; do
        # current state
        STATE=$(cat $APPLICATION_STATE_FILE)

        # pid alive?
        ps -p $PID >/dev/null 2>/dev/null
        ACTIVE=$?

        # still starting...
        if [ $ACTIVE -eq 0 ] && [ "$STATE" = "STARTING" ]; then
            continue
        fi

        # successfully started
        if [ $ACTIVE -eq 0 ] && [ "$STATE" = "RUNNING" ]; then
            echo "done"
            return 0
        fi

        # something is horribly broken
        echo "FAILED"
        echo "Application can not boot" >&2
        return 1
    done
}

palava_stop() {
    echo_n "Stopping palava framework...   "

    palava_status
    if [ $? -ne 0 ]; then
        echo "FAILED"
        echo "palava framework does not run" >&2
        return 1
    fi

    # send shutdown signal to java
    PID=$(cat $APPLICATION_PID_FILE)
    kill $PID

    # wait until
    while [ true ]; do
        palava_status
        [ $? -ne 0 ] && break
    done

    echo "done"
    return 0
}

palava_kill() {
    echo_n "Killing palava framework...   "

    palava_status
    if [ $? -ne 0 ]; then
        echo "FAILED"
        echo "palava framework does not run" >&2
        return 1
    fi

    # send shutdown signal to java
    PID=$(cat $APPLICATION_PID_FILE)
    kill -9 $PID

    echo "done"
    return 0
}

palava_status() {
    if [ ! -r $APPLICATION_PID_FILE ]; then
        # nothing runs with this configuration
        return 1
    fi

    # current pid
    PID=$(cat $APPLICATION_PID_FILE)

    # check pid
    ps -p $PID 2>&1 >/dev/null
    return $?
}

palava_usage() {
    echo "Usage: $0 start|stop|kill|restart|status"
}


case "$1" in
    "start")
        palava_start
        exit $?
        ;;

    "stop")
        palava_stop
        exit $?
        ;;

    "kill")
        palava_kill
        exit $?
        ;;

    "restart")
        palava_stop && palava_start
        exit $?
        ;;

    "status")
        palava_status
        if [ $? -eq 0 ]; then
            echo "palava framework is running"
            exit 0
        else
            echo "palava framework is not running"
            exit 1
        fi
        ;;

    "help")
        palava_usage
        exit 0
        ;;

    *)
        palava_usage >&2
        exit 1
esac
