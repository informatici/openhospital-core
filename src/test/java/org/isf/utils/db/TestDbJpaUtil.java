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
package org.isf.utils.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.isf.menu.manager.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

class TestDbJpaUtil {

	@Mock
	private EntityManagerFactory entityManagerFactoryMock;
	@Mock
	private EntityManager entityManagerMock;
	@Mock
	private ApplicationContext applicationContextMock;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		Context.setApplicationContext(applicationContextMock);
		when(applicationContextMock.getBean("entityManagerFactory", EntityManagerFactory.class)).thenReturn(entityManagerFactoryMock);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testNew() throws Exception {
		DbJpaUtil dbJpaUtil = new DbJpaUtil();
		assertThat(dbJpaUtil).isNotNull();
	}

	@Test
	void testOpen() throws Exception {
		DbJpaUtil dbJpaUtil = new DbJpaUtil();
		when(entityManagerFactoryMock.createEntityManager()).thenReturn(entityManagerMock);
		dbJpaUtil.open();
	}

}
