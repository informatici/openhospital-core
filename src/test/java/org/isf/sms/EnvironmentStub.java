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
package org.isf.sms;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public class EnvironmentStub implements Environment {

	@Override
	public String[] getActiveProfiles() {
		return new String[0];
	}
	@Override
	public String[] getDefaultProfiles() {
		return new String[0];
	}
	@Override
	public boolean acceptsProfiles(String... profiles) {
		return false;
	}
	@Override
	public boolean acceptsProfiles(Profiles profiles) {
		return false;
	}
	@Override
	public boolean containsProperty(String key) {
		return false;
	}
	@Override
	public String getProperty(String key) {
		return "";
	}
	@Override
	public String getProperty(String key, String defaultValue) {
		return "";
	}
	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		return null;
	}
	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return null;
	}
	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		return "";
	}
	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		return null;
	}
	@Override
	public String resolvePlaceholders(String text) {
		return "";
	}
	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		return "";
	}
}
