package org.isf.admission.service;


import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;

import java.util.GregorianCalendar;
import java.util.List;


public interface AdmissionIoOperationRepositoryCustom {

	List<Object[]> findAllBySearch(String searchTerms);

	List<PatientAdmission> findPatientAndAdmissionId(String searchTerms);
	
	List<Object[]> findAllBySearchAndDateRanges(String searchTerms, GregorianCalendar[] admissionRange,
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
