/*-
 * #%L
 * OpenHospital
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2020 Informatici Senza Frontiere
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.isf.utils.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator
{
	//Java email validation permitted by RFC 5322
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
 
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
 
    public static boolean isValid(String email) {
 
    	//Empty emails are allowed in the app
        if (email == null || email.isEmpty()) {
            return true;
        }
 
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

}
