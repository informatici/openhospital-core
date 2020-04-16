package org.isf.examination.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationOperations;
import org.isf.generaldata.ExaminationParameters;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExaminationBrowserManager {
	
	@Autowired
	private ExaminationOperations ioOperations;

	protected LinkedHashMap<String, String> auscultationHashMap;

	/**
	 * Default PatientExamination
	 */
	public PatientExamination getDefaultPatientExamination(	Patient patient){
		PatientExamination defaultPatient = new PatientExamination(new Timestamp(new Date().getTime()), patient, ExaminationParameters.HEIGHT_INIT, ExaminationParameters.WEIGHT_INIT,
				ExaminationParameters.AP_MIN, ExaminationParameters.AP_MAX, ExaminationParameters.HR_INIT, ExaminationParameters.TEMP_INIT, ExaminationParameters.SAT_INIT, ExaminationParameters.RR_INIT, ExaminationParameters.AUSC_INIT, "");
		return defaultPatient;
	}

	/**
	 * Get from last PatientExamination (only height, weight & note)
	 */
	public PatientExamination getFromLastPatientExamination(PatientExamination lastPatientExamination){
		PatientExamination newPatientExamination = new PatientExamination(new Timestamp(new Date().getTime()), lastPatientExamination.getPatient(), lastPatientExamination.getPex_height(),
				lastPatientExamination.getPex_weight(), lastPatientExamination.getPex_pa_min(), lastPatientExamination.getPex_pa_max(), lastPatientExamination.getPex_fc(), 
				lastPatientExamination.getPex_temp(), lastPatientExamination.getPex_sat(), lastPatientExamination.getPex_rr(), lastPatientExamination.getPex_ausc(), lastPatientExamination.getPex_note());
		return newPatientExamination;
	}

	private void buildAuscultationHashMap() {
		auscultationHashMap = new LinkedHashMap<String, String>();
		auscultationHashMap.put("unknown", MessageBundle.getMessage("angal.examination.auscultation.unknown"));
		auscultationHashMap.put("normal", MessageBundle.getMessage("angal.examination.auscultation.normal"));
		auscultationHashMap.put("wheezes", MessageBundle.getMessage("angal.examination.auscultation.wheezes"));
		auscultationHashMap.put("rhonchi", MessageBundle.getMessage("angal.examination.auscultation.rhonchi"));
		auscultationHashMap.put("crackles", MessageBundle.getMessage("angal.examination.auscultation.crackles"));
		auscultationHashMap.put("stridor", MessageBundle.getMessage("angal.examination.auscultation.stridor"));
		auscultationHashMap.put("bronchial", MessageBundle.getMessage("angal.examination.auscultation.bronchial"));
	}

	public String[] getAuscultationList() {	
		if (auscultationHashMap == null) buildAuscultationHashMap();
		String[] auscultationDescriptionList = auscultationHashMap.values().toArray(new String[0]);
		return auscultationDescriptionList;
	}

	public String getAuscultationTranslated(String auscultationKey) {
		if (auscultationHashMap == null) buildAuscultationHashMap();
		if (auscultationKey == null || !auscultationHashMap.containsKey(auscultationKey)) 
			return MessageBundle.getMessage("angal.examination.auscultation.unknown"); 
		else return auscultationHashMap.get(auscultationKey);
	}

	public String getAuscultationKey(String description) {
		if (auscultationHashMap == null) buildAuscultationHashMap();
		String key = "undefined";
		for (String value : auscultationHashMap.keySet()) {
			if (auscultationHashMap.get(value).equals(description)) {
				key = value;
				break;
			}
		}
		return key;
	}

	/**
	 * 
	 * @param path - the PatientHistory to save
	 * @throws OHServiceException 
	 */
	public void saveOrUpdate(PatientExamination patex) throws OHServiceException {
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

}
