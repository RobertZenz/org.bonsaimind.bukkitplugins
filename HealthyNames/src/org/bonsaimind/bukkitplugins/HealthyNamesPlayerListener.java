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

import java.util.Arrays;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class HealthyNamesPlayerListener extends PlayerListener {

	private HealthyNames parent = null;
	private Integer foodIds[] = {
		92,		// Cake (Block)
		260,	// Red Apple
		282,	// Mushroom Stew...reminds me of Wiggles.
		//296,	// Wheat
		297,	// Bread
		319,	// Raw Porkchop
		320,	// Cooked Porkchop
		322,	// Golden Apple
		335,	// Milk
		//344,	// Egg
		349,	// Raw Fish
		350,	// Fish
		//353,	// Sugar
		354,	// Why do I implement this? It's a lie anyway...
		357		// Cookies!!!
	};

	public HealthyNamesPlayerListener(HealthyNames parentInstance) {
		parent = parentInstance;

		Arrays.sort(foodIds);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		parent.refreshHealth(event.getPlayer());
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		parent.refreshHealth(event.getPlayer());
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.hasBlock()) {
			if(Arrays.binarySearch(foodIds, event.getClickedBlock().getTypeId()) >= 0) {
				parent.refreshHealth(event.getPlayer());
			}
		}

		if(event.hasItem()) {
			if(Arrays.binarySearch(foodIds, event.getItem().getTypeId()) >= 0) {
				parent.refreshHealth(event.getPlayer());
			}
		}
	}
}
