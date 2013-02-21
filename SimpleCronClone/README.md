SimpleCronClone
---------------

A simple cron-like system, utilizing the cron4j-system.


Commands
--------

    simplecronclone

        exec SCRIPTNAME     Execute the given script (name only).
        restart             Restart the plugin (you don't need to stop first).
        stop                Stop the plugin.


Be aware that `exec` assumes that the given script can be found under `plugins/SimpleCronClone`.


Bukkit Dev
----------

This plugin can be found on [Bukkit Dev](http://dev.bukkit.org/server-mods/SimpleCronClone).

You can also check out [the usage stats](http://mcstats.org/plugin/SimpleCronClone).


CronTab format
--------------

Borrowed from the default tab.scc:

    # +----------------> Minute
    # | +--------------> Hour
    # | | +------------> Day of month
    # | | | +----------> Month
    # | | | | +--------> Day of week
    # | | | | |     +--> Script/File
    # | | | | |     | 
      * * * * * sayHello.scc

      # Execute every minute
      * * * * * someScript.scc
      
      # Execute at 06:05 AM
      5 6 * * * someScript.scc
      
      # Execute at the first of every month at 04:50 AM
      50 04 * 1 * someScript.scc
      
      # Run every third hour
      * */3 * * * someScript.scc

For further information [see the Wikipedia-Page of cron](http://en.wikipedia.org/wiki/Cron).
      

Script Format
-------------

There are four basic commands: `do` `doAsync` `exec` and `execWait`. 

 * `do` executes a command as the console, eg: `do say hello world!` is like typing in `say hello world!` at the console. see `exampleDo.scc` for some basic uses, other examples show more advanced fun stuff you can do. The system will wait for the command to finish before it continues with the execution of the script.
 
 * `doAsync` does the same as `do`, but does not wait for completion of the command.
 
 * `exec` executes a program in the background, this is useful if you for example want to run a external map rendering program every once in a while, or some script to update your site. see `exampleExec.scc` for some more uses.
 
 * `execWait` is much like `exec` except it executes your program and waits for it to close. this is useful for back up scripts. because you can do a `save-all` just before and just after, or even have a `say warning: backup in progress, beware lag`. It also captures the output so that you can use it in script for something else. see `exampleExecWait.scc` for some uses.

