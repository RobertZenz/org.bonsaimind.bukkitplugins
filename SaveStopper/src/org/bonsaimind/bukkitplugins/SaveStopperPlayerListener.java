/*
 * GPL v3
 */

package org.bonsaimind.bukkitplugins;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class SaveStopperPlayerListener extends PlayerListener {
	private SaveStopper parent = null;

	public SaveStopperPlayerListener(SaveStopper parentInstance) {
		parent = parentInstance;
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		parent.onPlayerLogin();

		super.onPlayerLogin(event);
	}

	@Override
	public void onPlayerQuit(PlayerEvent event) {
		parent.onPlayerQuit();
		
		super.onPlayerQuit(event);
	}

}
