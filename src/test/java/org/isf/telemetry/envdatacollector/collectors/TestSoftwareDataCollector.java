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
package org.isf.telemetry.envdatacollector.collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Map;

import jakarta.persistence.EntityManager;

import org.isf.OHCoreTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TestSoftwareDataCollector extends OHCoreTestCase {

	private SoftwareDataCollector softwareDataCollector;

	@Mock
	EntityManager entityManagerMock;
	@Mock
	Connection connectionMock;
	@Mock
	DatabaseMetaData databaseMetaDataMock;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		softwareDataCollector = new SoftwareDataCollector();
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetId() {
		assertThat(softwareDataCollector.getId()).isEqualTo("TEL_SW");
	}

	@Test
	void testGetDescription() {
		assertThat(softwareDataCollector.getDescription()).startsWith(
						"Software information versions and usage (ex. Ubuntu 22.04, MariaDB 10.6, Open Hospital");
	}

	@Test
	void testRetrieveData() throws Exception {
		ReflectionTestUtils.setField(softwareDataCollector, "em", entityManagerMock);
		when(entityManagerMock.unwrap(Connection.class)).thenReturn(connectionMock);
		when(connectionMock.getMetaData()).thenReturn(databaseMetaDataMock);
		Map<String, String> data = softwareDataCollector.retrieveData();
		assertThat(data).isNotNull();
		assertThat(data).hasSize(41);
	}
}
