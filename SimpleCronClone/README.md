SimpleCronClone
---------------

A simple cron-like system, utilizing the cron4j-system.


Bukkit Updates
--------------

If you want to run the plugin, but you're not sure if it still works, do the following check list:

 * Does it load without an error?
 * Create a simple test-case:
   * Create the script `HelloWorld.scc`, with `do say Hello World!` as content.
   * Schedule it to run every 3 minutes.
   * Does it execute without an error?

If you can answer both questions with **yes**, it works.


Commands
--------

    simplecronclone

        exec SCRIPTNAME     Execute the given script (name only).
        restart             Restart the plugin (you don't need to stop first).
        stop                Stop the complete plugin.


Be aware that `exec` assumes that the given script can be found under `plugins/SimpleCronClone`.


Internal Structure
------------------

The Plugin is divided into three files:

 * Plugin.java
 * Engine.java

The heavy-lifting is done inside the `Engine` class. It will read the `tab.scc`, parse it and set up the cron4j scheduler.


Bukkit Forum
------------

There's a thread at the Bukkit Forum: http://forums.bukkit.org/threads/admn-simplecronclone-v0-5-a-cron-like-scheduling-system-1337.6331/
