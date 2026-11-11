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
package org.isf.sessionaudit.manager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.isf.OHCoreTestCase;
import org.isf.sessionaudit.TestSessionAudit;
import org.isf.sessionaudit.model.SessionAudit;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SessionAuditManagerTest extends OHCoreTestCase {

	private static TestSessionAudit testSessionAudit;

	@Autowired
	SessionAuditManager sessionAuditManager;

	@BeforeAll
	static void setUpClass() {
		testSessionAudit = new TestSessionAudit();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void newSessionAudit_shouldSaveSessionAudit() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		int code = sessionAuditManager.newSessionAudit(sessionAudit);

		Optional<SessionAudit> newSessionAudit = sessionAuditManager.getSessionAudit(code);
		assertThat(newSessionAudit.isPresent()).isTrue();

		testSessionAudit.check(newSessionAudit.get());
	}

	@Test
	void getSessionAudit_shouldReturnEmpty_whenCodeNotFound() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		int code = sessionAuditManager.newSessionAudit(sessionAudit);

		Optional<SessionAudit> newSessionAudit = sessionAuditManager.getSessionAudit(code + 99);
		assertThat(newSessionAudit.isPresent()).isFalse();
	}

	@Test
	void updateSessionAudit_shouldSaveLogoutDate() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		int code = sessionAuditManager.newSessionAudit(sessionAudit);

		Optional<SessionAudit> newSessionAudit = sessionAuditManager.getSessionAudit(code);
		assertThat(newSessionAudit.isPresent()).isTrue();

		SessionAudit sessionAuditObject = newSessionAudit.get();
		LocalDateTime now = TimeTools.getNow();
		sessionAuditObject.setLogoutDate(now);

		SessionAudit updatedSessionAudit = sessionAuditManager.updateSessionAudit(sessionAuditObject);
		assertThat(updatedSessionAudit.getLogoutDate()).isEqualTo(now);
	}

}
