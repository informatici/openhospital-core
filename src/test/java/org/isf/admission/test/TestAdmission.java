/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.GregorianCalendar;

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

	// set dates in reasonable order to pass any validatation tests
	private static final GregorianCalendar ADMINDATE = new GregorianCalendar(2001, 0, 28);
	private static final GregorianCalendar VISITDATE = new GregorianCalendar(2002, 1, 28);
	private static final GregorianCalendar OPDATE = new GregorianCalendar(2003, 2, 1);
	private static final GregorianCalendar ABORTDATE = null;
	private static final GregorianCalendar DELIVERYDATE = new GregorianCalendar(2004, 11, 1);
	private static final GregorianCalendar CTRLDATE1 = new GregorianCalendar(2005, 2, 1);
	private static final GregorianCalendar CTRLDATE2 = new GregorianCalendar(2005, 2, 2);
	private static final GregorianCalendar DISDATE = new GregorianCalendar(2006, 11, 1);

	private int id = 0;
	private int admitted = 1;
	private String type = "T";
	private int yProg = 0;
	private String FHU = "TestFHU";
	private String opResult = "Result";
	private String note = "TestNote";
	private Float transUnit = (float) 10.10;
	private Float weight = (float) 20.20;
	private String userID = "TestUserId";
	private String deleted = "N";

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
			_setParameters(admission, ward, patient, admissionType, diseaseIn, diseaseOut1, diseaseOut2,
					diseaseOut3, operation, dischargeType, pregTreatmentType, deliveryType, deliveryResult);
		} else {
			// Create Admission with all parameters 
			admission = new Admission(id, admitted, type, ward, yProg, patient, ADMINDATE, admissionType, FHU, diseaseIn,
					diseaseOut1, diseaseOut2, diseaseOut3, operation, opResult, OPDATE, DISDATE, dischargeType, note,
					transUnit, VISITDATE, pregTreatmentType, DELIVERYDATE, deliveryType, deliveryResult, weight,
					CTRLDATE1, CTRLDATE2, ABORTDATE, userID, deleted);
		}

		return admission;
	}

	public void _setParameters(
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
		admission.setOpDate(OPDATE);
		admission.setOperation(operation);
		admission.setOpResult(opResult);
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
		assertThat(admission.getOpDate()).isEqualTo(OPDATE);
		assertThat(admission.getOpResult()).isEqualTo(opResult);
		assertThat(admission.getTransUnit()).isEqualTo(transUnit);
		assertThat(admission.getType()).isEqualTo(type);
		assertThat(admission.getUserID()).isEqualTo(userID);
		assertThat(admission.getVisitDate()).isEqualTo(VISITDATE);
		assertThat(admission.getWeight()).isEqualTo(weight);
		assertThat(admission.getYProg()).isEqualTo(yProg);
	}
}
