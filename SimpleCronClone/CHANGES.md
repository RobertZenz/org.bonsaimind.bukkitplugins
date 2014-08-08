Version 1.3

* Update Metrics to stop getting that annoying warning on the Metrics website.
* Update EventListener.java and Metrics.java such that they understand getOnlinePlayers() is now a collection (thus use `.size()`)
* Added `onDisable` event: Caution that it happens during plugin shutdown (and thus server shutdown) so most services are unavailable at this time. Meaning that the script command `exec` is basically the only "safe" command.
* Added `onEnable` event: Ran when the plugin finishes loading. Note that does not mean the server is ready to handle console commands yet (via `do`), but those will be queued and ran once available though.

Version 1.2

* [Change log on DBO](http://dev.bukkit.org/bukkit-plugins/simplecronclone/files/5-simple-cron-clone-v1-2/)

Version 1.1

* [Change log on DBO](http://dev.bukkit.org/bukkit-plugins/simplecronclone/files/4-simple-cron-clone-v1-1/)

Version 1.0

* [Change log on DBO](http://dev.bukkit.org/bukkit-plugins/simplecronclone/files/3-simple-cron-clone-v1-0/)

Version 0.9 

 > (edit notes: oops forgot these existed due to using DBO...)


Version 0.8

 > e877b3d973

 * Added MCStats  (admalled/Eric Driggers)
 * Executing a script from the console is now asynchronous  (admalled/Eric Driggers)
 * Every output is now send through the logger of Bukkit
 * Updated cron4j to 2.2.5


Version 0.7

 > fc05d7bea5
 
 * Fixed compatibility with 1.4.*-* (admalled/Eric Driggers)
 * Fixed possible exceptions when executing server commands (admalled/Eric Driggers)


Version 0.6

 > 2e4a3abd0b

 * Fixed compatibility with 1.3.1-R1 (admalled/Eric Driggers)


Version 0.5

 > 280676a6e9

 * Cleaned up and refactored


Version 0.4

 * Fixed compatibility with 733


Version 0.3

 * Fixed problems with shell calls


Version 0.2

 * Added commands (cron_reinit, cron_stop, cron_exec
 * Added support for inlined comments in scripts


Version 0.1

 * First Version

