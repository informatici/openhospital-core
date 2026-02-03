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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.sessionaudit.model.SessionAudit;
import org.isf.utils.exception.OHException;

public class TestSessionAudit {

	private static String USERNAME = "TestUserName";
	private static LocalDateTime LOGINDATE = LocalDateTime.of(2024, 7, 12, 12, 12, 0);
	private static LocalDateTime LOGOUTDATE = LocalDateTime.of(2023, 7, 12, 14, 15, 0);

	public SessionAudit setup(boolean usingSet) throws OHException {
		SessionAudit sessionAudit;

		if (usingSet) {
			sessionAudit = new SessionAudit();
			setParameters(sessionAudit);
		} else {
			// Create SessionAudit with all parameters
			sessionAudit = new SessionAudit(USERNAME, LOGINDATE, LOGOUTDATE);
		}

		return sessionAudit;
	}

	public void setParameters(SessionAudit sessionAudit) {
		sessionAudit.setUserName(USERNAME);
		sessionAudit.setLoginDate(LOGINDATE);
		sessionAudit.setLogoutDate(LOGOUTDATE);
	}

	public void check(SessionAudit sessionAudit) {
		assertThat(sessionAudit.getUserName()).isEqualTo(USERNAME);
		assertThat(sessionAudit.getLoginDate()).isEqualTo(LOGINDATE);
		assertThat(sessionAudit.getLogoutDate()).isEqualTo(LOGOUTDATE);
	}
}
