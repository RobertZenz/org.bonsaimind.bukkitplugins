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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
/**
 * This is the engine which does the heavy lifting and interfacing
 */
public final class EventEngine {

	private static final String COMMENT_START = "#";
	//TODO: there has to be a better data structure for all of these that we could iterate over...
	public static final String EVENT_JOIN = "playerJoin";
	public static final String EVENT_QUIT = "playerQuit";
	public static final String EVENT_FIRST_JOIN = "playerFirstJoin";
	public static final String EVENT_SERVER_EMPTY = "serverEmpty";
	public static final String EVENT_SERVER_NOT_EMPTY = "serverNotEmpty";
	public static final String EVENT_PLAYER_WORLD_MOVE = "playerTeleportWorld";
	public static final String EVENT_WORLD_EMPTY = "worldEmpty";
	public static final String EVENT_WORLD_NOT_EMPTY = "worldNotEmpty";
	private File workingDir;
	private Server server;
	private Logger logger;
	public boolean verbose;
	//strings of the filePaths to the .sce files
	private HashMap<String, List<MemorySection>> events = new HashMap<String, List<MemorySection>>();

	public EventEngine(Server server, Logger logger, File workingDir, boolean verbose) {
		this.server = server;
		this.workingDir = workingDir;
		this.logger = logger;
		this.verbose = verbose;
	}

	public void start() {
		// clear all the old stuff away
		stop();

		events.put(EVENT_JOIN, new ArrayList<MemorySection>());
		events.put(EVENT_FIRST_JOIN, new ArrayList<MemorySection>());
		events.put(EVENT_QUIT, new ArrayList<MemorySection>());
		events.put(EVENT_SERVER_EMPTY, new ArrayList<MemorySection>());
		events.put(EVENT_SERVER_NOT_EMPTY, new ArrayList<MemorySection>());
		events.put(EVENT_PLAYER_WORLD_MOVE, new ArrayList<MemorySection>());
		events.put(EVENT_WORLD_EMPTY, new ArrayList<MemorySection>());
		events.put(EVENT_WORLD_NOT_EMPTY, new ArrayList<MemorySection>());

		readTab();
	}

	public void stop() {
		events.clear();
	}

	/**
	 * Reads the tab.sce (from the default location) and parses it.
	 * @return Returns true if reading and parsing was without incident.
	 */
	protected boolean readTab() {
		File tabfile = new File(workingDir, "tab.sce");

		if (!tabfile.exists() || !tabfile.canRead()) {
			logger.log(Level.WARNING, "{0} does not exist or is not accessible.", tabfile.getPath());
			return false;
		}
		
		FileConfiguration tab = YamlConfiguration.loadConfiguration(tabfile);
		logger.info("SCE tab loaded, parsing...");
		parseEventSection(EVENT_JOIN, tab);
		parseEventSection(EVENT_FIRST_JOIN, tab);
		parseEventSection(EVENT_QUIT, tab);
		parseEventSection(EVENT_SERVER_EMPTY, tab);
		parseEventSection(EVENT_SERVER_NOT_EMPTY, tab);
		parseEventSection(EVENT_PLAYER_WORLD_MOVE, tab);
		parseEventSection(EVENT_WORLD_EMPTY, tab);
		parseEventSection(EVENT_WORLD_NOT_EMPTY, tab);
		

		
		
		
		

		return false;
	}


	protected void parseEventSection(String event_name, FileConfiguration tab) {
		logger.info(String.format("SCE loading %s events...",event_name));
		MemorySection ej = (MemorySection) tab.getConfigurationSection(event_name);
		for (String partialKey : ej.getKeys(false)){
			MemorySection script = (MemorySection) ej.getConfigurationSection(String.format("%s.sce", partialKey));
			events.get(event_name).add(script);
			System.out.println(script.getCurrentPath().split("\\.", 2)[1]);
		}
	}

	/**
	 * Parse the given line and add it to the event runner.
	 * @param event name
	 * @param "arguments" to replace inside of the .sce
	 */
	public void runEventsFor(String event_name, final String[] args) {
		if (events.containsKey(event_name)) {
			for (MemorySection config : events.get(event_name)) {
				final String filePath = config.getCurrentPath().split("\\.", 2)[1];
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						ScriptParser script = new ScriptParser(server, logger, verbose);
						if (filePath.split(" ")[0].endsWith(".sce")) {
							// We have a script
							// Note that args will = [] if the tab line is blank afterwards as well, so no special casing needed.
							String[] args = filePath.split(" ");
							String file = filePath.split(" ")[0];
							script.executeScript(new File(workingDir, file), args);
						} else {
							// not a script, only a one line script-thing
							try {
								script.parseScriptLine(filePath, "", args);
							} catch (ScriptExecutionException ex) {
								logger.log(Level.WARNING, "Failed to execute PartialScript \"{0}\" at \"{1}\"\n{2}", new Object[]{filePath, ex.getMessage(), ex.getCause().getMessage()});
							}
						}
					}
				});
				//set to a daemon so that when we are stopped, we ignore this thread
				//TODO: does this bork a reload command?
				t.setDaemon(true);
				t.start();
			}
		}
	}
}
