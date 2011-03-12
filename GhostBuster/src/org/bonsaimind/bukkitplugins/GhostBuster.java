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

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
	private GhostBusterPlayerListener playerListener = new GhostBusterPlayerListener(this);
	private GhostBusterEntityListener entityListener = new GhostBusterEntityListener(this);
	private Map<String, Object> config = null;
	private Map<String, Date> ghosts = null;
	private List<String> exceptions = null;

	public void onDisable() {
		saveGhosts();
	}

	public void onEnable() {
		server = getServer();

		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.High, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		readConfiguration();
		loadGhosts();
		getExceptions();

		setCommands();
	}

	protected void readConfiguration() {
		GhostBusterYamlHelper helper = new GhostBusterYamlHelper("plugins/GhostBuster/config.yml");
		config = (Map<String, Object>) helper.read();

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

		if (!config.containsKey("freeSlotsMode")) {
			config.put("freeSlotsMode", false);
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

	protected void loadGhosts() {
		GhostBusterYamlHelper helper = new GhostBusterYamlHelper("plugins/GhostBuster/ghosts.yml");
		ghosts = (Map<String, Date>) helper.read();

		if (ghosts == null) {
			System.out.println("GhostBuster: No ghost list was found.");
			ghosts = new HashMap<String, Date>();
		}
	}

	protected void saveGhosts() {
		GhostBusterYamlHelper helper = new GhostBusterYamlHelper("plugins/GhostBuster/ghosts.yml");

		if ((Boolean) config.get("keepAtRestart")) {
			helper.write(ghosts);
		} else {
			helper.write(new HashMap<String, Date>());
		}
	}

	protected void getExceptions() {
		GhostBusterYamlHelper helper = new GhostBusterYamlHelper("plugins/GhostBuster/exceptions.yml");
		exceptions = (List<String>) helper.read();

		if (exceptions == null) {
			System.out.println("GhostBuster: No exceptions list was found.");
			exceptions = new LinkedList<String>();
		}
	}

	protected void setCommands() {
		getCommand("ghost_ban").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				if (strings.length > 0) {
					String playerName = strings[0];
					Player player = server.getPlayer(playerName);

					if (player != null) {
						cs.sendMessage("GhostBuster: Banning \"" + playerName + "\"...");
						makeGhost(player);
					} else {
						cs.sendMessage("GhostBuster: Sorry, I don't know who \"" + playerName + "\" is...");
					}

					return true;
				}

				return false;
			}
		});

		getCommand("ghost_unban").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				if (strings.length > 0) {
					String playerName = strings[0];

					if (ghosts.containsKey(playerName)) {
						cs.sendMessage("GhostBuster: Unbanning \"" + playerName + "\"...");
						ghosts.remove(playerName);
						saveGhosts();
					} else {
						cs.sendMessage("GhostBuster: \"" + playerName + "\" isn't banned...");
					}

					return true;
				}

				return false;
			}
		});

		getCommand("ghost_list").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				Integer counter = 0;
				Long now = new Date().getTime();
				DecimalFormat format = new DecimalFormat("00");

				for (Map.Entry<String, Date> ghost : ghosts.entrySet()) {
					long diff = (now - ghost.getValue().getTime()) / 1000 / 60;

					if (diff < (Integer) config.get("banTime")) {
						cs.sendMessage(prepareMessage("\"" + ghost.getKey() + "\" is banned for %h:%m", (Integer) config.get("banTime") - diff));
						counter++;
					}
				}

				if (counter <= 0) {
					cs.sendMessage("GhostBuster: No ghosts on this server.");
				}

				return true;
			}
		});

		getCommand("ghost_clear").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				ghosts.clear();
				saveGhosts();
				System.out.println("GhostBuster: All ghosts are revived.");

				return true;
			}
		});

		getCommand("ghost_reinit").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				readConfiguration();
				System.out.println("GhostBuster: Configuration reloaded.");

				return true;
			}
		});
	}

	protected void makeGhost(Player player) {
		if (!exceptions.contains(player.getName())) {
			// Only ban if the freeSlotsMode is off or the Server is full...
			if (!(Boolean) config.get("freeSlotsMode") || server.getOnlinePlayers().length == server.getMaxPlayers()) {
				ghosts.put(player.getName(), new Date());
				player.kickPlayer(prepareMessage((String) config.get("deathMessage"), ((Integer) (config.get("banTime"))).longValue()));
				saveGhosts();
			}
		}
	}

	protected void playerLoggedIn(PlayerLoginEvent event) {
		String name = event.getPlayer().getName();

		if (ghosts.containsKey(name)) {
			Date now = new Date();
			Date then = ghosts.get(name);
			long diff = (now.getTime() - then.getTime()) / 1000 / 60;

			if (diff < (Integer) config.get("banTime")) {
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, prepareMessage((String) config.get("stillDeadMessage"), (Integer) config.get("banTime") - diff));
			} else {
				ghosts.remove(name);
				saveGhosts();
			}
		}
	}

	private String prepareMessage(String message, long timeLeft) {
		message = message.replace("%h", Long.toString(timeLeft / 60));
		message = message.replace("%m", Long.toString(timeLeft % 60));

		return message;
	}
}
