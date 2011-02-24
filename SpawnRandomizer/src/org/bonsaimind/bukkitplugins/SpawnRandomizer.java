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
import org.bukkit.entity.Player;
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

	private Server server = null;
	SpawnRandomizerPlayerListener listener = new SpawnRandomizerPlayerListener(this);
	private Map<String, Object> config = null;
	private Random rand = new Random(System.nanoTime());
	private int diffX = 0;
	private int diffZ = 0;
	
	public void onDisable() {
		rand = null;

		listener = null;

		config.clear();
		config = null;

		server = null;
	}

	public void onEnable() {
		server = getServer();

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
			config.put("minX", -100);
		}

		if (!config.containsKey("minZ")) {
			config.put("minZ", -100);
		}

		if (!config.containsKey("maxX")) {
			config.put("maxX", 100);
		}

		if (!config.containsKey("maxZ")) {
			config.put("maxZ", 100);
		}

		if (!config.containsKey("allowCaveSpawn")) {
			config.put("allowCaveSpawn", false);
		}

		if (!helper.exists()) {
			System.out.println("SpawnRandomizer: Configuration file doesn't exist, dumping now...");
			helper.write(config);
		}

		diffX = Math.abs((Integer) config.get("maxX") - (Integer) config.get("minX") + 1);
		diffZ = Math.abs((Integer) config.get("maxZ") - (Integer) config.get("minZ") + 1);
	}

	protected boolean teleportOnLogin() {
		return (Boolean) config.get("teleportOnLogin");
	}

	protected void teleport(Player player) {
		World world = player.getWorld();

		player.teleportTo(getRandomLocation(world));
	}

	protected void teleport(PlayerRespawnEvent event) {
		World world = event.getPlayer().getWorld();

		event.setRespawnLocation(getRandomLocation(world));
	}

	private Location getRandomLocation(World world) {
		int x = rand.nextInt(diffX) + (Integer) config.get("minX");
		int z = rand.nextInt(diffZ) + (Integer) config.get("minZ");

		System.out.println("x: " + x);
		System.out.println("z: " + z);

		int y = world.getHighestBlockYAt(x, z);

		System.out.println("y: " + y);

		if ((Boolean) config.get("allowCaveSpawn")) {
			for (int i = 0; i < y; i++) {
				if (world.getBlockTypeIdAt(x, z, i) == 0 && world.getBlockTypeIdAt(x, z, i + 1) == 0) {
					y = i;
					break;
				}
			}
		}
		System.out.println("y: " + y);

		return new Location(world, x + 0.5, y + 2, z + 0.5);
	}
}
