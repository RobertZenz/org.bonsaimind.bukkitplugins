/*
 * This file is part of SaveStopper.
 *
 * SaveStopper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SaveStopper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SaveStopper.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/SaveStopper
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Robert 'Bobby' Zenz
 */
public class SaveStopper extends JavaPlugin {

	private final Server server = getServer();
	private boolean isSaving = true;
	private Map<String, Object> config = null;
	private Timer timer = new Timer(true);
	private SaveStopperPlayerListener listener = new SaveStopperPlayerListener(this);

	public SaveStopper(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	public void onDisable() {
		timer.cancel();
		timer = null;

		listener = null;

		config.clear();
		config = null;
	}

	public void onEnable() {
		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_LOGIN, listener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_QUIT, listener, Priority.Low, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		readConfiguration();

		if ((Boolean) config.get("disableOnStart")) {
			internalDisable();
		}
	}

	protected void readConfiguration() {
		YamlHelper helper = new YamlHelper("plugins/SaveStopper/config.yml");
		config = helper.read();

		if (config == null) {
			if ((Boolean) config.get("verbose")) {
				System.out.println("SaveStopper: No configuration file found, using defaults.");
			}
			config = new HashMap<String, Object>();
		}

		// Set the defaults
		if (!config.containsKey("disableOnStart")) {
			config.put("disableOnStart", true);
		}

		if (!config.containsKey("saveAll")) {
			config.put("saveAll", true);
		}

		if (!config.containsKey("wait")) {
			config.put("wait", 300);
		}

		if (!config.containsKey("verbose")) {
			config.put("verbose", true);
		}

		if (!helper.exists()) {
			if ((Boolean) config.get("verbose")) {
				System.out.println("SaveStopper: Configuration file doesn't exist, dumping now...");
			}
			helper.write(config);
		}
	}

	/**
	 * Enable saving.
	 */
	protected void enable() {
		if (server.getOnlinePlayers().length == 0 && isSaving && (Boolean) config.get("verbose")) {
			System.out.println("SaveStopper: Canceling scheduled disabling...");
			timer.purge();
		}

		if (!isSaving) {
			if ((Boolean) config.get("verbose")) {
				System.out.println("SaveStopper: Enabling saving...");
			}
			CommandHelper.queueConsoleCommand(server, "save-on");
			isSaving = true;
		}
	}

	/**
	 * Disable saving, check if we should use the timer or not.
	 */
	protected void disable() {
		long wait = ((Number) config.get("wait")).longValue();
		if (wait > 0) {

			if ((Boolean) config.get("verbose")) {
				System.out.println("SaveStopper: Scheduling disabling in " + Long.toString(wait) + " seconds...");
			}

			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					internalDisable();
				}
			},
					wait * 1000);
		} else {
			internalDisable();
		}
	}

	/**
	 * Disable saving.
	 */
	private void internalDisable() {
		if (isSaving && server.getOnlinePlayers().length == 0) {
			if ((Boolean) config.get("verbose")) {
				System.out.println("SaveStopper: Disabling saving...");
			}

			if ((Boolean) config.get("saveAll")) {
				CommandHelper.queueConsoleCommand(server, "save-all");
			}

			CommandHelper.queueConsoleCommand(server, "save-off");

			isSaving = false;
		}
	}
}
