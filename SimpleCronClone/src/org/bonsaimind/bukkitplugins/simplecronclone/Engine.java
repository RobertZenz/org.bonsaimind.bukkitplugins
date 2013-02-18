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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * This is the engine which does the heavy lifting and interfacing
 * with cron4j.
 */
public final class Engine {

	private static final String COMMAND_DO = "do";
	private static final String COMMAND_EXEC = "exec";
	private static final String COMMAND_EXECWAIT = "execWait";
	private static final String COMMENT_START = "#";
	private static final String OUTPUT_TOKEN = "$?";
	private File workingDir;
	private Server server;
	private Scheduler scheduler;
	private Logger logger;
	/**
	 * If you wonder what this is, no problem. I'll tell you.
	 * This is some awesome RegEx written by Tim Pietzcker
	 * http://stackoverflow.com/questions/4780728/regex-split-string-preserving-quotes
	 *
	 * This is a RegEx which allows you to split a string by single-quotes
	 * and preserving the quotes.
	 * 
	 * Go and vote that awesome guy up!
	 */
	Pattern preparePattern = Pattern.compile("(?<=^[^']*(?:'[^']?'[^']?)?) (?=(?:[^']*'[^']*')*[^']*$)");

	public Engine(Server server, File workingDir, Logger logger) {
		this.server = server;
		this.workingDir = workingDir;
		this.logger = logger;
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
			Reader reader = new FileReader(tab);
			BufferedReader bufReader = new BufferedReader(reader);

			String line;
			while ((line = bufReader.readLine()) != null) {
				if (!line.isEmpty() && !line.trim().startsWith(COMMENT_START)) {
					parseTabLine(line);
				}
			}

			bufReader.close();
			reader.close();

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

		String timerPart = line.substring(0, line.lastIndexOf(" ")).trim();
		final String commandPart = line.substring(line.lastIndexOf(" ") + 1).trim();

		logger.log(Level.INFO, "Scheduling: {0}", commandPart);
		scheduler.schedule(timerPart, new Runnable() {

			@Override
			public void run() {
				executeScript(new File(workingDir, commandPart));
			}
		});
	}

	/**
	 * Parses and executes the given script.
	 * @param script The file which represents the script.
	 * @return Returns true of the execution was without incident.
	 */
	protected boolean executeScript(File script) {
		logger.log(Level.INFO, "Executing: {0}", script.getPath());
		if (!script.exists() || !script.canRead()) {
			logger.log(Level.WARNING, "{0} does not exist or is not accessible.", script.getPath());
			return false;
		}

		try {
			Reader reader = new FileReader(script);
			BufferedReader bufReader = new BufferedReader(reader);

			String line;
			String lastOutput = "";
			while ((line = bufReader.readLine()) != null) {
				if (!line.isEmpty() && !line.trim().startsWith(COMMENT_START)) {
					// Remove inlined comments
					if (line.contains(COMMENT_START)) {
						line = line.substring(0, line.indexOf(COMMENT_START));
					}

					line = line.trim();

					if (!line.isEmpty() && line.indexOf(' ') > 0) {
						lastOutput = parseScriptLine(line.trim().replace(OUTPUT_TOKEN, lastOutput));
					}
				}
			}

			bufReader.close();
			reader.close();
		} catch (FileNotFoundException ex) {
			logger.log(Level.WARNING, "Could not find script: \"{0}\"", script);
			return false;
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Failed to read from \"{0}\"\n{1}", new Object[]{script, ex.getMessage()});
			return false;
		}

		return true;
	}

	protected String parseScriptLine(String line) {
		final String type = line.substring(0, line.indexOf(" ")).trim();
		final String command = line.substring(line.indexOf(" ") + 1).trim();

		if (type.equalsIgnoreCase(COMMAND_DO)) {
			// Server command
			runDo(command);
		} else if (type.equalsIgnoreCase(COMMAND_EXEC)) {
			// Kick off a process
			runExec(command);
		} else if (type.equalsIgnoreCase(COMMAND_EXECWAIT)) {
			// Execute a process
			return runExecWait(command);
		}

		return "";
	}

	/**
	 * Runs the given command via the Bukkit/InGame-Console.
	 * @param command The command to execute.
	 */
	protected void runDo(final String command) {
		try {
			BukkitScheduler bscheduler = server.getScheduler();
			bscheduler.callSyncMethod(server.getPluginManager().getPlugin("SimpleCronClone"), new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					server.dispatchCommand(server.getConsoleSender(), command);
					return true;
				}
			}).get();
		} catch (InterruptedException ex) {
			logger.log(Level.WARNING, "Interrupted: \"{0}\"\n{1}", new Object[]{command, ex.getMessage()});
		} catch (ExecutionException ex) {
			logger.log(Level.WARNING, "Execution exception: \"{0}\"\n{1}", new Object[]{command, ex.getMessage()});
		}
	}

	/**
	 * Executes an external command.
	 * @param command The command to execute.
	 */
	protected void runExec(final String command) {
		try {
			Runtime.getRuntime().exec(command).waitFor();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Can not access/execute: \"{0}\"\n{1}", new Object[]{command, ex.getMessage()});
		} catch (InterruptedException ex) {
			logger.log(Level.WARNING, "Interrupted: \"{0}\"\n{1}", new Object[]{command, ex.getMessage()});
		}
	}

	/**
	 * Executes an external command and returns its output (stdout).
	 * @param command The command to execute.
	 * @return The output (stdout) of the executed command.
	 */
	protected String runExecWait(final String command) {
		try {
			// We need to split the string to pass it to the system
			String[] splittedCommand = preparePattern.split(command);
			for (int idx = 0; idx < splittedCommand.length; idx++) {
				// Strip single quotes from the commands
				splittedCommand[idx] = splittedCommand[idx].replaceAll("^'|'$", "");
			}

			Process proc = Runtime.getRuntime().exec(splittedCommand);
			proc.waitFor();

			String errOutput = readFromStream(proc.getErrorStream());
			if (errOutput.length() > 0) {
				logger.log(Level.WARNING, "Command returned with an error: {0}", errOutput);
			}

			return readFromStream(proc.getInputStream());
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Can not access/execute: \"{0}\"\n{1}", new Object[]{command, ex.getMessage()});
		} catch (InterruptedException ex) {
			logger.log(Level.WARNING, "Interrupted Error :-( \"{0}\"\n{1}", new Object[]{command, ex.getMessage()});
		}

		return "";
	}

	/**
	 * Reads from the stream and returns what was read.
	 * @param strm The input stream.
	 * @return The content which could be read from the stream. 
	 */
	private String readFromStream(InputStream strm) {
		StringBuilder builder = new StringBuilder();

		String line;
		try {
			InputStreamReader reader = new InputStreamReader(strm);
			BufferedReader bufReader = new BufferedReader(reader);
			while ((line = bufReader.readLine()) != null) {
				builder.append(line);
				builder.append(" ");
			}

			bufReader.close();
			reader.close();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Failed to read from stream:\n{0}", ex.getMessage());
		}

		return builder.toString();
	}
}
