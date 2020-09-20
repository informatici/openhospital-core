/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.generaldata;

/*
 * Created on 27/ott/07
 */

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.isf.utils.db.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBundle {

	private static final Logger logger = LoggerFactory.getLogger(MessageBundle.class);

	private static ResourceBundle resourceBundle = null;

	private static ResourceBundle defaultResourceBundle = null;

	public static void initialize() throws RuntimeException {
		try {
			defaultResourceBundle = ResourceBundle.getBundle("language", new Locale("en"));
			resourceBundle = ResourceBundle.getBundle("language", new Locale(GeneralData.LANGUAGE), new UTF8Control());
			JOptionPane.setDefaultLocale(new Locale(GeneralData.LANGUAGE));
		} catch (MissingResourceException e) {
			logger.error(">> no resource bundle found.");
			System.exit(1);
			//throw new RuntimeException("no resource bundle found.");
		}
	}

	public static String getMessage(String key) {
		String message = "";

		try {
			if (resourceBundle != null) {
				//message = new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
				message = resourceBundle.getString(key);
			} else
				return key;
		} catch (MissingResourceException e) {
			if (GeneralData.DEBUG) {
				message = key;
			} else {
				try {
					message = defaultResourceBundle.getString(key);
				} catch (MissingResourceException e1) {
					message = key;
				}
			}
			logger.error(">> key not found: {}", key);
		}
		return message;
	}

	public static String getMessagePattern(String key, Object input) {
		String message = "";

		try {
			if (resourceBundle != null) {
				//message = new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
				message = resourceBundle.getString(key);
			}
		} catch (MissingResourceException e) {
			if (GeneralData.DEBUG) {
				message = key;
			} else {
				message = defaultResourceBundle.getString(key);
			}
			logger.error(">> key not found: {}", key);
		}
		message = message.replace("#", input.toString());
		return message;
	}

	public static String getMessagePattern(String key, Object[] inputs) {
		String message = "";

		try {
			if (resourceBundle != null) {
				//message = new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
				message = resourceBundle.getString(key);
			}
		} catch (MissingResourceException e) {
			if (GeneralData.DEBUG) {
				message = key;
			} else {
				message = defaultResourceBundle.getString(key);
			}
			logger.error(">> key not found: {}", key);
		}

		for (Object input : inputs) {
			message = message.replaceFirst("#", input.toString());
		}
		return message;
	}

	public static ResourceBundle getBundle() {
		return resourceBundle;
	}
}
