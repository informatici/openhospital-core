/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;

public class LogUtil {

	private LogUtil() {
	}

	/**
	 * Gets the application log file ({@code openhospital.log}) absolute path defined in the {@code log4j.properties}
	 * 
	 * @return
	 */
	public static String getLogFileAbsolutePath() {
		Logger rootLogger = Logger.getRootLogger();
		Appender appender = rootLogger.getAppender("RollingFile");

		if (appender instanceof DailyRollingFileAppender rollingFileAppender) {
			String relativePath = rollingFileAppender.getFile();
			return new File(relativePath).getAbsolutePath();
		} else {
			return "No appender found with the name 'RollingFile' in the root logger.";
		}
	}

	/**
	 * Opens the application log file ({@code openhospital.log}) folder with the System default file explorer.
	 *
	 * @throws IOException 
	 */
	public static void openLogFileLocation() throws IOException {
		File logFile = new File(getLogFileAbsolutePath());
		Desktop.getDesktop().open(new File(logFile.getParent()));
	}

}
