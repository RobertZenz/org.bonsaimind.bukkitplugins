GhostBuster
-----------

A plugin which allows you to ban players if they die for a certain amount of time. That basic idea is that you can create your own hardcore mode for your server.


Bukkit Updates
--------------

If you want to run this lugin, but you're not sure if it still works with your version of Bukkit, just do the following checks:

 * Does it load without error?
 * Get a friend to join your server:
   * Kill him
   * Is your friend now unable to join the server?

If you can answer both questions with **yes**, then it works.


Commands
--------

    ghostbuster

        ban PLAYERNAME         Ban the player with given name.
        except PLAYERNAME      Add that player to the exception-list.
        info PLAYERNAME        Display info about that player.
        reload                 Reload the exception-list and the settings.
        save                   Save everything.
        unban PLAYERNAME       Unban the player.
        unban_all              Unban all players.
        unexcept PLAYERNAME    Remove that player from the exception-list.
        
Be warned that `ban` and `except` do *not* check the given playername, they'll simply add it to the list, they do not check if it is an existing player.


Internal Structure
------------------

The plugin is divided into four files:
 
 * EntityDeathListener
 * PlayerloginListener
 * Plugin.java
 * Winston.java
 
The heavy lifting is done inside the `Winston` class, it contains everything and provides a rather verbose interface to the `Plugin` itself.


Bukkit Forum
------------

Link goes here.
