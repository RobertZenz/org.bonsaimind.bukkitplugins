/*
 * Unknown License, courtesy of Redecouverte
 * http://forums.bukkit.org/threads/send-commands-to-console.3241/
 */

package org.bonsaimind.bukkitplugins;

import java.lang.reflect.Field;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;

public class CommandHelper {

	private static final Logger logger = Logger.getLogger("Minecraft");

	public static void queueConsoleCommand(Server server, String cmd) {

		if (server instanceof CraftServer) {
			CraftServer cs = (CraftServer) server;
			Field f;
			try {
				f = CraftServer.class.getDeclaredField("console");
			} catch (NoSuchFieldException ex) {
				logger.info("NoSuchFieldException");
				return;
			} catch (SecurityException ex) {
				logger.info("SecurityException");
				return;
			}
			MinecraftServer ms;
			try {
				f.setAccessible(true);
				ms = (MinecraftServer) f.get(cs);
			} catch (IllegalArgumentException ex) {
				logger.info("IllegalArgumentException");
				return;
			} catch (IllegalAccessException ex) {
				logger.info("IllegalAccessException");
				return;
			}
			if ((!ms.g) && (MinecraftServer.a(ms))) {
				ms.a(cmd, ms);
			}
		}

	}
}
