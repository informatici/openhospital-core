/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.admission.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AdmissionIoOperationRepositoryImpl implements AdmissionIoOperationRepositoryCustom {


	private static String nativeQueryTerms = "SELECT * from OH_patient as p  "
			+ " left join (select * from OH_admission where ADM_IN = 1 and ( (ADM_DELETED='N') or (ADM_DELETED is null ) ) ) as a on p.PAT_ID = a.ADM_PAT_ID "
			+ " where ( ( p.PAT_DELETED='N' ) or ( p.PAT_DELETED is null ) )"
			+ " and ( lower(concat_ws(' ', p.PAT_ID, p.PAT_SNAME, p.PAT_FNAME, p.PAT_NAME, p.PAT_NOTE, p.PAT_TAXCODE, p.PAT_CITY, p.PAT_ADDR, p.PAT_TELE)) like :param0 ) "
			+ " order by p.PAT_ID desc";

	private static String nativeQueryRanges = "SELECT * from OH_patient as p  "
			+ " left join (select * from OH_admission where ADM_IN = 1 and ( (ADM_DELETED='N') or (ADM_DELETED is null ) ) ) as a on p.PAT_ID = a.ADM_PAT_ID "
			+ " where (p.PAT_ID IN (SELECT ADM_PAT_ID from OH_admission where param1))"
			+ " and ( lower(concat_ws(' ', p.PAT_ID, p.PAT_SNAME, p.PAT_FNAME, p.PAT_NAME, p.PAT_NOTE, p.PAT_TAXCODE, p.PAT_CITY, p.PAT_ADDR, p.PAT_TELE)) like :param0 ) "
			+ " order by p.PAT_ID desc";

	private static String nativeQueryCode = "SELECT * from OH_patient as p  "
			+ " left join (select * from OH_admission where ADM_IN = 1 and ( (ADM_DELETED='N') or (ADM_DELETED is null ) ) order by ADM_ID desc) as a on p.PAT_ID = a.ADM_PAT_ID "
			+ " where p.PAT_ID = :param0 "
			+ " and ( ( p.PAT_DELETED='N' ) or ( p.PAT_DELETED is null ) )";

	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<AdmittedPatient> findPatientAdmissionsBySearchAndDateRanges(final String searchTerms, final LocalDateTime[] admissionRange,
			final LocalDateTime[] dischargeRange) throws OHServiceException {
		String[] terms = getTermsToSearch(searchTerms);
		List<AdmittedPatient> admittedPatients = new ArrayList<>();
		if (terms.length == 1) {
			try {
				int code = Integer.parseInt(terms[0]);
				Query nativeQuery = this.entityManager.createNativeQuery(nativeQueryCode, "AdmittedPatient");
				nativeQuery.setParameter("param0", code);

				return parseResultSet(admittedPatients, nativeQuery);

			} catch (NumberFormatException nfe) {
				// used to see if the search parameter is a patient code (number)
			}
		}

		if ((admissionRange != null && (admissionRange[0] != null || admissionRange[1] != null)) ||
				(dischargeRange != null && (dischargeRange[0] != null || dischargeRange[1] != null))) {
			StringBuilder rangePredicate = new StringBuilder("( (ADM_DELETED='N') or (ADM_DELETED is null ) )");
			if (admissionRange != null) {
				
				if (admissionRange[0] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_ADM) >= '").append(TimeTools.formatDateTime(admissionRange[0], YYYY_MM_DD))
							.append("'");
				}
				if (admissionRange[1] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_ADM) <= '").append(TimeTools.formatDateTime(admissionRange[1], YYYY_MM_DD))
							.append("'");
				}
			}
			if (dischargeRange != null) {
				
				if (dischargeRange[0] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_DIS) >= '").append(TimeTools.formatDateTime(dischargeRange[0], YYYY_MM_DD))
							.append("'");
				}
				if (dischargeRange[1] != null) {
					rangePredicate.append(" and ").append("DATE(ADM_DATE_DIS) <= '").append(TimeTools.formatDateTime(dischargeRange[1], YYYY_MM_DD))
							.append("'");
				}
			}
			Query nativeQuery = this.entityManager.createNativeQuery(nativeQueryRanges.replace("param1", rangePredicate.toString()), "AdmittedPatient");
			String paramTerms = like(terms);
			nativeQuery.setParameter("param0", paramTerms);

			return parseResultSet(admittedPatients, nativeQuery);

		} else {

			Query nativeQuery = this.entityManager.createNativeQuery(nativeQueryTerms, "AdmittedPatient");
			String paramTerms = like(terms);
			nativeQuery.setParameter("param0", paramTerms);

			return parseResultSet(admittedPatients, nativeQuery);
		}
	}
	
	private List<AdmittedPatient> parseResultSet(List<AdmittedPatient> admittedPatients, Query nativeQuery) throws OHServiceException {
		List<Object[]> results = nativeQuery.getResultList();
		results.stream().forEach(resultRecord -> {
			Patient patientRecord = (Patient)resultRecord[0];
			Admission admissionRecord = (Admission)resultRecord[1];
			admittedPatients.add(new AdmittedPatient(patientRecord, admissionRecord));
		});
		return admittedPatients;
	}

	private String like(String[] terms) {
		StringBuilder sb = new StringBuilder("%");

		// result of type "%term0%term1%...%termN%"
		for (String term : terms) {
			sb.append(term).append("%");
		}
		return sb.toString();
	}

	private String[] getTermsToSearch(String searchTerms) {
		String[] terms = {};

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			terms = searchTerms.split(" ");
		}
		return terms;
	}

}
