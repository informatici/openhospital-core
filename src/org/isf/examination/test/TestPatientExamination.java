package org.isf.examination.test;


import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.isf.examination.model.PatientExamination;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestPatientExamination 
{	
	private Timestamp pex_date = new Timestamp(1000);
	private Integer pex_height = 170;	
	private Double pex_weight = 60.;	
	private Integer pex_ap_min = 80;	
	private Integer pex_ap_max = 120;	
	private Integer pex_hr = 60;	
	private Double pex_temp = 36.;	
	private Double pex_sat = 1.;	
	private Integer pex_hgt = 85;
	private Integer pex_diuresis = 100;
	private String pex_diuresis_desc = "physiological";
	private String pex_bowel_desc = "regular";
	private String pex_note = "";
	
	
	public PatientExamination setup(
			Patient patient,
			boolean usingSet) throws OHException 
	{
		PatientExamination patientExamination;
	

		if (usingSet)
		{
			patientExamination = new PatientExamination();
			_setParameters(patientExamination, patient);
		}
		else
		{
			// Create Patient Examination with all parameters 
			patientExamination = new PatientExamination(pex_date, patient, pex_height, pex_weight, 
					pex_ap_min, pex_ap_max, pex_hr, pex_temp, pex_sat, 
					pex_hgt, pex_diuresis, pex_diuresis_desc, pex_bowel_desc, pex_note);	
		}
		
		return patientExamination;
	}
	
	private void _setParameters(
			PatientExamination patientExamination,
			Patient patient) 
	{			
		patientExamination.setPatient(patient);
		patientExamination.setPex_date(pex_date);
		patientExamination.setPex_hr(pex_hr);
		patientExamination.setPex_height(pex_height);
		patientExamination.setPex_note(pex_note);
		patientExamination.setPex_ap_max(pex_ap_max);
		patientExamination.setPex_ap_min(pex_ap_min);
		patientExamination.setPex_sat(pex_sat);
		patientExamination.setPex_temp(pex_temp);
		patientExamination.setPex_weight(pex_weight);
		
		return;
	}
	
	public void check(
			PatientExamination patientExamination)  
	{		
		//assertEquals(pex_date, foundPatientExamination.getPex_date());
		assertEquals(pex_hr, patientExamination.getPex_hr());
		//assertEquals(pex_height, patientExamination.getPex_height());
		assertEquals(pex_note, patientExamination.getPex_note());
		assertEquals(pex_ap_max, patientExamination.getPex_ap_max());
		assertEquals(pex_ap_min, patientExamination.getPex_ap_min());
		//assertEquals(pex_sat, foundPatientExamination.getPex_sat());
		//assertEquals(pex_temp, foundPatientExamination.getPex_temp());
		assertEquals(pex_weight, patientExamination.getPex_weight());
		
		return;
	}	
}