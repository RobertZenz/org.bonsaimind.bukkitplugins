/*
 * This file is part of SimpleCronClone.
 *
 * SimpleCronClone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleCronClone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleCronClone.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonsaimind.bukkitplugins.simplecronclone;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.IllegalPluginAccessException;


public class EventListener implements Listener {

	private Plugin sccMain;

	public EventListener(Plugin plugin) {
		sccMain = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		// if it is a new player, do stuff before anything else
		if (!event.getPlayer().hasPlayedBefore()) {
			// eventFirstJoin
			// TODO: this seems not to be reliable? any ideas on why? :/
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_FIRST_JOIN, new String[]{EventEngine.EVENT_FIRST_JOIN,
						event.getPlayer().getName()});
		}

		sccMain.eventEngine.runEventsFor(EventEngine.EVENT_JOIN, new String[]{EventEngine.EVENT_JOIN,
					event.getPlayer().getName()});

		// now that the player has "joined" lets see if the player is alone...
		if (sccMain.getServer().getOnlinePlayers().size() == 1) {
			// only user logged in means that we were just empty.
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_SERVER_NOT_EMPTY, new String[]{
						EventEngine.EVENT_SERVER_NOT_EMPTY, event.getPlayer().getName()});
		}
		if (event.getPlayer().getWorld().getPlayers().isEmpty()) {
			//eventWorldNotEmpty joined and made a world no longer empty
			//fired before the player is a part of the world list (because onjoin might move them?)
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_WORLD_NOT_EMPTY, new String[]{
						EventEngine.EVENT_WORLD_NOT_EMPTY, event.getPlayer().getName(),
						event.getPlayer().getWorld().getName()});
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		// eventQuit
		sccMain.eventEngine.runEventsFor(EventEngine.EVENT_QUIT, new String[]{EventEngine.EVENT_QUIT,
					event.getPlayer().getName()});

		if (sccMain.getServer().getOnlinePlayers().size() == 1) {
			// this event fires before the server removes the player from the OnlinePlayers, so 1 not 0
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_SERVER_EMPTY, new String[]{
						EventEngine.EVENT_SERVER_EMPTY, event.getPlayer().getName()});
		}
		if (event.getPlayer().getWorld().getPlayers().size() == 1) {
			//eventWorldEmpty, left and made a world empty
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_WORLD_EMPTY, new String[]{
						EventEngine.EVENT_WORLD_EMPTY, event.getPlayer().getName(), event.getPlayer().getWorld().getName()});
		}
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		// eventPlayerWorldMove
		sccMain.eventEngine.runEventsFor(EventEngine.EVENT_PLAYER_WORLD_MOVE, new String[]{
					EventEngine.EVENT_PLAYER_WORLD_MOVE, event.getPlayer().getName(), event.getFrom().getName(),
					event.getPlayer().getWorld().getName()});

		if (event.getFrom().getPlayers().isEmpty()) {
			// eventWorldEmpty
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_WORLD_EMPTY, new String[]{
						EventEngine.EVENT_WORLD_EMPTY, event.getPlayer().getName(), event.getFrom().getName()});
		}
		if (event.getPlayer().getWorld().getPlayers().size() == 1) {
			// eventWorldNotEmpty (if one player, that must means its ours that
			// just moved)
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_WORLD_NOT_EMPTY, new String[]{
						EventEngine.EVENT_WORLD_NOT_EMPTY, event.getPlayer().getName(),
						event.getPlayer().getWorld().getName()});
		}

	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		//eventPlayerDeath
		sccMain.eventEngine.runEventsFor(EventEngine.EVENT_DEATH, new String[]{
			EventEngine.EVENT_DEATH, event.getEntity().getName(), event.getEntity().getWorld().getName()
		});
	}
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		//eventPlayerRespawn
		sccMain.eventEngine.runEventsFor(EventEngine.EVENT_RESPAWN, new String[]{
			EventEngine.EVENT_RESPAWN, event.getPlayer().getName(), event.getPlayer().getWorld().getName()
		});
	}
	public void onEnable(){
		sccMain.eventEngine.runEventsFor(EventEngine.EVENT_ENABLE, new String[]{
				EventEngine.EVENT_ENABLE 
			});
	}
	public void onDisable(){
		try {
			sccMain.eventEngine.runEventsFor(EventEngine.EVENT_DISABLE, new String[]{
					EventEngine.EVENT_DISABLE 
				});
		} catch (IllegalPluginAccessException ex) {
			sccMain.getLogger().log(Level.WARNING, "Unable to execute shutdown events, Likely caused by a script interacting with the main thread! ('exec' is basically the only command available for onDisable events)");
		}
	}
}
