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
package org.isf;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OHCoreTestCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(OHCoreTestCase.class);

	@PersistenceContext
	public EntityManager entityManager;

	public void cleanH2InMemoryDb() {
		List<Object[]> showTables = entityManager.createNativeQuery("SHOW TABLES")
			.getResultList();
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
		showTables.stream()
			.map(result -> (String) result[0])
			.forEach(s -> {
				try {
					entityManager.createNativeQuery("TRUNCATE TABLE " + s).executeUpdate();
				} catch (Exception e) {
					LOGGER.error("Could not delete table ", e);
				}
			});
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}

}
