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

import java.util.Comparator;

/**
 * DefaultSorter (implements {@link Comparator}) in order to sort
 * by specifying a default element that will be on top
 *
 * @author Nanni
 */
public class DefaultSorter implements Comparator<String> {

	private String defaultValue;

	/**
	 * DefaultSorter (implements {@link Comparator})
	 * @param defaultValue - the default element that will be on top
	 */
	public DefaultSorter(String defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}

	@Override
	public int compare(String o1, String o2) {
		if (o1.compareTo(defaultValue) == 0) {
			return -1;
		} else if (o2.compareTo(defaultValue) == 0) {
			return 1;
		}
		return o1.compareTo(o2);
	}
}
