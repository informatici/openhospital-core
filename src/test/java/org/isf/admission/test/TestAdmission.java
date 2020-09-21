/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

public class TestAdmission 
{	
	private int id = 0;	
	private int admitted = 1;	
	private String type = "T"; 
	private int yProg = 0;		
	private LocalDateTime now = LocalDateTime.now();
	private LocalDateTime admDate = now.withMonth(9).withDayOfMonth(8);
	private String FHU = "TestFHU";		
	private String opResult = "Result";
	private LocalDateTime opDate = LocalDateTime.of(2015, 9, 8, 0, 0);
	private LocalDateTime disDate = LocalDateTime.of(2015, 7, 6, 0, 0); 	
	private String note = "TestNote";
	private Float transUnit = (float)10.10;	
	private LocalDateTime visitDate = LocalDateTime.of(2015, 5, 4, 0, 0);
	private LocalDateTime deliveryDate = LocalDateTime.of(2015, 3, 2, 0, 0);
	private Float weight = (float)20.20;		
	private LocalDateTime ctrlDate1 =  LocalDateTime.of(2015, 2, 1, 0, 0);
	private LocalDateTime ctrlDate2 =  LocalDateTime.of(2015, 1, 2, 0, 0);
	private LocalDateTime abortDate =  LocalDateTime.of(2015, 3, 4, 0, 0);
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
			boolean usingSet) throws OHException 
	{
		Admission admission;
	
				
		if (usingSet)
		{
			admission = new Admission();
			_setParameters(admission, ward, patient, admissionType, diseaseIn, diseaseOut1, diseaseOut2, 	
					diseaseOut3, operation, dischargeType, pregTreatmentType, deliveryType,	deliveryResult);
		}
		else
		{
			// Create Admission with all parameters 
			admission = new Admission(id, admitted, type, ward, yProg, patient, admDate, admissionType, FHU, diseaseIn, 
					diseaseOut1, diseaseOut2, diseaseOut3, operation, opResult, opDate, disDate, dischargeType, note, 
					transUnit, visitDate, pregTreatmentType, deliveryDate, deliveryType, deliveryResult, weight, 
					ctrlDate1, ctrlDate2, abortDate, userID, deleted);
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
			DeliveryResultType deliveryResult) 
	{	
		admission.setAbortDate(abortDate);
		admission.setAdmDate(admDate);
		admission.setAdmitted(admitted);
		admission.setAdmType(admissionType);
		admission.setCtrlDate1(ctrlDate1);
		admission.setCtrlDate2(ctrlDate2);
		admission.setDeleted(deleted);
		admission.setDeliveryDate(deliveryDate);
		admission.setDeliveryResult(deliveryResult);
		admission.setDeliveryType(deliveryType);
		admission.setDisDate(disDate);
		admission.setDiseaseIn(diseaseIn);
		admission.setDiseaseOut1(diseaseOut1);
		admission.setDiseaseOut2(diseaseOut2);
		admission.setDiseaseOut3(diseaseOut3);
		admission.setDisType(dischargeType);
		admission.setFHU(FHU);
		admission.setNote(note);
		admission.setOpDate(opDate);
		admission.setOperation(operation);
		admission.setOpResult(opResult);
		admission.setPatient(patient);
		admission.setPregTreatmentType(pregTreatmentType);
		admission.setTransUnit(transUnit);
		admission.setType(type);
		admission.setUserID(userID);
		admission.setVisitDate(visitDate);
		admission.setWard(ward);
		admission.setWeight(weight);
		admission.setYProg(yProg);
	}
	
	public void check(
			Admission admission) 
	{		
    	assertThat(admission.getAbortDate()).isEqualTo(abortDate);
    	assertThat(admission.getAdmDate()).isEqualTo(admDate);
    	assertThat(admission.getAdmitted()).isEqualTo(admitted);
    	assertThat(admission.getCtrlDate1()).isEqualTo(ctrlDate1);
    	assertThat(admission.getCtrlDate2()).isEqualTo(ctrlDate2);
    	assertThat(admission.getDeleted()).isEqualTo(deleted);
    	assertThat(admission.getDeliveryDate()).isEqualTo(deliveryDate);
    	assertThat(admission.getDisDate()).isEqualTo(disDate);
    	assertThat(admission.getFHU()).isEqualTo(FHU);
    	assertThat(admission.getNote()).isEqualTo(note);
    	assertThat(admission.getOpDate()).isEqualTo(opDate);
    	assertThat(admission.getOpResult()).isEqualTo(opResult);
    	assertThat(admission.getTransUnit()).isEqualTo(transUnit);
    	assertThat(admission.getType()).isEqualTo(type);
    	assertThat(admission.getUserID()).isEqualTo(userID);
    	assertThat(admission.getVisitDate()).isEqualTo(visitDate);
    	assertThat(admission.getWeight()).isEqualTo(weight);
    	assertThat(admission.getYProg()).isEqualTo(yProg);
	}
}
