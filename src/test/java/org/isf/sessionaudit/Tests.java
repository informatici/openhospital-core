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
package org.isf.sessionaudit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.isf.OHCoreTestCase;
import org.isf.menu.model.User;
import org.isf.sessionaudit.manager.SessionAuditManager;
import org.isf.sessionaudit.model.SessionAudit;
import org.isf.sessionaudit.model.UserSession;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

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
	void testGetSetSessionAudit() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		testSessionAudit.check(sessionAudit);
	}

	@Test
	void testSessionAuditHashCode() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		assertThat(sessionAudit.hashCode()).isEqualTo(-1113769856);
	}

	@Test
	void testSessionAuditEquals() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(false);
		assertThat(sessionAudit.equals(sessionAudit)).isTrue();

		assertThat(sessionAudit.equals(null)).isFalse();
		assertThat(sessionAudit.equals("someString")).isFalse();

		SessionAudit sessionAudit2 = testSessionAudit.setup(true);
		assertThat(sessionAudit.equals(sessionAudit2)).isTrue();

		sessionAudit2.setCode(sessionAudit2.getCode() + 3);
		assertThat(sessionAudit.equals(sessionAudit2)).isFalse();
	}

	@Test
	void testNewSessionAudit() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		int code = sessionAuditManager.newSessionAudit(sessionAudit);

		Optional<SessionAudit> newSessionAudit = sessionAuditManager.getSessionAudit(code);
		assertThat(newSessionAudit.isPresent()).isTrue();

		testSessionAudit.check(newSessionAudit.get());
	}

	@Test
	void testSessionAuditNotFound() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		int code = sessionAuditManager.newSessionAudit(sessionAudit);

		Optional<SessionAudit> newSessionAudit = sessionAuditManager.getSessionAudit(code + 99);
		assertThat(newSessionAudit.isPresent()).isFalse();
	}

	@Test
	void testUpdateSessionAudit() throws Exception {
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

	@Test
	void testUserSessionGetSet() throws  Exception {
		UserSession.setUser(new User("TestUser", null, "TestPassWord", "TestDescription"));

		assertThat(UserSession.isLoggedIn()).isTrue();

		UserSession.setSessionAuditId(-999);
		assertThat(UserSession.getSessionAuditId()).isEqualTo(-999);

		UserSession.removeUser();
		assertThat(UserSession.isLoggedIn()).isFalse();
	}
}
