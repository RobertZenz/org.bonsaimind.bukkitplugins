/*
 * This file is part of SaveStopper.
 *
 * SaveStopper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SaveStopper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SaveStopper.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Robert 'Bobby' Zenz
 * Website: http://www.bonsaimind.org
 * GitHub: https://github.com/RobertZenz/org.bonsaimind.bukkitplugins/tree/master/SaveStopper
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
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Robert 'Bobby' Zenz
 */
public class SaveStopperYamlHelper {

	private File configFile = null;
	private Yaml yaml = null;

	/*
	 * Create the class.
	 * @param configFile The path to the configuration file.
	 */
	public SaveStopperYamlHelper(String configFile) {
		this.configFile = new File(configFile);

		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	/**
	 * Check if the file exists.
	 * @return bool true if the file exists.
	 */
	public boolean exists() {
		return configFile.exists();
	}

	/**
	 * Read the configuration file and return the content.
	 * @return The content of the configuration file. null on error.
	 */
	public Map<String, Object> read() {
		Map<String, Object> res = null;
		if (configFile.exists() && configFile.canRead()) {
			try {
				Reader rdr = new FileReader(configFile);
				res = (Map<String, Object>) yaml.load(rdr);
				rdr.close();
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		}

		return res;
	}

	/**
	 * Write the configuration file with the given content.
	 * @param content The new content to write.
	 * @return true if the file was successfully written.
	 */
	public boolean write(Map<String, Object> content) {
		if (!configFile.exists()) {
			File parentDir = new File(configFile.getParent());
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			try {
				configFile.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		}

		if (configFile.exists() && configFile.canWrite()) {
			try {
				Writer wrtr = new FileWriter(configFile);
				yaml.dump(content, wrtr);
				wrtr.close();

				return true;
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}
		}

		return false;
	}
}
