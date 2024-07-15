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
package org.isf.generaldata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConfigurationProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationProperties.class);

	@Test
	void testConfigurationProperties() {

		MockConfigClass mockConfigClass = new MockConfigClass("fileThatDoesNotExist");
		assertThat(mockConfigClass.isInitialized()).isFalse();
		assertThat(mockConfigClass.myGetProperty("someProperty", 0.0D)).isZero();
	}

	class MockConfigClass extends ConfigurationProperties {
		MockConfigClass(String fileProperties) {
			super(fileProperties, false);
		}
	}
}
