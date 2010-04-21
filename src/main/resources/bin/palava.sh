#!/bin/sh
#
# palava - a java-php-bridge
# Copyright (C) 2007-2010  CosmoCode GmbH
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#

# change current working directory to application root
# script has to be under bin/
cd $(dirname $0)/..

# load global environment variables
if [ -r /etc/environment ]; then
    . /etc/environment
fi

# look for a palava.conf file to include
CONFIG_FILE=conf/palava.conf

# load it
if [ -r "$CONFIG_FILE" ]; then
    . $CONFIG_FILE
fi

# environment configuration file?
if [ ! -z "$PALAVA_ENVIRONMENT" ]; then
    ENV_CONFIG_FILE="conf/palava/$PALAVA_ENVIRONMENT.conf"
    if [ -r "$ENV_CONFIG_FILE" ]; then
        . $ENV_CONFIG_FILE
    fi
fi

if [ "$(id -u)" -eq 0 ]; then
    if [ ! -z "$PALAVA_USER" ]; then
        # switch user
        su $PALAVA_USER --command "$(pwd)/bin/palava.sh $*"
        exit $?
    else
        echo "Running as root and no user is configured! aborting!" >&2
        exit 1
    fi
fi

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
        echo -n "$*"
    else
        echo "$*"
    fi
}

palava_internal_start() {
    echo_n "Starting framework..."

    palava_status
    if [ $? -eq 0 ]; then
        echo "FAILED"
        echo "Framework is already running" >&2
        return 1
    fi

    # check classpath
    if [ -z "$CLASSPATH" ]; then
        CLASSPATH="$(dirname $0)/../conf/:$(echo $(ls lib/*.jar) | sed 's/ /:/g')"
    fi

    # which log4j config
    if [ ! -z "$PALAVA_ENVIRONMENT" ] && [ -r conf/log4j/$PALAVA_ENVIRONMENT.xml ]; then
        LOG4J_CONFIG=conf/log4j/$PALAVA_ENVIRONMENT.xml
    else
        LOG4J_CONFIG=conf/log4j.xml
    fi

    # check vm arguments
    if [ -z "$JVM_ARGS" ]; then
        JVM_ARGS="\
			-Xms256m \
			-Xmx512m \
			-XX:-OmitStackTraceInFastThrow \
			-Dlog4j.configuration=file:$LOG4J_CONFIG \
			-Dfile.encoding=UTF-8 \
			-Djava.awt.headless=true \
			-enableassertions \
			-classpath $CLASSPATH"
    fi

    # check for extra vm arguments
	if [ ! -z "$EXTRA_JVM_ARGS" ]; then
		JVM_ARGS="$JVM_ARGS $EXTRA_JVM_ARGS"
	fi

    # jvm args file?
    if [ -r conf/jvm.options ]; then
        JVM_ARGS="\
            -classpath $CLASSPATH \
            -Dlog4j.configuration=file:$LOG4J_CONFIG \
            -Dfile.encoding=UTF-8"
        for line in $(cat conf/jvm.options); do
            JVM_ARGS="$JVM_ARGS $line"
        done
        if [ ! -z "$PALAVA_ENVIRONMENT" ] && [ -r conf/jvm/$PALAVA_ENVIRONMENT.options ]; then
            for line in $(cat conf/jvm/$PALAVA_ENVIRONMENT.options); do
                JVM_ARGS="$JVM_ARGS $line"
            done
        fi
    fi

    # configuration file
    if [ -z "$APPLICATION_CONFIG" ]; then
        APPLICATION_CONFIG=conf/application.properties
    fi
    if [ ! -z "$PALAVA_ENVIRONMENT" ] && [ -r conf/application/$PALAVA_ENVIRONMENT.properties ]; then
        APPLICATION_ENV_CONFIG=conf/application/$PALAVA_ENVIRONMENT.properties
    else
        APPLICATION_ENV_CONFIG=
    fi

    # computed arguments
    APPLICATION_ARGS=" --state-file $APPLICATION_STATE_FILE --no-auto-shutdown $APPLICATION_CONFIG $APPLICATION_ENV_CONFIG"

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

    if [ ! -f "$JAVA" ]; then
    	echo "FAILED"
    	echo "Java interpreter [$JAVA] does not exist" >&2
    	return 1
    fi

    mkdir -p $(dirname $APPLICATION_STATE_FILE)
    mkdir -p $(dirname $APPLICATION_PID_FILE)

    # check write access
    touch $APPLICATION_STATE_FILE 2>&1 1>/dev/null

    if [ $? -ne 0 ]; then
        echo "FAILED"
        echo "State file [$APPLICATION_STATE_FILE] not writeable" >&2
        return 1
    fi

    # check write access
    echo 0 > $APPLICATION_PID_FILE 2>/dev/null

    if [ $? -ne 0 ]; then
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

    # prepare log directory
    mkdir -p logs

    # start java in the background
    COMMAND="$JAVA $JVM_ARGS de.cosmocode.palava.core.Main $APPLICATION_ARGS"
    nohup $COMMAND > logs/stdout.log 2> logs/stderr.log &
    PID=$!

    # save pid
    echo $PID > $APPLICATION_PID_FILE

    return 0
}

palava_start() {

	palava_internal_start
	RESULT=$?

	if [ 0 -ne $RESULT ]; then
		return $RESULT
	fi

    while [ true ]; do
        echo_n "."
        sleep 1

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

        # anything else is considered an error
        echo "FAILED"
        echo "Application boot failed. Showing logs/stderr.log:" >&2
        cat logs/stderr.log
        return 1
    done
}

palava_quickstart() {
	palava_internal_start
	RESULT=$?

	if [ 0 -ne $RESULT ]; then
		return $RESULT
	fi

	echo  "done"
	return 0
}

palava_stop() {
    echo_n "Stopping framework..."

    palava_status
    if [ $? -ne 0 ]; then
        echo "done"
        echo "Framework did not run" >&2
        return 0
    fi

    # send shutdown signal to java
    PID=$(cat $APPLICATION_PID_FILE)
    kill $PID

    # wait until
    while [ true ]; do
    	echo_n "."
    	sleep 1
        palava_status
        [ $? -ne 0 ] && break
    done

    echo "done"
    return 0
}

palava_kill() {
    echo_n "Killing framework... "

    palava_status
    if [ $? -ne 0 ]; then
        echo "FAILED"
        echo "Framework is not running" >&2
        return 0
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
    echo "Usage: $0 <start|quickstart|stop|kill|restart|force-restart|status>"
}


case "$1" in
    "start")
        palava_start
        exit $?
        ;;

	"quickstart")
		palava_quickstart
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

	"force-restart")
		palava_kill && palava_start
		exit $?
		;;
		
    "status")
        palava_status
        if [ $? -eq 0 ]; then
            echo "Framework is running"
            exit 0
        else
            echo "Framework is not running"
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
