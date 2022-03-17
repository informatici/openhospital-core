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
package org.isf.generaldata;

public final class Version extends ConfigurationProperties {

	private static final String FILE_PROPERTIES = "version.properties";
	private static final boolean EXIT_ON_FAIL = true;

	public static String VER_MAJOR;
    public static String VER_MINOR;
    public static String VER_RELEASE;
    
    private static Version mySingleData;

	private Version(String fileProperties) {
		super(fileProperties, EXIT_ON_FAIL);

		VER_MAJOR = myGetProperty("VER_MAJOR", "");
		VER_MINOR = myGetProperty("VER_MINOR", "");
		VER_RELEASE = myGetProperty("VER_RELEASE", "");
	}

	public static Version getVersion() {
		if (mySingleData == null) {
			initialize();
		}
		return mySingleData;
	}

	public static void initialize() {
		mySingleData = new Version(FILE_PROPERTIES);
	}

}
