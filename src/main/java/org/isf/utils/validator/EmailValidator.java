/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.utils.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

	// Java email validation permitted by RFC 5322
	// Found this expression and discussion near the end of https://www.regular-expressions.info/email.html
	private static final String EMAIL_REGEX = "\\A(?=[A-Z0-9@.!#$%&'*+/=?^_‘{|}~-]{6,254}\\z)"
			+ "(?=[A-Z0-9.!#$%&'*+/=?^_‘{|}~-]{1,64}@)"
			+ "[A-Z0-9!#$%&'*+/=?^_‘{|}~-]+(?:\\.[A-Z0-9!#$%&'*+/=?^_‘{|}~-]+)*"
			+ "@(?:(?=[A-Z0-9-]{1,63}\\.)[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?\\.)+"
			+ "(?=[A-Z0-9-]{1,63}\\z)[A-Z0-9](?:[A-Z0-9-]*[A-Z0-9])?\\z";
	private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

	public static boolean isValid(String email) {

		// Empty emails are allowed in the app
		if (email == null || email.isEmpty()) {
			return true;
		}

		Matcher matcher = EMAIL_PATTERN.matcher(email);
		return matcher.matches();
	}

}
