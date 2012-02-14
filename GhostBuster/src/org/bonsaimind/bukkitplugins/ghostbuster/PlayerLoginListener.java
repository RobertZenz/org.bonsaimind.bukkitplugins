/*
 * This file is part of Plugin.
 *
 * Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/Plugin
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins.ghostbuster;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class PlayerLoginListener extends PlayerListener {

	private Plugin parent = null;

	public PlayerLoginListener(Plugin parentInstance) {
		parent = parentInstance;
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		parent.playerLoggedIn(event);
		super.onPlayerLogin(event);
	}
}
