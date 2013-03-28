#!/usr/bin/env sh

# Launch a testserver with the given plugin.

# Check if we have a terminal, if not launch xterm
if [ ! -t 0 ]; then
	exec xterm $0 $@
fi

plugin=
clean=0

for arg in "$@"; do
	case "$arg" in
		GhostBuster|gb)
			plugin=GhostBuster;;
			
		HealthyNames|hn)
			plugin=HealthyNames;;
			
		RedSponge|rs)
			plugin=RedSponge;;
			
		SaveStopper|ss)
			plugin=SaveStopper;;
			
		SimpleCronClone|scc)
			plugin=SimpleCronClone;;
			
		SpawnRandomizer|sr)
			plugin=SpawnRandomizer;;
			
		clean)
			clean=1
	esac
done

if [ -z "$plugin" ]; then
	echo "runTestServer.sh [clean] PLUGIN

Avalailable Plugins
GhostBuser      | gb
HealthyNames    | hn
RedSponge       | rs
SaveStopper     | ss
SimpleCronClone | scc
SpawnRandomizer | sr

clean will give you an empty plugins dir and also destroy
the stashed version of the plugins.

Wait...did you just say 'stashed version'?
Nice that you ask, yes, I did say 'stashed version', that's because in
this script every plugin gets its own plugins directory. It will be saved
under the name 'PLUGIN-plugins'. It will be copied into the place of plugins
everytime you run this script, and then will be stashed away again.
That allows you to easily test mulitple plugins, keep their configurations
but don't need to worry about anything else.
Also this script will hapiily build every plugin before running it.

Please note that stopping this script with Ctrl+C will yield !!!FUN!!!
for your configurations."
	exit 1
fi

# Create the directory
serverDir=./TestServer
if [ ! -d "$serverDir" ]; then
	mkdir "$serverDir"
fi

# Check if there is already a bukkit.jar
if [ ! -f "$serverDir/craftbukkit.jar" ]; then
	if [ ! -f "./lib/craftbukkit.jar" ]; then
		echo "You didn't download craftbukkit, can't start, sorry."
		exit 1
	fi
	
	cp "./lib/craftbukkit.jar" "$serverDir/craftbukkit.jar"
fi

# Descend into our directory
cd $serverDir

pluginDir=$plugin-plugins

# Remove an existing plugins directory
if [ -d "plugins" ]; then
	rm -r "plugins"
fi

# Rename the other one (if it exists) and only if the user
# did not request a clean environment.
if [ -d "$pluginDir" ] && [ $clean -eq 0 ]; then
	mv "$pluginDir" "plugins"
else
	mkdir "plugins"
fi

# We're ready, let's build and copy the plugin.
cd "../$plugin/"
ant
cd "../$serverDir/" # I hate this path changing crap so much...
cp -f "../$plugin/dist/$plugin.jar" "./plugins/$plugin.jar"

# Now run the server
java -Xms1024M -Xmx1024M -jar "craftbukkit.jar" nogui

# We returned from the server, the user is done,
# let's stash that plugins directory away.
if [ -d "plugins" ]; then
	if [ -d "$pluginDir" ]; then
		rm -r "$pluginDir"
	fi
	
	mv "plugins" "$pluginDir"
fi

# We're done here!
