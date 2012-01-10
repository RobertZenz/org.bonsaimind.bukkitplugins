/*
 * Unknown License, courtesy of Redecouverte
 * http://forums.bukkit.org/threads/send-commands-to-console.3241/
 *
 * Modified by me
 */
package org.bonsaimind.bukkitplugins.SaveStopper;

import java.lang.reflect.Field;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;

public class CommandHelper {

	public static void queueConsoleCommand(Server server, String command) {
		if (server == null) {
			System.err.println("CommandHelper: server is null...is the plugin broken?");
			return;
		}

		if (!(server instanceof CraftServer)) {
			System.err.println("CommandHelper: server is not a server...is the plugin broken?");
			return;
		}

		Field field;
		try {
			field = CraftServer.class.getDeclaredField("console");
		} catch (NoSuchFieldException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		} catch (SecurityException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		}
		
		MinecraftServer minecraftServer;
		try {
			field.setAccessible(true);
			minecraftServer = (MinecraftServer) field.get(server);
		} catch (IllegalArgumentException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		} catch (IllegalAccessException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		}
		
		if ((!minecraftServer.isStopped) && (MinecraftServer.isRunning(minecraftServer))) {
			minecraftServer.issueCommand(command, minecraftServer);
		}
	}
}
