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
package org.isf.admission.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.admission.model.Admission;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.model.Disease;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.operation.model.Operation;
import org.isf.patient.model.Patient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;

public class TestAdmission {

	// set dates in reasonable order to pass any validation tests
	private static final LocalDateTime ADMINDATE = LocalDateTime.of(2001, 1, 28, 0, 0);
	private static final LocalDateTime VISITDATE = LocalDateTime.of(2002, 2, 28, 0, 0);
	private static final LocalDateTime ABORTDATE = null;
	private static final LocalDateTime DELIVERYDATE = LocalDateTime.of(2004, 12, 1, 0, 0);
	private static final LocalDateTime CTRLDATE1 = LocalDateTime.of(2005, 3, 1, 0, 0);
	private static final LocalDateTime CTRLDATE2 = LocalDateTime.of(2005, 3, 2, 0, 0);
	private static final LocalDateTime DISDATE =  LocalDateTime.of(2006, 12, 1, 0, 0);

	private int id = 0;
	private int admitted = 1;
	private String type = "T";
	private int yProg = 0;
	private String FHU = "TestFHU";
	private String note = "TestNote";
	private Float transUnit = (float) 10.10;
	private Float weight = (float) 20.20;
	private String userID = "TestUserId";
	private char deleted = 'N';

	public Admission setup(
			Ward ward,
			Patient patient,
			AdmissionType admissionType,
			Disease diseaseIn,
			Disease diseaseOut1,
			Disease diseaseOut2,
			Disease diseaseOut3,
			Operation operation,
			DischargeType dischargeType,
			PregnantTreatmentType pregTreatmentType,
			DeliveryType deliveryType,
			DeliveryResultType deliveryResult,
			boolean usingSet) throws OHException {
		Admission admission;

		if (usingSet) {
			admission = new Admission();
			setParameters(admission, ward, patient, admissionType, diseaseIn, diseaseOut1, diseaseOut2,
					diseaseOut3, operation, dischargeType, pregTreatmentType, deliveryType, deliveryResult);
		} else {
			// Create Admission with all parameters 
			admission = new Admission(id, admitted, type, ward, yProg, patient, ADMINDATE, admissionType, FHU, diseaseIn,
					diseaseOut1, diseaseOut2, diseaseOut3, DISDATE, dischargeType, note,
					transUnit, VISITDATE, pregTreatmentType, DELIVERYDATE, deliveryType, deliveryResult, weight,
					CTRLDATE1, CTRLDATE2, ABORTDATE, userID, deleted);
		}

		return admission;
	}

	public void setParameters(
			Admission admission,
			Ward ward,
			Patient patient,
			AdmissionType admissionType,
			Disease diseaseIn,
			Disease diseaseOut1,
			Disease diseaseOut2,
			Disease diseaseOut3,
			Operation operation,
			DischargeType dischargeType,
			PregnantTreatmentType pregTreatmentType,
			DeliveryType deliveryType,
			DeliveryResultType deliveryResult) {
		admission.setAbortDate(ABORTDATE);
		admission.setAdmDate(ADMINDATE);
		admission.setAdmitted(admitted);
		admission.setAdmType(admissionType);
		admission.setCtrlDate1(CTRLDATE1);
		admission.setCtrlDate2(CTRLDATE2);
		admission.setDeleted(deleted);
		admission.setDeliveryDate(DELIVERYDATE);
		admission.setDeliveryResult(deliveryResult);
		admission.setDeliveryType(deliveryType);
		admission.setDisDate(DISDATE);
		admission.setDiseaseIn(diseaseIn);
		admission.setDiseaseOut1(diseaseOut1);
		admission.setDiseaseOut2(diseaseOut2);
		admission.setDiseaseOut3(diseaseOut3);
		admission.setDisType(dischargeType);
		admission.setFHU(FHU);
		admission.setNote(note);
		admission.setPatient(patient);
		admission.setPregTreatmentType(pregTreatmentType);
		admission.setTransUnit(transUnit);
		admission.setType(type);
		admission.setUserID(userID);
		admission.setVisitDate(VISITDATE);
		admission.setWard(ward);
		admission.setWeight(weight);
		admission.setYProg(yProg);
	}

	public void check(Admission admission) {
		assertThat(admission.getAbortDate()).isEqualTo(ABORTDATE);
		assertThat(admission.getAdmDate()).isEqualTo(ADMINDATE);
		assertThat(admission.getAdmitted()).isEqualTo(admitted);
		assertThat(admission.getCtrlDate1()).isEqualTo(CTRLDATE1);
		assertThat(admission.getCtrlDate2()).isEqualTo(CTRLDATE2);
		assertThat(admission.getDeleted()).isEqualTo(deleted);
		assertThat(admission.getDeliveryDate()).isEqualTo(DELIVERYDATE);
		assertThat(admission.getDisDate()).isEqualTo(DISDATE);
		assertThat(admission.getFHU()).isEqualTo(FHU);
		assertThat(admission.getNote()).isEqualTo(note);
		assertThat(admission.getTransUnit()).isEqualTo(transUnit);
		assertThat(admission.getType()).isEqualTo(type);
		assertThat(admission.getUserID()).isEqualTo(userID);
		assertThat(admission.getVisitDate()).isEqualTo(VISITDATE);
		assertThat(admission.getWeight()).isEqualTo(weight);
		assertThat(admission.getYProg()).isEqualTo(yProg);
	}
}
