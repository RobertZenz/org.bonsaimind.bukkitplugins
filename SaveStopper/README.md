SaveStopper
-----------

SaveStopper is a simple administration plugin which will disable world saving if no one is playing. While monitoring my server I realized that the Minecraft-Server was writing to the harddisk even though noone was online, and so no chunk should be loaded/active. I never did find out what it saves, but it's most likely Day/Night-Cycle related, everything else shouldn't be running if noone is online.


Commands
--------

 > savestopper
 >
 >	check		Check if saving should be enabled or disabled.
 >			Be warned, this command really enables or disables it.
 >	save-off	Disable saving (not scheduled, does save-all (if configured)).
 >	save-on		Enables saving.
 >	start		Starts monitoring Login/Quit-Events (default on).
 >	status		Prints status of the plugin.
 >	stop		Stops monitoring Login/Quit-Events.


Usage in combination with backup scripts
----------------------------------------

If you want to abckup your server frequently, I suggest to stop SaveStopper during that time:

 > savestopper stop
 > savestopper save-off
 > 
 > dobackupLogicHere
 >
 > savestopper check
 > savestopper start

That way saving will not be prematurely enabled if somebody comes online during backup, and it will not unnecessarily enabled if noone is online after backup.
This kind of command-chain was actually Kimundis (Bukkit Forum Id: 75010) idea, I'm just too lazy to go back to that Forum and tell him that I finally did it.


Internal Structure
------------------

The plugin is separated into four files:

 * Plugin.java
 * PlayerListener.java
 * CommandHelper.java
 * Settings.java

The heavy lifting is done inside the Plugin-Class, especially in the saveOn(), saveOff() and saveOffScheduled() functions.
