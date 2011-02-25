/*
 * This file is part of GhostBuster.
 *
 * GhostBuster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GhostBuster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GhostBuster.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/GhostBuster
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class GhostBuster extends JavaPlugin {

	private Server server = null;
	private GhostBusterPlayerListener listener = new GhostBusterPlayerListener(this);
	private Map<String, Object> config = null;
	private Map<String, Object> ghosts = null;

	public void onDisable() {
		setGhosts();
	}

	public void onEnable() {
		server = getServer();

		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_LOGIN, listener, Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, listener, Priority.High, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		readConfiguration();
		getGhosts();
	}

	protected void readConfiguration() {
		YamlHelper helper = new YamlHelper("plugins/GhostBuster/config.yml");
		config = helper.read();

		if (config == null) {
			System.out.println("GhostBuster: No configuration file found, using defaults.");
			config = new HashMap<String, Object>();
		}

		// Set the defaults
		if (!config.containsKey("banTime")) {
			config.put("banTime", 120);
		}

		if (!config.containsKey("keepAtRestart")) {
			config.put("keepAtRestart", true);
		}

		if (!config.containsKey("deathMessage")) {
			config.put("deathMessage", "You've died...come back when you live again.");
		}

		if (!config.containsKey("stillDeadMessage")) {
			config.put("stillDeadMessage", "You're a ghost, you don't exist, go away.");
		}

		if (!helper.exists()) {
			System.out.println("GhostBuster: Configuration file doesn't exist, dumping now...");
			helper.write(config);
		}
	}

	protected void getGhosts() {
		YamlHelper helper = new YamlHelper("plugins/GhostBuster/ghosts.yml");
		ghosts = helper.read();

		if (ghosts == null) {
			System.out.println("GhostBuster: No ghost list was found.");
			ghosts = new HashMap<String, Object>();
		}
	}

	protected void setGhosts() {
		YamlHelper helper = new YamlHelper("plugins/GhostBuster/ghosts.yml");

		if ((Boolean) config.get("keepAtRestart")) {
			helper.write(ghosts);
		} else {
			helper.write(new HashMap<String, Object>());
		}
	}

	protected void makeGhost(Player player) {
		ghosts.put(player.getName(), new Date());

		System.out.println("makeGhost()");
		player.kickPlayer((String) config.get("deathMessage"));
	}

	protected void playerLoggedIn(PlayerLoginEvent event) {
		String name = event.getPlayer().getName();
		System.out.println("ghostJoined()");
		if (ghosts.containsKey(name)) {
			Date now = new Date();
			Date then = (Date) ghosts.get(name);
			long diff = (now.getTime() - then.getTime()) / 1000 / 60;

			if (diff < (Integer) config.get("banTime")) {
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, (String) config.get("stillDeadMessage"));
			} else {
				ghosts.remove(name);
			}
		}
	}
}
