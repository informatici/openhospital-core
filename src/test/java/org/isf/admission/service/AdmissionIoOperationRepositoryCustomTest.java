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
package org.isf.admission.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperationRepositoryCustom.PatientAdmission;
import org.isf.generaldata.GeneralData;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AdmissionIoOperationRepositoryCustomTest extends OHCoreTestCase {

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
	}

	static Collection<Object[]> maternityRestartInJune() {
		return Arrays.asList(new Object[][] {
			{ false },
			{ true }
		});
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void patientAdmission_shouldReturnPatientIdAndAdmissionId(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		PatientAdmission patientAdmission = new PatientAdmission(1, 2);
		assertThat(patientAdmission.patientId()).isEqualTo(1);
		assertThat(patientAdmission.admissionId()).isEqualTo(2);
	}

	class MyAdmissionIoOperationRepositoryCustom implements AdmissionIoOperationRepositoryCustom {

		@Override
		public List<AdmittedPatient> findPatientAdmissionsBySearchAndDateRanges(String searchTerms, LocalDateTime[] admissionRange,
			LocalDateTime[] dischargeRange) throws OHServiceException {
			return null;
		}
	}

}
