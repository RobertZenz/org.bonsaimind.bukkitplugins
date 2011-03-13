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

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/SimpleCronClone
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import java.io.File;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class SimpleCronClone extends JavaPlugin {

	private Server server = null;
	private SimpleCronCloneHelper helper = null;

	public void onDisable() {
		helper.stop();
		helper = null;
	}

	public void onEnable() {
		server = getServer();

		PluginManager pm = server.getPluginManager();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		helper = new SimpleCronCloneHelper(server, new File("plugins/SimpleCronClone/"));
		helper.start();

		setCommands();
	}

	public void setCommands() {
		getCommand("cron_reinit").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				helper.stop();
				helper.start();
				cs.sendMessage("SimpleCronClone: Configuration reloaded.");

				return true;
			}
		});

		getCommand("cron_stop").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				helper.stop();
				cs.sendMessage("SimpleCronClone: HALTED!");
				cs.sendMessage("SimpleCronClone: Use /cron_reinit to restart it.");

				return true;
			}
		});

		getCommand("cron_exec").setExecutor(new CommandExecutor() {

			public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
				if (!cs.isOp()) {
					cs.sendMessage("I'm sorry, Dave. I'm afraid I can't do that.");
					return true;
				}

				if (strings.length == 0) {
					return false;
				}

				for (String script : strings) {
					if (!script.endsWith(".scc")) {
						script += ".scc";
					}

					if(helper.executeScript(new File("plugins/SimpleCronClone/" + script))){
						cs.sendMessage("SimpleCronClone: Executed \"plugins/SimpleCronClone/" + script + "\".");
					} else {
						cs.sendMessage("SimpleCronClone: Error while executing \"plugins/SimpleCronClone/" + script + "\".");
					}
				}

				return true;
			}
		});
	}
}
