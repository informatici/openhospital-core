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
package org.isf.utils.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.Test;

class TestExceptions {

	@Test
	void testOHDataLockFailureException() {
		assertThat(new OHDataLockFailureException(new OHExceptionMessage("message"))).isNotNull();
		assertThat(new OHDataLockFailureException(new Throwable(), new OHExceptionMessage("message"))).isNotNull();
	}

	@Test
	void testOHDBConnectionException() {
		assertThat(new OHDBConnectionException(new OHExceptionMessage("message"))).isNotNull();
		assertThat(new OHDBConnectionException(new Throwable(), new OHExceptionMessage("message"))).isNotNull();
	}

	@Test
	void testOHDicomException() {
		List<OHExceptionMessage> ohExceptionMessages = new ArrayList<>();
		ohExceptionMessages.add(new OHExceptionMessage("message"));
		assertThat(new OHDicomException(ohExceptionMessages)).isNotNull();

		assertThat(new OHDicomException(new Throwable(), ohExceptionMessages)).isNotNull();
	}

	@Test
	void testOHException() {
		assertThat(new OHException("message", new Throwable())).isNotNull();

		assertThat(new OHException("message")).isNotNull();
	}

	@Test
	void testOHInvalidSQLException() {
		assertThat(new OHInvalidSQLException(new OHExceptionMessage("message"))).isNotNull();
		assertThat(new OHInvalidSQLException(new Throwable(), new OHExceptionMessage("message"))).isNotNull();
	}

	@Test
	void testOHReportException() {
		assertThat(new OHReportException(new Throwable(), new OHExceptionMessage("message"))).isNotNull();
	}

	@Test
	void testOHServiceException() {
		List<OHExceptionMessage> ohExceptionMessages = new ArrayList<>();
		ohExceptionMessages.add(new OHExceptionMessage("message"));
		assertThat(new OHServiceException(ohExceptionMessages)).isNotNull();

		assertThat(new OHServiceException(new Throwable(), ohExceptionMessages)).isNotNull();
	}
}
