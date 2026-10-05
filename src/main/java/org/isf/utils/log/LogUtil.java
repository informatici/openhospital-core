/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.utils.log;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;

public class LogUtil {

	private LogUtil() {
	}

	/**
	 * Gets the application log file ({@code openhospital.log}) absolute path defined in the {@code log4j2-spring.properties}
	 * 
	 * @return
	 */
	public static String getLogFileAbsolutePath() {
		String logFilePath = null;
		Logger rootLogger = LogManager.getRootLogger();

		if (rootLogger instanceof org.apache.logging.log4j.core.Logger coreLogger) {
			for (Appender appender : coreLogger.getAppenders().values()) {
				if (appender instanceof FileAppender fileAppender) {
					logFilePath = fileAppender.getFileName();
				} else if (appender instanceof RollingFileAppender rollingFileAppender) {
					logFilePath = rollingFileAppender.getFileName();
				} else if (appender instanceof RollingRandomAccessFileAppender rollingRandomAccessFileAppender) {
					logFilePath = rollingRandomAccessFileAppender.getFileName();
				}
			}
		}
		return logFilePath;
	}

	/**
	 * Opens the application log file ({@code openhospital.log}) folder with the System default file explorer.
	 *
	 * @throws IOException when the folder is unknown, so that the caller can report it instead of failing
	 */
	public static void openLogFileLocation() throws IOException {
		File folder = logFileFolder(getLogFileAbsolutePath());
		Desktop.getDesktop().open(folder);
	}

	/**
	 * Resolves the folder holding the given log file path.
	 *
	 * @param logFilePath the configured log file path, {@code null} when no file appender is configured
	 * @return the folder holding the log file
	 * @throws IOException when the path is unknown or has no folder
	 */
	static File logFileFolder(String logFilePath) throws IOException {
		if (logFilePath == null) {
			throw new IOException("No file appender is configured: there is no log file to show.");
		}
		// a bare file name has no parent, but it is written in the working directory
		File folder = new File(logFilePath).getAbsoluteFile().getParentFile();
		if (folder == null) {
			throw new IOException("Cannot determine the folder holding " + logFilePath + '.');
		}
		return folder;
	}

}
