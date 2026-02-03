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
package org.isf.utils.exception.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TestExceptionModel {

	@Test
	void testConstructors() {
		assertThat(setup()).isNotNull();
		assertThat(setupWithDescription()).isNotNull();
	}

	@Test
	void testSetGet() {
		OHExceptionMessage ohExceptionMessage = new OHExceptionMessage("Some Message");

		ohExceptionMessage.setTitle("title");
		assertThat(ohExceptionMessage.getTitle()).isEqualTo("title");

		ohExceptionMessage.setMessage("message");
		assertThat(ohExceptionMessage.getMessage()).isEqualTo("message");

		ohExceptionMessage.setLevel(OHSeverityLevel.INFO);
		assertThat(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.INFO);

		ohExceptionMessage.setDescription(ErrorDescription.PASSWORD_TOO_SHORT);
		assertThat(ohExceptionMessage.getDescription()).isEqualTo(ErrorDescription.PASSWORD_TOO_SHORT);
	}

	protected OHExceptionMessage setup() {
		OHExceptionMessage ohExceptionMessage = new OHExceptionMessage("title", "message", OHSeverityLevel.ERROR);
		return ohExceptionMessage;
	}

	protected OHExceptionMessage setupWithDescription() {
		OHExceptionMessage ohExceptionMessage = new OHExceptionMessage("title", ErrorDescription.PASSWORD_TOO_SHORT,"message", OHSeverityLevel.ERROR);
		return ohExceptionMessage;
	}
}
