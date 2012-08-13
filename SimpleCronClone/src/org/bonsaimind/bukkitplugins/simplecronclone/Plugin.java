/*
 * This file is part of Plugin.
 *
 * Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/Plugin
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins.simplecronclone;

import java.io.File;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class Plugin extends JavaPlugin {

	private Server server;
	private Engine engine;

	@Override
	public void onDisable() {
		engine.stop();
		engine = null;
	}

	@Override
	public void onEnable() {
		server = getServer();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		engine = new Engine(server, new File("plugins/SimpleCronClone/"));
		engine.start();

		setCommands();
	}

	private void setCommands() {
		getCommand("simplecronclone").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if (!sender.isOp()) {
					sender.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				if (args.length == 0) {
					return false;
				}

				for (int idx = 0; idx < args.length; idx++) {
					String arg = args[idx];

					if (arg.equalsIgnoreCase("exec")) {
						for (int scriptIdx = idx + 1; scriptIdx < args.length; scriptIdx++) {
							String script = args[scriptIdx];

							if (!script.endsWith(".scc")) {
								script += ".scc";
							}

							if (engine.executeScript(new File("plugins/SimpleCronClone/" + script))) {
								sender.sendMessage("SimpleCronClone: Executed \"plugins/SimpleCronClone/" + script + "\".");
							} else {
								sender.sendMessage("SimpleCronClone: Error while executing \"plugins/SimpleCronClone/" + script + "\".");
							}
						}
					} else if (arg.equalsIgnoreCase("restart")) {
						engine.stop();
						engine.start();
						sender.sendMessage("SimpleCronClone: Restarted.");
					} else if (arg.equalsIgnoreCase("stop")) {
						engine.stop();
						sender.sendMessage("SimpleCronClone: HALTED!");
						sender.sendMessage("SimpleCronClone: Use \"/simplecronclone restart\" to restart it.");
					}
				}

				return true;
			}
		});
	}
}
