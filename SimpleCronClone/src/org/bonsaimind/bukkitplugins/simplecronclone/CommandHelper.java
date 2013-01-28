/*
 * This file is part of SimpleCronClone.
 *
 * SimpleCronClone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleCronClone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleCronClone.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package org.bonsaimind.bukkitplugins.simplecronclone;

import org.bukkit.Server;

public class CommandHelper {

	public static void queueConsoleCommand(Server server, String command) {
		if (server == null) {
			System.err.println("CommandHelper: server is null...is the plugin broken?");
			return;
		}
		server.dispatchCommand(server.getConsoleSender(), command);
	}
}
