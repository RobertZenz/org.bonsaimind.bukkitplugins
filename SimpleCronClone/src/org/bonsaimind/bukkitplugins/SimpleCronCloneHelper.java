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

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/SimpleCronClone
 * E-Mail: bobby@bonsaimind.org
 */
package org.bonsaimind.bukkitplugins;

import it.sauronsoftware.cron4j.Scheduler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.bukkit.Server;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public final class SimpleCronCloneHelper {

	private File workingDir = null;
	private Server parent = null;
	private Scheduler scheduler = null;

	public SimpleCronCloneHelper(Server parent, File workingDir) {
		this.parent = parent;
		this.workingDir = workingDir;
	}

	public void start() {
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

	protected boolean readTab() {
		if (scheduler == null) {
			scheduler = new Scheduler();
			scheduler.setDaemon(true);
		}

		File tab = new File(workingDir, "tab.scc");

		if (!tab.exists() || !tab.canRead()) {
			System.out.println("SimpleCronHelper: " + tab.getPath() + " does not exist or is not accessible.");
			return false;
		}

		try {
			Reader reader = new FileReader(tab);
			BufferedReader bufReader = new BufferedReader(reader);

			String line;
			while ((line = bufReader.readLine()) != null) {
				if (!line.isEmpty() && line.charAt(0) != '#') {
					parseTabLine(line.trim());
				}
			}

			bufReader.close();
			reader.close();
		} catch (FileNotFoundException ex) {
			System.err.println(ex.getMessage());
			return false;
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			return false;
		}

		return true;
	}

	protected void parseTabLine(String line) {
		String timerPart = line.substring(0, line.lastIndexOf(" ")).trim();
		final String commandPart = line.substring(line.lastIndexOf(" ") + 1).trim();

		System.out.println("SimpleCronHelper: Scheduling: " + commandPart);
		scheduler.schedule(timerPart, new Runnable() {

			public void run() {
				executeScript(new File(workingDir, commandPart));
			}
		});
	}

	protected boolean executeScript(File script) {
		System.out.println("SimpleCronHelper: Executing: " + script.getPath());
		if (!script.exists() || !script.canRead()) {
			System.out.println("SimpleCronHelper: " + script.getPath() + " does not exist or is not accessible.");
			return false;
		}

		try {
			Reader reader = new FileReader(script);
			BufferedReader bufReader = new BufferedReader(reader);

			String line;
			String lastOutput = "";
			while ((line = bufReader.readLine()) != null) {
				if (!line.isEmpty() && line.charAt(0) != '#') {
					// Remove inlined comments
					if (line.contains("#")) {
						line = line.substring(0, line.indexOf("#"));
					}

					line = line.trim();

					if (!line.isEmpty() && line.indexOf(' ') > 0) {
						lastOutput = parseScriptLine(line.trim().replace("$?", lastOutput));
					}
				}
			}

			bufReader.close();
			reader.close();
		} catch (FileNotFoundException ex) {
			System.err.println(ex.getMessage());
			return false;
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			return false;
		}

		return true;
	}

	protected String parseScriptLine(String line) {
		String type = line.substring(0, line.indexOf(" ")).trim();
		String command = line.substring(line.indexOf(" ") + 1).trim();

		if (type.equalsIgnoreCase("do")) {
			// Server command
			SimpleCronCloneCommandHelper.queueConsoleCommand(parent, command);

			return "";

		} else if (type.equalsIgnoreCase("exec")) {
			// Kick off a process
			try {
				Process proc = Runtime.getRuntime().exec(command);
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

			return "";

		} else if (type.equalsIgnoreCase("execWait")) {
			// Execute a process
			try {
				Process proc = Runtime.getRuntime().exec(command);
				proc.waitFor();

				StringBuilder builder = new StringBuilder();

				InputStreamReader reader = new InputStreamReader(proc.getInputStream());
				BufferedReader bufReader = new BufferedReader(reader);

				String tempLine;
				while ((tempLine = bufReader.readLine()) != null) {
					builder.append(tempLine);
					builder.append(" ");
				}

				bufReader.close();
				reader.close();

				return builder.toString();
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			} catch (InterruptedException ex) {
				System.err.println(ex.getMessage());
			}
		}

		return "";
	}
}
