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
package org.isf.patconsensus;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestPatientConsensus {

	private static boolean CONSENSUSFLAG = true;
	private static boolean SERVICEFLAG = false;

	public PatientConsensus setup(boolean usingSet) throws OHException {
		PatientConsensus patientConsensus;
		Patient patient = new Patient();
		TestPatient testPatient = new TestPatient();
		testPatient.setParameters(patient);
		if (usingSet) {
			patientConsensus = new PatientConsensus();
			setParameters(patientConsensus, patient);
		} else {
			// Create PatientConsensus with all parameters
			patientConsensus = new PatientConsensus(CONSENSUSFLAG, SERVICEFLAG, patient);
		}
		return patientConsensus;
	}

	public void setParameters(PatientConsensus patientConsensus, Patient patient) {
		patientConsensus.setConsensusFlag(CONSENSUSFLAG);
		patientConsensus.setServiceFlag(SERVICEFLAG);
		patientConsensus.setPatient(patient);
	}

	public void check(PatientConsensus patientConsensus) {
		assertThat(patientConsensus.isConsensusFlag()).isTrue();
		assertThat(patientConsensus.isServiceFlag()).isFalse();
		assertThat(patientConsensus.getPatient()).isNotNull();
	}

}
