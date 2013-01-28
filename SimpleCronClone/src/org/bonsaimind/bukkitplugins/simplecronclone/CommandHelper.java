/*
 * Unknown License, courtesy of Redecouverte
 * http://forums.bukkit.org/threads/send-commands-to-console.3241/
 *
 * Modified by me
 */
package org.bonsaimind.bukkitplugins.simplecronclone;

//import java.lang.reflect.Field;
import org.bukkit.Server;
//import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
//import net.minecraft.server.v1_4_R1.DedicatedServer;

public class CommandHelper {

	public static void queueConsoleCommand(Server server, String command) {
		if (server == null) {
			System.err.println("CommandHelper: server is null...is the plugin broken?");
			return;
		}
			server.dispatchCommand(server.getConsoleSender(), command);
	}
}
