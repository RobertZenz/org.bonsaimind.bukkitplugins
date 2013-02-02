#!/bin/sh
out="$(dirname $0)"
if [ -n "$1" ]; then
    if [ "$1" = "dev" ]; then
        wget "http://dl.bukkit.org/downloads/bukkit/get/latest-dev/bukkit-dev.jar" -O $out/bukkit.jar
        wget "http://dl.bukkit.org/downloads/craftbukkit/get/latest-dev/craftbukkit-dev.jar" -O $out/craftbukkit.jar
        echo 'using latest dev build'
    elif [ "$1" = "beta" ]; then
        wget "http://dl.bukkit.org/downloads/bukkit/get/latest-dev/bukkit-beta.jar" -O $out/bukkit.jar
        wget "http://dl.bukkit.org/downloads/craftbukkit/get/latest-beta/craftbukkit-beta.jar" -O $out/craftbukkit.jar
        echo 'using latest beta build'
    elif [ "$1" = "rb" ]; then
        wget "http://dl.bukkit.org/downloads/bukkit/get/latest-dev/bukkit-rb.jar" -O $out/bukkit.jar
        wget "http://dl.bukkit.org/downloads/craftbukkit/get/latest-rb/craftbukkit-rb.jar" -O $out/craftbukkit.jar
        echo 'using latest recomended build'
    else
        echo 'no build chosen, use ./CMD (dev|beta|rb)'
    fi
fi

