#!/bin/sh
if [ -z "$YNOT_HOME" ]; then
	YNOT_HOME="$HOME/ynot"
fi
java -jar $YNOT_HOME/ynot-vm-1.5-SNAPSHOT.jar $1
