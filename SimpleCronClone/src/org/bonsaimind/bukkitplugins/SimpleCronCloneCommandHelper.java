/*
 * Unknown License, courtesy of Redecouverte
 * http://forums.bukkit.org/threads/send-commands-to-console.3241/
 *
 * Modified by me
 */
package org.bonsaimind.bukkitplugins;

import java.lang.reflect.Field;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;

public class SimpleCronCloneCommandHelper {

	public static void queueConsoleCommand(Server server, String cmd) {
		if (server == null) {
			System.err.println("CommandHelper: server is null...is the plugin broken?");
			return;
		}

		if (!(server instanceof CraftServer)) {
			System.err.println("CommandHelper: server is not a server...is the plugin broken?");
			return;
		}

		CraftServer cs = (CraftServer) server;
		Field f;
		try {
			f = CraftServer.class.getDeclaredField("console");
		} catch (NoSuchFieldException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		} catch (SecurityException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		}
		MinecraftServer ms;
		try {
			f.setAccessible(true);
			ms = (MinecraftServer) f.get(cs);
		} catch (IllegalArgumentException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		} catch (IllegalAccessException ex) {
			System.err.println("CommandHelper: " + ex.getMessage());
			return;
		}
		if ((!ms.isStopped) && (MinecraftServer.isRunning(ms))) {
			ms.issueCommand(cmd, ms);
		}
	}
}
