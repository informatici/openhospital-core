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
package org.isf.examination.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationOperations;
import org.isf.generaldata.ExaminationParameters;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.validator.DefaultSorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExaminationBrowserManager {
	
	@Autowired
	private ExaminationOperations ioOperations;
	protected HashMap<String, String> diuresisDescriptionHashMap;
	protected HashMap<String, String> bowelDescriptionHashMap;
	protected LinkedHashMap<String, String> auscultationHashMap;

	/**
	 * Default PatientExamination
	 */
	public PatientExamination getDefaultPatientExamination(Patient patient){
		PatientExamination defaultPatient = new PatientExamination(
				LocalDateTime.now(), 
				patient, 
				new Integer(ExaminationParameters.HEIGHT_INIT), 
				new Double(ExaminationParameters.WEIGHT_INIT), 
				new Integer(ExaminationParameters.AP_MIN_INIT), 
				new Integer(ExaminationParameters.AP_MAX_INIT),
				new Integer(ExaminationParameters.HR_INIT), 
				new Double(ExaminationParameters.TEMP_INIT), 
				new Double(ExaminationParameters.SAT_INIT), 
				new Integer(ExaminationParameters.HGT_INIT),
				new Integer(ExaminationParameters.DIURESIS_INIT),
				ExaminationParameters.DIURESIS_DESC_INIT,
				ExaminationParameters.BOWEL_DESC_INIT,
				new Integer(ExaminationParameters.RR_INIT),
				ExaminationParameters.AUSCULTATION_INIT,
				"");
		return defaultPatient;
	}

	/**
	 * Get from last PatientExamination (only height, weight & note)
	 */
	public PatientExamination getFromLastPatientExamination(PatientExamination lastPatientExamination){
		PatientExamination newPatientExamination = new PatientExamination(LocalDateTime.now(), lastPatientExamination.getPatient(), lastPatientExamination.getPex_height(),
				lastPatientExamination.getPex_weight(), lastPatientExamination.getPex_ap_min(), lastPatientExamination.getPex_ap_max(), lastPatientExamination.getPex_hr(), 
				lastPatientExamination.getPex_temp(), lastPatientExamination.getPex_sat(), lastPatientExamination.getPex_hgt(),
				lastPatientExamination.getPex_diuresis(), lastPatientExamination.getPex_diuresis_desc(), lastPatientExamination.getPex_bowel_desc(), lastPatientExamination.getPex_rr(), 
				lastPatientExamination.getPex_auscultation(),lastPatientExamination.getPex_note());
		return newPatientExamination;
	}

	private void buildAuscultationHashMap() {
		auscultationHashMap = new LinkedHashMap<String, String>();
		auscultationHashMap.put("normal", MessageBundle.getMessage("angal.examination.auscultation.normal"));
		auscultationHashMap.put("wheezes", MessageBundle.getMessage("angal.examination.auscultation.wheezes"));
		auscultationHashMap.put("rhonchi", MessageBundle.getMessage("angal.examination.auscultation.rhonchi"));
		auscultationHashMap.put("crackles", MessageBundle.getMessage("angal.examination.auscultation.crackles"));
		auscultationHashMap.put("stridor", MessageBundle.getMessage("angal.examination.auscultation.stridor"));
		auscultationHashMap.put("bronchial", MessageBundle.getMessage("angal.examination.auscultation.bronchial"));
	}

	public ArrayList<String> getAuscultationList() {	
		if (auscultationHashMap == null) buildAuscultationHashMap();
		ArrayList<String> auscultationDescriptionList = new ArrayList<String>(auscultationHashMap.values());
		Collections.sort(auscultationDescriptionList, new DefaultSorter(MessageBundle.getMessage("angal.examination.auscultation.normal")));
		return auscultationDescriptionList;
	}

	public String getAuscultationTranslated(String auscultationKey) {
		if (auscultationHashMap == null) buildAuscultationHashMap();
		return auscultationHashMap.get(auscultationKey);
	}

	public String getAuscultationKey(String description) {
		if (auscultationHashMap == null) buildAuscultationHashMap();
		String key = "";
		for (String value : auscultationHashMap.keySet()) {
			if (auscultationHashMap.get(value).equals(description)) {
				key = value;
				break;
			}
		}
		return key;
	}

	/**
	 * @param patex - the PatientExamination to save
	 * @throws OHServiceException 
	 */
	public void saveOrUpdate(PatientExamination patex) throws OHServiceException {
		List<OHExceptionMessage> errors = validateExamination(patex);
        if(!errors.isEmpty()){
            throw new OHServiceException(errors);
        }
        ioOperations.saveOrUpdate(patex);
	}

	public PatientExamination getByID(int id) throws OHServiceException{
        return ioOperations.getByID(id);
	}

	public PatientExamination getLastByPatID(int patID) throws OHServiceException {
		ArrayList<PatientExamination> patExamination = getByPatID(patID);
		
		return !patExamination.isEmpty() ? patExamination.get(0) : null;
	}

	public ArrayList<PatientExamination> getLastNByPatID(int patID, int number) throws OHServiceException {
        return ioOperations.getLastNByPatID(patID, number);
	}

	public ArrayList<PatientExamination> getByPatID(int patID) throws OHServiceException {
        return ioOperations.getByPatID(patID);
	}
	
	/**
	 * @param patexList - the {@link PatientExamination} to delete.
	 * @throws OHServiceException 
	 */
	public void remove(ArrayList<PatientExamination> patexList) throws OHServiceException {
		ioOperations.remove(patexList);
	}
	
	public String getBMIdescription(double bmi) {
		if (bmi < 16.5)
			return MessageBundle.getMessage("angal.examination.bmi.severeunderweight");
		if (bmi >= 16.5 && bmi < 18.5)
			return MessageBundle.getMessage("angal.examination.bmi.underweight");
		if (bmi >= 18.5 && bmi < 24.5)
			return MessageBundle.getMessage("angal.examination.bmi.normalweight");
		if (bmi >= 24.5 && bmi < 30)
			return MessageBundle.getMessage("angal.examination.bmi.overweight");
		if (bmi >= 30 && bmi < 35)
			return MessageBundle.getMessage("angal.examination.bmi.obesityclassilight");
		if (bmi >= 35 && bmi < 40)
			return MessageBundle.getMessage("angal.examination.bmi.obesityclassiimedium");
		if (bmi >= 40)
			return MessageBundle.getMessage("angal.examination.bmi.obesityclassiiisevere");
		return "";
	}
	
	private void buildDiuresisDescriptionHashMap() {
		diuresisDescriptionHashMap = new HashMap<String, String>();
		diuresisDescriptionHashMap.put("physiological", MessageBundle.getMessage("angal.examination.diuresis.physiological"));
		diuresisDescriptionHashMap.put("oliguria", MessageBundle.getMessage("angal.examination.diuresis.oliguria"));
		diuresisDescriptionHashMap.put("anuria", MessageBundle.getMessage("angal.examination.diuresis.anuria"));
		diuresisDescriptionHashMap.put("frequent", MessageBundle.getMessage("angal.examination.diuresis.frequent"));
		diuresisDescriptionHashMap.put("nocturia", MessageBundle.getMessage("angal.examination.diuresis.nocturia"));
		diuresisDescriptionHashMap.put("stranguria", MessageBundle.getMessage("angal.examination.diuresis.stranguria"));
		diuresisDescriptionHashMap.put("hematuria", MessageBundle.getMessage("angal.examination.diuresis.hematuria"));
		diuresisDescriptionHashMap.put("pyuria", MessageBundle.getMessage("angal.examination.diuresis.pyuria"));
	}
	
	private void buildBowelDescriptionHashMap() {
		bowelDescriptionHashMap = new HashMap<String, String>();
		bowelDescriptionHashMap.put("regular", MessageBundle.getMessage("angal.examination.bowel.regular"));
		bowelDescriptionHashMap.put("irregular", MessageBundle.getMessage("angal.examination.bowel.irregular"));
		bowelDescriptionHashMap.put("constipation", MessageBundle.getMessage("angal.examination.bowel.constipation"));
		bowelDescriptionHashMap.put("diarrheal", MessageBundle.getMessage("angal.examination.bowel.diarrheal"));
	}

	/**
	 * return a list of diuresis descriptions:
	 * physiological,
	 * oliguria,
	 * anuria,
	 * fequent,
	 * nocturia,
	 * stranguria,
	 * hematuria,
	 * pyuria
	 * @return
	 */
	public ArrayList<String> getDiuresisDescriptionList() {
		if (diuresisDescriptionHashMap == null) buildDiuresisDescriptionHashMap();
		ArrayList<String> diuresisDescriptionList = new ArrayList<String>(diuresisDescriptionHashMap.values());
		Collections.sort(diuresisDescriptionList, new DefaultSorter(MessageBundle.getMessage("angal.examination.diuresis.physiological")));
		return diuresisDescriptionList;
	}
	
	/**
	 * return a list of bowel descriptions:
	 * regular,
	 * irregular,
	 * constipation,
	 * diarrheal
	 * @return
	 */
	public ArrayList<String> getBowelDescriptionList() {
		if (bowelDescriptionHashMap == null) buildBowelDescriptionHashMap();
		ArrayList<String> bowelDescriptionList = new ArrayList<String>(bowelDescriptionHashMap.values());
		Collections.sort(bowelDescriptionList,  new DefaultSorter(MessageBundle.getMessage("angal.examination.bowel.regular")));
		return bowelDescriptionList;
	}
	
	public String getBowelDescriptionTranslated(String pex_bowel_desc_key) {
		if (bowelDescriptionHashMap == null) buildBowelDescriptionHashMap();
		return bowelDescriptionHashMap.get(pex_bowel_desc_key);
	}
	
	public String getBowelDescriptionKey(String description) {
		if (bowelDescriptionHashMap == null) buildBowelDescriptionHashMap();
		String key = "";
		for (String value : bowelDescriptionHashMap.keySet()) {
			if (bowelDescriptionHashMap.get(value).equals(description)) {
				key = value;
				break;
			}
		}
		return key;
	}
	
	public String getDiuresisDescriptionTranslated(String pex_diuresis_desc_key) {
		if (diuresisDescriptionHashMap == null) buildDiuresisDescriptionHashMap();
		return diuresisDescriptionHashMap.get(pex_diuresis_desc_key);
	}
	
	public String getDiuresisDescriptionKey(String description) {
		if (diuresisDescriptionHashMap == null) buildDiuresisDescriptionHashMap();
		String key = "";
		for (String value : diuresisDescriptionHashMap.keySet()) {
			if (diuresisDescriptionHashMap.get(value).equals(description)) {
				key = value;
				break;
			}
		}
		return key;
	}
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param patex
	 * @return list of {@link OHExceptionMessage}
	 */
	protected List<OHExceptionMessage> validateExamination(PatientExamination patex) {
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if (patex.getPex_diuresis_desc() != null && !diuresisDescriptionHashMap.keySet().contains(patex.getPex_diuresis_desc()))
        	errors.add(new OHExceptionMessage("diuresisDescriptionNotAllowed", 
        		MessageBundle.getMessage("angal.examination.pleaseinsertavaliddiuresisdescription"), 
        		OHSeverityLevel.ERROR));
        if (patex.getPex_bowel_desc() != null && !bowelDescriptionHashMap.keySet().contains(patex.getPex_bowel_desc()))
        	errors.add(new OHExceptionMessage("bowelDescriptionNotAllowed", 
        		MessageBundle.getMessage("angal.examination.pleaseinsertavalidboweldescription"), 
        		OHSeverityLevel.ERROR));
        if (patex.getPex_auscultation() != null && !auscultationHashMap.keySet().contains(patex.getPex_auscultation()))
        	errors.add(new OHExceptionMessage("auscultationDescriptionNotAllowed", 
        		MessageBundle.getMessage("angal.examination.pleaseinsertavalidauscultationdescription"), 
        		OHSeverityLevel.ERROR));
        return errors;
    }
}
