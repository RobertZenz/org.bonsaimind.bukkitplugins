/*
 * GPL v3
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
