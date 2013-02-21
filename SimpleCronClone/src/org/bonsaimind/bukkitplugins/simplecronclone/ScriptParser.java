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
 * A static helper class which can parse scripts and execute
 * all kinds of fancy fuck...arrr stuff.
 */
public final class ScriptParser {

	private static final String COMMAND_DO = "do";
	private static final String COMMAND_DO_ASYNC = "doasync";
	private static final String COMMAND_EXEC = "exec";
	private static final String COMMAND_EXECWAIT = "execWait";
	private static final String COMMENT_START = "#";
	private static final String OUTPUT_TOKEN = "$?";
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
	private static Pattern preparePattern = Pattern.compile("(?<=^[^']*(?:'[^']?'[^']?)?) (?=(?:[^']*'[^']*')*[^']*$)");

	/**
	 * Parses and executes the given script.
	 * @param script The file which represents the script.
	 * @return Returns true of the execution was without incident.
	 */
	public static boolean executeScript(final Server server, final Logger logger, File script) {
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
						lastOutput = parseScriptLine(server, logger, line.trim().replace(OUTPUT_TOKEN, lastOutput));
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
		} catch (ScriptExecutionException ex) {
			logger.log(Level.WARNING, "Failed to execute script \"{0}\" at \"{1}\"\n{2}", new Object[]{script, ex.getMessage(), ex.getCause().getMessage()});
			return false;
		}


		return true;
	}

	public static String parseScriptLine(final Server server, final Logger logger, String line) throws ScriptExecutionException {
		final String type = line.substring(0, line.indexOf(" ")).trim();
		final String command = line.substring(line.indexOf(" ") + 1).trim();

		if (type.equalsIgnoreCase(COMMAND_DO)) {
			// Server command
			runDo(server, command);
		} else if (type.equalsIgnoreCase(COMMAND_DO_ASYNC)) {
			// Server command
			runDoAsync(server, command);
		} else if (type.equalsIgnoreCase(COMMAND_EXEC)) {
			// Kick off a process
			runExec(server, command);
		} else if (type.equalsIgnoreCase(COMMAND_EXECWAIT)) {
			// Execute a process
			return runExecWait(server, logger, command);
		}

		return "";
	}

	/**
	 * Runs the given command via the Bukkit/InGame-Console. Does not wait for the command to be completed before
	 * returning.
	 * @param command The command to execute.
	 */
	private static void runDoAsync(final Server server, final String command) throws ScriptExecutionException {
		server.getScheduler().scheduleSyncDelayedTask(
				server.getPluginManager().getPlugin("SimpleCronClone"), new Runnable() {

			@Override
			public void run() {
				server.dispatchCommand(server.getConsoleSender(), command);
			}
		});
	}

	/**
	 * Runs the given command via the Bukkit/InGame-Console. Waits for the command to complete before returning.
	 * @param command The command to execute.
	 */
	public static void runDo(final Server server, final String command) throws ScriptExecutionException {
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
			throw new ScriptExecutionException(command, ex);
		} catch (ExecutionException ex) {
			throw new ScriptExecutionException(command, ex);
		}
	}

	/**
	 * Executes an external command.
	 * @param command The command to execute.
	 */
	public static void runExec(final Server server, final String command) throws ScriptExecutionException {
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException ex) {
			throw new ScriptExecutionException(command, ex);
		}
	}

	/**
	 * Executes an external command and returns its output (stdout).
	 * @param command The command to execute.
	 * @return The output (stdout) of the executed command.
	 */
	public static String runExecWait(final Server server, final Logger logger, final String command) throws ScriptExecutionException {
		try {
			// We need to split the string to pass it to the system
			String[] splittedCommand = preparePattern.split(command);
			for (int idx = 0; idx < splittedCommand.length; idx++) {
				// Strip single quotes from the commands
				splittedCommand[idx] = splittedCommand[idx].replaceAll("^'|'$", "");
			}

			Process proc = Runtime.getRuntime().exec(splittedCommand);
			proc.waitFor();

			String errOutput = readFromStream(server, logger, proc.getErrorStream());
			if (errOutput.length() > 0) {
				logger.log(Level.WARNING, "Command returned with an error: {0}", errOutput);
			}

			return readFromStream(server, logger, proc.getInputStream());
		} catch (IOException ex) {
			throw new ScriptExecutionException(command, ex);
		} catch (InterruptedException ex) {
			throw new ScriptExecutionException(command, ex);
		}
	}

	/**
	 * Reads from the stream and returns what was read.
	 * @param strm The input stream.
	 * @return The content which could be read from the stream. 
	 */
	private static String readFromStream(final Server server, final Logger logger, InputStream strm) {
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
