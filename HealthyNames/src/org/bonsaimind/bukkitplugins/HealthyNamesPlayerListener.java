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

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class HealthyNamesPlayerListener extends PlayerListener {
	private HealthyNames parent = null;

	public HealthyNamesPlayerListener(HealthyNames parentInstance) {
		parent = parentInstance;
	}

	@Override
	public void onPlayerJoin(PlayerEvent event) {
		parent.damageOccured(event.getPlayer());
		super.onPlayerJoin(event);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		parent.damageOccured(event.getPlayer());
		super.onPlayerRespawn(event);
	}
}
