/*
 * This file is part of HealthyNames.
 *
 * HealthyNames is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HealthyNames is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HealthyNames.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/HealthyNames
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
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
public class HealthyNames extends JavaPlugin {

	final Server server = getServer();
	private Map<Integer, ChatColor> colors = new HashMap<Integer, ChatColor>();
	private HealthyNamesPlayerListener playerListener = new HealthyNamesPlayerListener(this);
	private HealthyNamesEntityListener entityListener = new HealthyNamesEntityListener(this);

	public HealthyNames(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	public void onDisable() {
		playerListener = null;
		entityListener = null;
	}

	public void onEnable() {
		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_DAMAGED, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_DAMAGEDBY_BLOCK, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_DAMAGEDBY_ENTITY, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_DAMAGEDBY_PROJECTILE, entityListener, Priority.Low, this);
		pm.registerEvent(Type.ENTITY_COMBUST, entityListener, Priority.Low, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		readConfiguration();
	}

	protected void readConfiguration() {
		YamlHelper helper = new YamlHelper("plugins/HealthyNames/config.yml");

		// We'll read an integer list, because SnakeYaml can't
		// reload CHATCOLOR for some reason...
		Map<Integer, Integer> temp = helper.read();

		if (temp == null) {
			temp = new HashMap<Integer, Integer>();
		}

		// Set the defaults
		ChatColor defaultColor = ChatColor.DARK_RED;
		for (int i = 0; i <= 20; i++) {
			if (!temp.containsKey(i)) {
				temp.put(i, defaultColor.getCode());
			}

			switch (i) {
				case 5:
					defaultColor = ChatColor.RED;
					break;
				case 10:
					defaultColor = ChatColor.YELLOW;
					break;
				case 15:
					defaultColor = ChatColor.GREEN;
					break;
			}
		}

		if (!helper.exists()) {
			helper.write(temp);
		}

		// Now we'll convert our new shiny list
		// what we actually need.
		colors.clear();

		for (Entry<Integer, Integer> entry : temp.entrySet()) {
			colors.put(entry.getKey(), ChatColor.getByCode(entry.getValue()));
		}
	}

	protected void damageOccured(Player player) {
		System.out.println(player.getHealth());
		if (colors.containsKey(player.getHealth())) {
			player.setDisplayName(colors.get(player.getHealth()) + player.getName() + ChatColor.WHITE);
		} else {
			player.setDisplayName(ChatColor.WHITE + player.getName() + ChatColor.WHITE);
		}
	}
}
