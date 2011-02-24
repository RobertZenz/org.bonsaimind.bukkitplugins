/*
 * This file is part of SpawnRandomizer.
 *
 * SpawnRandomizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpawnRandomizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SpawnRandomizer.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/SpawnRandomizer
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class SpawnRandomizer extends JavaPlugin {

	private final Server server = getServer();
	SpawnRandomizerPlayerListener listener = null;
	private Map<String, Object> config = null;
	private Random rand = new Random(System.nanoTime());

	public void onDisable() {
		config.clear();
		config = null;

		rand = null;

		listener = null;
	}

	public void onEnable() {
		PluginManager pm = server.getPluginManager();
		pm.registerEvent(Type.PLAYER_LOGIN, listener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, listener, Priority.Low, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + pdfFile.getVersion() + " is enabled.");

		readConfiguration();
	}

	protected void readConfiguration() {
		YamlHelper helper = new YamlHelper("plugins/SpawnRandomizer/config.yml");
		config = helper.read();

		if (config == null) {
			System.out.println("SpawnRandomizer: No configuration file found, using defaults.");
			config = new HashMap<String, Object>();
		}

		// Set the defaults
		if (!config.containsKey("teleportOnLogin")) {
			config.put("teleportOnLogin", false);
		}

		if (!config.containsKey("minX")) {
			config.put("minX", 0);
		}

		if (!config.containsKey("minY")) {
			config.put("minY", 0);
		}

		if (!config.containsKey("maxX")) {
			config.put("maxX", 0);
		}

		if (!config.containsKey("maxY")) {
			config.put("maxY", 0);
		}

		if (!config.containsKey("allowCaveSpawn")) {
			config.put("allowCaveSpawn", false);
		}

		if (!helper.exists()) {
			System.out.println("SpawnRandomizer: Configuration file doesn't exist, dumping now...");
			helper.write(config);
		}
	}

	protected void teleport(PlayerRespawnEvent event) {
		World world = event.getPlayer().getWorld();

		event.setRespawnLocation(getRandomLocation(world));
	}

	private Location getRandomLocation(World world) {
		int x = rand.nextInt() * (Integer) config.get("minX") % (Integer) config.get("maxX");
		int y = rand.nextInt() * (Integer) config.get("minY") % (Integer) config.get("maxY");

		int z = world.getHighestBlockYAt(x, y);

		if ((Boolean) config.get("allowCaveSpawn")) {
			for (int i = 0; i < world.getHighestBlockYAt(x, y); i++) {
				if (world.getBlockTypeIdAt(x, y, i) == 0 && world.getBlockTypeIdAt(x, y, i + 1) == 0) {
					z = i;
					break;
				}
			}
		}

		return new Location(world, x, y, z);
	}
}
