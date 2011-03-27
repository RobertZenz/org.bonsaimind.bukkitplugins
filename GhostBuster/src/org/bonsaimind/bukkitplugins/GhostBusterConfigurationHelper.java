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
package org.bonsaimind.bukkitplugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author robert
 */
public class GhostBusterConfigurationHelper {

	public enum timeUnit {

		seconds,
		minutes,
		hours,
		days
	}
	protected long banTime = 120;
	protected timeUnit banTimeUnit = timeUnit.minutes;
	protected boolean keepAtRestart = true;
	protected String deathMessage = "You've died, comeback in %h hours and %m minutes.";
	protected String stillDeadMessage = "You're still dead for another %h hours and %m minutes.";
	protected boolean freeSlotsMode = false;

	public long getBanTime() {
		switch (banTimeUnit) {
			case seconds:
				return banTime;

			case minutes:
				return banTime * 60;

			case hours:
				return banTime * 3600;

			case days:
				return banTime * (3600 * 24);
		}

		return banTime;
	}

	public void setBanTime(long banTime) {
		switch (banTimeUnit) {
			case seconds:
				this.banTime = banTime;

			case minutes:
				this.banTime = banTime / 60;

			case hours:
				this.banTime = banTime / 3600;

			case days:
				this.banTime = banTime / (3600 * 24);
		}
	}

	public timeUnit getBanTimeUnit() {
		return banTimeUnit;
	}

	public void setBanTimeUnit(timeUnit banTimeUnit) {
		this.banTimeUnit = banTimeUnit;
	}

	public boolean isKeepAtRestart() {
		return keepAtRestart;
	}

	public void setKeepAtRestart(boolean keepAtRestart) {
		this.keepAtRestart = keepAtRestart;
	}

	public String getDeathMessage() {
		return deathMessage;
	}

	public void setDeathMessage(String deathMessage) {
		this.deathMessage = deathMessage;
	}

	public String getStillDeadMessage() {
		return stillDeadMessage;
	}

	public void setStillDeadMessage(String stillDeadMessage) {
		this.stillDeadMessage = stillDeadMessage;
	}

	public boolean isFreeSlotsMode() {
		return freeSlotsMode;
	}

	public void setFreeSlotsMode(boolean freeSlotsMode) {
		this.freeSlotsMode = freeSlotsMode;
	}

	public void save() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);

		if (!config.exists()) {
			File parentDir = new File(config.getParent());
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			try {
				config.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		}

		if (config.exists() && config.canWrite()) {
			try {
				Writer wrtr = new FileWriter(config);
				yaml.dump(this, wrtr);
				wrtr.close();
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}

	protected static File config = new File("./plugins/GhostBuster/config.yml");
	public static GhostBusterConfigurationHelper load() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);

		GhostBusterConfigurationHelper res = new GhostBusterConfigurationHelper();
		if (config.exists() && config.canRead()) {
			try {
				Reader rdr = new FileReader(config);
				res = (GhostBusterConfigurationHelper) yaml.load(rdr);
				rdr.close();
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		}

		return res;
	}

	private static String prepareMessage(String message, long timeLeft) {
		message = message.replace("%h", Long.toString(timeLeft / 60));
		message = message.replace("%m", Long.toString(timeLeft % 60));

		return message;
	}
}
