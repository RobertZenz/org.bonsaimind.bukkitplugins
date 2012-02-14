/*
 * This file is part of GhostBuster.
 *
 * GhostBuster is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GhostBuster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GhostBuster.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/GhostBuster
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins.ghostbuster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class Settings {

	private static final String BANTIME = "banTime";
	private static final String FREE_SLOTS_MODE = "freeSlotsMode";
	private static final String DEATH_MESSAGE = "deathMessage";
	private static final String KEEP_AT_RESTART = "keepAtRestart";
	private static final String STILL_DEAD_MESSAGE = "stillDeadMessage";
	private File exceptionFile;
	private File ghostFile;
	private File settingsFile;
	private List<String> exceptions;
	private Map<String, Date> ghosts;
	private Map<String, Object> settings;

	public Settings(String settingsFile) {
		this.settingsFile = new File(settingsFile + "config.yml");
		this.exceptionFile = new File(settingsFile + "exceptions.yml");
		this.ghostFile = new File(settingsFile + "ghosts.yml");

		if (!this.settingsFile.exists()) {
			try {
				this.settingsFile.getParentFile().mkdirs();
				this.settingsFile.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}

		init();
	}

	public void banPlayer(String playerName) {
		ghosts.put(playerName, new Date());
	}

	/**
	 * How long should the player be banned (in minutes).
	 * @return
	 */
	public int getBanTime() {
		return (Integer) get(BANTIME);
	}

	/**
	 * Returns the moment the player was banned.
	 * @param playerName
	 * @return
	 */
	public Date getBanTime(String playerName) {
		if (ghosts.containsKey(playerName)) {
			return ghosts.get(playerName);
		} else {
			return null;
		}
	}

	/**
	 * Displayed when the player dies.
	 * @return
	 */
	public String getDeathMessage() {
		return (String) get(DEATH_MESSAGE);
	}

	/**
	 * Only kick players if maximum is reached.
	 * @return
	 */
	public boolean getFreeSlotsMode() {
		return (Boolean) get(FREE_SLOTS_MODE);
	}

	/**
	 * Keep the ghost list even when the server restarts.
	 * @return
	 */
	public boolean getKeepAtRestart() {
		return (Boolean) get(KEEP_AT_RESTART);
	}

	/**
	 * Displayed when the player tries to join, but is still banned.
	 * @return
	 */
	public String getStillDeadMessage() {
		return (String) get(STILL_DEAD_MESSAGE);
	}

	/**
	 * Initialize the settings. This will not overwrite anything,
	 * but makes sure that the class can be safely used.
	 */
	public void init() {
		if (ghosts == null) {
			ghosts = new HashMap<String, Date>();
		}
		if (exceptions == null) {
			exceptions = new ArrayList<String>();
		}
		if (settings == null) {
			settings = new HashMap<String, Object>();
		}

		// Default configuration
		if (!settings.containsKey(BANTIME)) {
			settings.put(BANTIME, 120);
		}
		if (!settings.containsKey(DEATH_MESSAGE)) {
			settings.put(DEATH_MESSAGE, "You've died...come back when you live again.");
		}
		if (!settings.containsKey(FREE_SLOTS_MODE)) {
			settings.put(FREE_SLOTS_MODE, false);
		}
		if (!settings.containsKey(KEEP_AT_RESTART)) {
			settings.put(KEEP_AT_RESTART, true);
		}
		if (!settings.containsKey(STILL_DEAD_MESSAGE)) {
			settings.put(STILL_DEAD_MESSAGE, "You're a ghost, you don't exist, go away!");
		}
	}

	public boolean isBanned(String playerName) {
		Date bannedSince = getBanTime(playerName);
		return bannedSince != null && ((new Date().getTime() - bannedSince.getTime()) <= getBanTime());
	}

	public boolean isExcepted(String playerName) {
		return exceptions.contains(playerName);
	}

	public void load() {
		ghosts = (Map<String, Date>) load(ghostFile);
		exceptions = (List<String>) load(exceptionFile);
		settings = (Map<String, Object>) load(settingsFile);
	}

	public void save() {
		if (ghosts != null) {
			save(ghostFile, ghosts);
		}
		if (exceptions == null) {
			save(exceptionFile, exceptions);
		}
		if (settings == null) {
			save(settingsFile, settings);
		}
	}

	private Object get(String name) {
		if (settings == null || !settings.containsKey(name)) {
			init();
		}

		return settings.get(name);
	}

	private Yaml getYaml() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		return new Yaml(options);
	}

	public Object load(File file) {
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			return getYaml().load(reader);
		} catch (FileNotFoundException ex) {
			System.err.println(ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}

		return null;
	}

	/**
	 * Save.
	 * @param settingsFile Save to this file.
	 */
	public void save(File file, Object content) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			getYaml().dump(content, writer);
		} catch (IOException ex) {
			System.err.println(ex);
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}
	}
}
