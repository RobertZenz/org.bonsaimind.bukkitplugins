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
package org.bonsaimind.bukkitplugins.SaveStopper;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class PlayerListener extends org.bukkit.event.player.PlayerListener {

	private Plugin parent = null;
	private boolean enabled = true;

	public PlayerListener(Plugin parentInstance) {
		parent = parentInstance;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (enabled) {
			parent.check();
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (enabled) {
			parent.check();
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
