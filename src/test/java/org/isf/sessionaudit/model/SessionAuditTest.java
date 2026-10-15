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
package org.isf.sessionaudit.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.sessionaudit.TestSessionAudit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionAuditTest extends OHCoreTestCase {

	private static TestSessionAudit testSessionAudit;

	@BeforeAll
	static void setUpClass() {
		testSessionAudit = new TestSessionAudit();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		testSessionAudit.check(sessionAudit);
	}

	@Test
	void hashCode_shouldBeComputedFromFields() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(true);
		assertThat(sessionAudit.hashCode()).isEqualTo(-1113769856);
	}

	@Test
	void equals_shouldCompareFields() throws Exception {
		SessionAudit sessionAudit = testSessionAudit.setup(false);
		assertThat(sessionAudit.equals(sessionAudit)).isTrue();

		assertThat(sessionAudit.equals(null)).isFalse();
		assertThat(sessionAudit.equals("someString")).isFalse();

		SessionAudit sessionAudit2 = testSessionAudit.setup(true);
		assertThat(sessionAudit.equals(sessionAudit2)).isTrue();

		sessionAudit2.setCode(sessionAudit2.getCode() + 3);
		assertThat(sessionAudit.equals(sessionAudit2)).isFalse();
	}

}
