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
package org.isf.sms.providers.skebby;

public class SkebbyGatewayUtil {

	public static String extractUserKey(String userKeyAccessToken) {
		if (userKeyAccessToken == null || !userKeyAccessToken.contains(";")) {
			return null;
		}
		return userKeyAccessToken.split(";")[0];
	}

	public static String extractAccessToken(String userKeyAccessToken) {
		return commonKeyExtractor(userKeyAccessToken);
	}

	public static String extractSessionKey(String userKeyAccessToken) {
		return commonKeyExtractor(userKeyAccessToken);
	}

	private static String commonKeyExtractor(String userKeyAccessToken) {
		if (userKeyAccessToken == null || !userKeyAccessToken.contains(";")) {
			return null;
		}
		return userKeyAccessToken.split(";")[1];
	}

}
