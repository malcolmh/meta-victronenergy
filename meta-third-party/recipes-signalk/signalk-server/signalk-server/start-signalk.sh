#!/bin/bash
exec 2>&1
echo "*** Starting signalk ***"

CONF="/data/conf/signalk"
PLUGINCONF="${CONF}/plugin-config-data"
DEFAULTCONF="/usr/lib/node_modules/signalk-server/defaults"

# Install the default settings, in case there are no settings yet
mkdir -p /data/conf/signalk/plugin-config-data

if [ ! -f "$CONF/settings.json" ]; then
	cp "$DEFAULTCONF/settings.json" "$CONF"
fi

exec /usr/lib/node_modules/signalk-server/bin/signalk-server -c /data/conf/signalk

