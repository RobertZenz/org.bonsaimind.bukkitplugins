/*
 * This file is part of SaveStopper.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
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

	private SaveStopperPlayerListener listener = new SaveStopperPlayerListener(this);

	public SaveStopper(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	public void onDisable() {
		listener = null;
	}

	public void onEnable() {
		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_LOGIN, listener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_QUIT, listener, Priority.Low, this);
		
        PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");
	}

	protected void onPlayerLogin() {
		if(!isSaving) {
			System.out.println("Enabling save...");
			CommandHelper.queueConsoleCommand(server, "save-on");
			isSaving = true;
		}
	}

	protected void onPlayerQuit() {
		if(isSaving && server.getOnlinePlayers().length == 0) {
			System.out.println("Disabling save...");
			CommandHelper.queueConsoleCommand(server, "save-off");
			isSaving = false;
		}
	}
}
