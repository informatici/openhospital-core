package org.isf.admission.service;


import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;

import java.util.GregorianCalendar;
import java.util.List;


public interface AdmissionIoOperationRepositoryCustom {

	/**
	 * @deprecated unless you need all the columns from patient and admission, use {@link #findPatientAndAdmissionId(String)} instead
	 */
	@Deprecated
	List<Object[]> findAllBySearch(String searchTerms);

	List<PatientAdmission> findPatientAndAdmissionId(String searchTerms);

	/**
	 * @deprecated unless you need all the columns from patient and admission, use {@link #findPatientAdmissionsBySearchAndDateRanges(String, GregorianCalendar[], GregorianCalendar[])} instead
	 */
	@Deprecated
	List<Object[]> findAllBySearchAndDateRanges(String searchTerms, GregorianCalendar[] admissionRange,
												GregorianCalendar[] dischargeRange);

	List<PatientAdmission> findPatientAdmissionsBySearchAndDateRanges(String searchTerms,
																	  GregorianCalendar[] admissionRange,
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
