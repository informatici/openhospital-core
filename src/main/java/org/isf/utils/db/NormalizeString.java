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
package org.isf.utils.db;

import java.text.Normalizer;

/**
 * @author Mwithi
 */
public class NormalizeString {

	/**
	 * Use java.text.Normalizer to reduce LOCALE strings for comparing 
	 * @param string - the string to normalize
	 * @return the string normalized
	 */
	public static String normalizeString(String string) {
		String normalizedString = Normalizer.normalize(string, Normalizer.Form.NFD);
		return normalizedString.replaceAll("[^\\p{ASCII}]", "");
	}
	
	/**
	 * String.compareToIgnorecase() method over two normalized strings
	 * @param first - the string to normalize
	 * @param second - the string to normalize and compare
	 * @return 
	 */
	public static int normalizeCompareTo(String first, String second) {
		String newFirst = normalizeString(first);
		String newSecond = normalizeString(second);
		return newFirst.compareTo(newSecond);
	}
	
	/**
	 * String.compareTo() method over two normalized strings
	 * @param first - the string to normalize
	 * @param second - the string to normalize and compare
	 * @return
	 */
	public static int normalizeCompareToIgnorecase(String first, String second) {
		String newFirst = normalizeString(first);
		String newSecond = normalizeString(second);
		return newFirst.compareToIgnoreCase(newSecond);
	}
	
	/**
	 * String.contains() method over two normalized strings
	 * @param string - the string to normalize
	 * @param token - the string to normalize and search for
	 * @return
	 */
	public static boolean normalizeContains(String string, String token) {
		String containingString = normalizeString(string);
		String tokenString = normalizeString(token);
		return containingString.contains(tokenString);
	}

}
