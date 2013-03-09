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

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import it.sauronsoftware.cron4j.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;

/**
 * This is the engine which does the heavy lifting and interfacing
 * with cron4j.
 */
public final class CronEngine {

	private static final String COMMENT_START = "#";
	private File workingDir;
	private Server server;
	private Scheduler scheduler;
	private Logger logger;
	public boolean verbose;

	public CronEngine(Server server, Logger logger, File workingDir,boolean verbose) {
		this.server = server;
		this.workingDir = workingDir;
		this.logger = logger;
		this.verbose = verbose;
	}

	public void start() {
		if (scheduler == null) {
			scheduler = new Scheduler();
			scheduler.setDaemon(true);
		}

		if (readTab()) {
			scheduler.start();
		}
	}

	public void stop() {
		if (scheduler != null && scheduler.isStarted()) {
			//kill any running scripts...
			for (TaskExecutor running : scheduler.getExecutingTasks()){
				if (running.canBeStopped()){
					running.stop();
				}
				else {
					logger.warning("unable to kill a task!");
				}
			}
			scheduler.stop();
		}

		scheduler = null;
	}

	/**
	 * Reads the tab.scc (from the default location) and parses it.
	 * @return Returns true if reading and parsing was without incident.
	 */
	protected boolean readTab() {
		File tab = new File(workingDir, "tab.scc");

		if (!tab.exists() || !tab.canRead()) {
			logger.log(Level.WARNING, "{0} does not exist or is not accessible.", tab.getPath());
			return false;
		}

		try {
			for (String line : ScriptParser.getLines(tab)) {
				if (!line.isEmpty() && !line.trim().startsWith(COMMENT_START)) {
					parseTabLine(line);
				}
			}

			return true;
		} catch (FileNotFoundException ex) {
			logger.log(Level.WARNING, "tab.scc does not exists!");
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Failed to read tab.scc:\n{0}", ex.getMessage());
		}

		return false;
	}

	/**
	 * Parse the given line and add it to the scheduler.
	 * @param line The line form the tab.scc.
	 */
	protected void parseTabLine(String line) {
		line = line.trim();
		// search for the 5th space after trimming leading and trailing whitespace
		// this should be the space between the timerPart and commandPart.
		int splitPoint = ScriptParser.nthOccurrence(line, ' ', 4);
		String timerPart = line.substring(0, splitPoint+1).trim();
		final String commandPart = line.substring(splitPoint+1).trim();

		logger.log(Level.INFO, "SCC Scheduling: {0}", commandPart);
		scheduler.schedule(timerPart, new Task() {

			@Override
			public void execute(TaskExecutionContext context) throws RuntimeException {
				
				ScriptParser script = new ScriptParser(server, logger,verbose);
				if (commandPart.split(" ")[0].endsWith(".scc")) {
					// We have a script
					// Note that args will = [] if the tab line is blank afterwards as well, so no special casing needed.
					String[] args = commandPart.split(" ");
					String file = commandPart.split(" ")[0];
					script.executeScript(new File(workingDir, file), args);
				}
				else {
					// not a script, only a one line script-thing
					try {
						script.parseScriptLine(commandPart, "",null);
					} catch (ScriptExecutionException ex) {
						logger.log(Level.WARNING, "Failed to execute PartialScript \"{0}\" at \"{1}\"\n{2}", new Object[]{commandPart, ex.getMessage(), ex.getCause().getMessage()});
					}
				}
			}
			@Override
			public boolean canBeStopped(){
				return true;
			}
		});
	}
}
