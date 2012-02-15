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
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/Plugin
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins.ghostbuster;

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
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class Plugin extends JavaPlugin {

	private Server server = null;
	private BukkitScheduler scheduler = null;
	private PlayerLoginListener playerListener = new PlayerLoginListener(this);
	private EntityDeathListener entityListener = new EntityDeathListener(this);
	private Settings settings;

	public void onDisable() {
		settings.save();
		settings = null;

		playerListener = null;
		entityListener = null;

		server = null;
	}

	public void onEnable() {
		server = getServer();
		scheduler = server.getScheduler();

		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		settings = new Settings("./plugins/GhostBuster/");
		settings.load();

		setCommands();
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
				Integer banTime = (Integer) config.get("banTime");

				for (Map.Entry<String, Date> ghost : ghosts.entrySet()) {
					long diff = (now - ghost.getValue().getTime()) / 1000 / 60;

					if (diff < banTime) {
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
				System.out.println("GhostBuster: All ghosts have been revived.");

				return true;
			}
		});

		getCommand("ghost_reinit").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				//readConfiguration();
				System.out.println("GhostBuster: Configuration reloaded.");

				return true;
			}
		});
	}

	protected void banPlayer(Player player) {
		if (!settings.isExcepted(player.getName())) {
			if (!settings.getFreeSlotsMode() || server.getOnlinePlayers().length >= server.getMaxPlayers()) {
				// Now ban the bastard!
				settings.banPlayer(player.getName());

				// Now kick the player
				final Player finalizedPlayer = player;
				scheduler.scheduleAsyncDelayedTask(this, new Runnable() {

					public void run() {
						finalizedPlayer.kickPlayer(prepareMessage(settings.getDeathMessage(), settings.getBanExpiration(finalizedPlayer.getName())));
					}
				}, 4);
			}
		}
	}

	protected void checkPlayer(PlayerLoginEvent event) {
		// Check if the player is allowed to join
		if (settings.isBanned(event.getPlayer().getName())) {
			// Nope, it's not...
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, prepareMessage(settings.getStillDeadMessage(), settings.getBanExpiration(event.getPlayer().getName())));
		}
	}

	/**
	 * Replace %h and %m.
	 * @param message The message.
	 * @param timeLeft Time left, in minutes!
	 * @return
	 */
	private String prepareMessage(String message, long timeLeft) {
		message = message.replace("%h", Long.toString(timeLeft / 60));
		message = message.replace("%m", Long.toString(timeLeft % 60));

		return message;
	}
}
