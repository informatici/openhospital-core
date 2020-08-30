package org.isf.admission.service;


import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;


public interface AdmissionIoOperationRepositoryCustom {

	Optional<Admission> findOneByPatientAndDateRanges(Patient patient, GregorianCalendar[] admissionRange,
													  GregorianCalendar[] dischargeRange);


	class PatientAdmission {
		/**
		 * @see Patient#getCode()
		 */
		private final Integer patientId;

		/**
		 * @see Admission#getId()
		 */
		private final Integer admissionId;

		public PatientAdmission(final Integer patientId,
								final Integer admissionId) {
			this.patientId = patientId;
			this.admissionId = admissionId;
		}

		public Integer getPatientId() {
			return patientId;
		}

		public Integer getAdmissionId() {
			return admissionId;
		}
	}
}
