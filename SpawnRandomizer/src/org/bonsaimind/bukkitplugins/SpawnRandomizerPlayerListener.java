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

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.Listener; // MeneXia: Added import.
import org.bukkit.event.EventHandler; // See above
import org.bukkit.event.EventPriority; // See above

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class SpawnRandomizerPlayerListener implements Listener { // MeneXia: No longer extends PlayerListener, will instead implement Listener

	SpawnRandomizer parent = null;

	public SpawnRandomizerPlayerListener(SpawnRandomizer parentInstance) {
		parent = parentInstance;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL) // MeneXia: Updated to the new Event system, >=CB1.0.0
	public void randomPlayerRespawn(final PlayerRespawnEvent event) {
		parent.teleport(event);
		super.onPlayerRespawn(event);
	}

	@Override
	public void onLogin(final PlayerLoginEvent event) {
		if (parent.teleportOnLogin()) {
			parent.teleport(event.getPlayer());
		}

		super.onPlayerLogin(event);
	}
}
