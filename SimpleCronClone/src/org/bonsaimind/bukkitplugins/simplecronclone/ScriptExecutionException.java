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

/**
 * This exception will be thrown when there's an error during
 * the execution of a script.
 */
public class ScriptExecutionException extends Exception {

	public ScriptExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
