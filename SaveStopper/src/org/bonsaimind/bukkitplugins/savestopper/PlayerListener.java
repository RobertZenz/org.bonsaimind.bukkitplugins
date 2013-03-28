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
package org.bonsaimind.bukkitplugins.savestopper;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
			parent.guess();
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (enabled) {
			parent.guess();
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
