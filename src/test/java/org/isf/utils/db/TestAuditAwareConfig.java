/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.OHCoreTestCase;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.menu.model.User;
import org.isf.sessionaudit.model.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class to indirectly test the {@link AuditorAwareConfig}.
 */
class TestAuditAwareConfig extends OHCoreTestCase {

	private static final String USERNAME = "admin";

	@Autowired
	private AdmissionTypeIoOperationRepository admissionTypeRepository; // Any repository, just for tests

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		insertAuditUser();
		UserSession.setUser(new User(USERNAME, null, "password", "administrator"));
	}

	@AfterEach
	void tearDown() {
		UserSession.removeUser();
	}

	@Test
	void shouldWriteCurrentGuiUserInAuditFields() {
		AdmissionType admissionType = admissionTypeRepository.saveAndFlush(new AdmissionType("AUD", "Audit test"));

		assertThat(admissionType.getCreatedBy()).isEqualTo(USERNAME);
		assertThat(admissionType.getLastModifiedBy()).isEqualTo(USERNAME);
		assertThat(admissionType.getCreatedDate()).isNotNull();
		assertThat(admissionType.getLastModifiedDate()).isNotNull();
	}

	private void insertAuditUser() {
		entityManager.createNativeQuery(
			"INSERT INTO OH_USERGROUP (UG_ID_A, UG_DESC, UG_ACTIVE, UG_DELETED) VALUES ('admin', 'Administrators', 1, false)").executeUpdate();
		entityManager.createNativeQuery(
			"INSERT INTO OH_USER (US_ID_A, US_UG_ID_A, US_PASSWD, US_DESC, US_ACTIVE, US_FAILED_ATTEMPTS, US_ACCOUNT_LOCKED) "
				+ "VALUES ('admin', 'admin', 'password', 'administrator', 1, 0, false)")
			.executeUpdate();
	}
}
