/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JComponent;

import org.isf.utils.db.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created on 27/ott/07
 */
public class MessageBundle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageBundle.class);

	private static ResourceBundle resourceBundle;

	private static ResourceBundle defaultResourceBundle;

	public static void initialize() throws RuntimeException {
		try {
			defaultResourceBundle = ResourceBundle.getBundle("language", new Locale("en"));
			resourceBundle = ResourceBundle.getBundle("language", new Locale(GeneralData.LANGUAGE), new UTF8Control());
			JComponent.setDefaultLocale(new Locale(GeneralData.LANGUAGE));
		} catch (MissingResourceException e) {
			LOGGER.error(">> no resource bundle found.");
			System.exit(1);
		}
	}

	public static String getMessage(String key) {
		String message;
		try {
			if (resourceBundle != null) {
				message = resourceBundle.getString(key);
			} else {
				return key;
			}
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
			LOGGER.error(">> key not found: {}", key);
		}
		return message;
	}

	public static ResourceBundle getBundle() {
		if (resourceBundle == null) {
			initialize();
		}
		return resourceBundle;
	}

	/**
	 * Given a single character string (e.g., "S", "C", etc.) return an int that is used for
	 * the setMemonic() method associated for example with a Button object.
	 * <p>
	 * This works because: VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A)
	 *
	 * @param key a MessageBundle key (ending in ".key")
	 * @return the int value associated with the string
	 */
	public static int getMnemonic(String key) {
		return getMessage(key).toUpperCase().charAt(0);
	}

	/**
	 * Given a key to an entry in the resource bundle and a series of objects to place into the
	 * message, return the formatted or compound message.
	 * <p>
	 * For example, given the resource bundle strings:
	 *    English:   User {0} added new item {1} to group {2}.
	 *    Italian:   L'utente {0} ha aggiunto un nuovo elemento {1} al gruppo {2}.
	 *    German:    Das Objekt {1} wurde von Benutzer {0} zur Gruppe {2} hinzugefügt.
	 * <p>
	 * Unlike concatenating the various components together which would work for English and Italian,
	 * it would fail for German (note the ordering of the subsitutable strings).
	 * Thus the code provides the arguments and the translator is free to order them as dicdated by the language.
	 *
	 * @param key a MessageBundle key (that contains ".fmt." in the key name) for a string that contains n-substituables.
	 * @param args a list of n-arguments that matches the substituables in the message string
	 * @return the string where @code{args} have been replaces in the original string
	 */
	public static String formatMessage(String key, Object... args) {
		String message = getMessage(key).replace("'", "''");
		MessageFormat messageFormat = new MessageFormat("");
		messageFormat.applyPattern(message);
		return messageFormat.format(args);
	}
}
