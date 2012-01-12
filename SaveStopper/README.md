SaveStopper
-----------

SaveStopper is a simple administration plugin which will disable world saving if no one is playing. While monitoring my server I realized that the Minecraft-Server was writing to the harddisk even though noone was online, and so no chunk should be loaded/active. I never did find out what it saves, but it's most likely Day/Night-Cycle related, everything else shouldn't be running if noone is online.

This will most likely have no effect onto the lifetime or performance of your harddisks, but it is nice if you don't hear your harddisks constantly writing.


Commands
--------

    savestopper
    
    	check		Check if saving should be enabled or disabled.
    			Be warned, this command really enables or disables it.
    	save-off	Disable saving (not scheduled, does save-all (if configured)).
    	save-on		Enables saving.
    	start		Starts monitoring Login/Quit-Events (default on).
    	status		Prints status of the plugin.
    	stop		Stops monitoring Login/Quit-Events.


`save-on` and `save-off` are doing basically the same as the default server commands. Except that `save-off` does an additional `save-all` if configured to do so, otherwise they work exactly the same.


Usage in combination with backup scripts
----------------------------------------

If you want to backup your server frequently, I suggest to stop SaveStopper during that time:

    savestopper stop
    savestopper save-off
    
    dobackupLogicHere
   
    savestopper check
    savestopper start

That way saving will not be prematurely enabled if somebody comes online during backup, and it will not unnecessarily enabled if noone is online after backup.

This kind of command-chain was actually Kimundis (Bukkit Forum Id: 75010) idea, I'm just too lazy to go back to that Forum and tell him that I finally did it. So, thanks dude!


Internal Structure
------------------

The plugin is separated into four files:

 * Plugin.java
 * PlayerListener.java
 * CommandHelper.java
 * Settings.java

The heavy lifting is done inside the Plugin-Class, especially in the `saveOn()`, `saveOff()` and `saveOffScheduled()` functions. The `PlayerListener` is only calling `Plugin.check()`.


Bukkit Forums
-------------

There's a thread at the Bukkit Forum: http://forums.bukkit.org/threads/inactive-misc-savestopper-v0-9-stops-saving-of-the-world-733.3959/

I'm unlikely to return to that Forum, though, it's too much work to stay in that Forum...GitHub got everything I need so far.
