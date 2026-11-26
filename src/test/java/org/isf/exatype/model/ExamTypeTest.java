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
package org.isf.exatype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.exatype.TestExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ExamTypeTest extends OHCoreTestCase {

	private static TestExamType testExamType;

	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testExamType = new TestExamType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestExamType(false);
		checkExamTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestExamType(true);
		checkExamTypeIntoDb(code);
	}

	@Test
	void hashCodeToString_shouldUseExamTypeFields() throws Exception {
		String code = setupTestExamType(false);
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();

		assertThat(foundExamType.hashCode()).isPositive();

		assertThat(foundExamType).hasToString(foundExamType.getDescription());
	}

	private String setupTestExamType(boolean usingSet) throws OHException {
		ExamType examType = testExamType.setup(usingSet);
		examTypeIoOperationRepository.saveAndFlush(examType);
		return examType.getCode();
	}

	private void checkExamTypeIntoDb(String code) throws OHException {
		ExamType foundExamType = examTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundExamType).isNotNull();
		testExamType.check(foundExamType);
	}

}
