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
 * along with SaveStopper.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonsaimind.bukkitplugins.savestopper;

import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	private static final String CONFIG_FILE = "./plugins/SaveStopper/config.yml";
	private Server server = null;
	private Timer timer = new Timer(true);
	private TimerTask currentTask;
	private PlayerListener listener = new PlayerListener(this);
	private Settings settings;

	@Override
	public void onDisable() {
		settings.save();
		settings = null;

		timer.cancel();
		timer = null;

		listener = null;

		server = null;
	}

	@Override
	public void onEnable() {
		server = getServer();

		server.getPluginManager().registerEvents(listener, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		setCommand();

		settings = new Settings("./plugins/SaveStopper/config.yml");
		settings.load();

		if (settings.getDisableOnStart()) {
			saveOff();
		}
	}

	/**
	 * Checks if saving should be enabled or disabled.
	 * If there are no players online, it will be disabled.
	 */
	protected void check() {
		purgeTask();

		if (server.getOnlinePlayers().length == 0) {
			saveOffScheduled();
		} else {
			saveOn();
		}
	}

	/**
	 * Takes an educated guess if saving should be enabled or disabled.
	 * If only one player is online, it will be disabled, if no players
	 * are online or more than one then it will be enabled.
	 */
	protected void guess() {
		purgeTask();

		if (server.getOnlinePlayers().length == 1) {
			saveOffScheduled();
		} else {
			saveOn();
		}
	}

	private void dispatchCommand(String command) {
		server.dispatchCommand(server.getConsoleSender(), command);
	}

	private void println(String text) {
		System.out.println("SaveStopper: " + text);
	}

	private void purgeTask() {
		if (currentTask != null) {
			currentTask.cancel();
			currentTask = null;
		}
		timer.purge();
	}

	private void saveOff() {
		println("Disabling saving.");

		if (settings.getSaveAll()) {
			dispatchCommand("save-all");
		}

		dispatchCommand("save-off");
	}

	private void saveOffScheduled() {
		if (settings.getWait() > 0) {
			println("Disabling scheduled, " + settings.getWait() + " seconds.");

			currentTask = new TimerTask() {

				@Override
				public void run() {
					saveOff();
				}
			};

			timer.schedule(currentTask, settings.getWait() * 1000);
		} else {
			saveOff();
		}
	}

	private void saveOn() {
		println("Enabling saving.");

		dispatchCommand("save-on");
	}

	private void setCommand() {
		getCommand("savestopper").setExecutor(new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if (!sender.isOp()) {
					sender.sendMessage("Touch me one more time and I'll scream rape!");
					return true;
				}

				if (args.length == 0) {
					return false;
				}

				for (String arg : args) {
					if (arg.equalsIgnoreCase("check")) {
						check();
					} else if (arg.equalsIgnoreCase("save-off")) {
						saveOff();
					} else if (arg.equalsIgnoreCase("save-on")) {
						saveOn();
					} else if (arg.equalsIgnoreCase("start")) {
						println("Started.");
						listener.setEnabled(true);
					} else if (arg.equalsIgnoreCase("status")) {
						println("Monitoring: " + listener.isEnabled());
					} else if (arg.equalsIgnoreCase("stop")) {
						println("Stopped.");
						listener.setEnabled(false);
					}
				}

				return true;
			}
		});
	}
}
