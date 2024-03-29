package org.isf.lab.service;

import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.Laboratory;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class LabPrintOperations {

	private final LabIoOperationRepository repository;

	public LabPrintOperations(LabIoOperationRepository repository) {
		this.repository = repository;
	}

	public List<LaboratoryForPrint> getLaboratoryForPrint() throws OHServiceException {
		LocalDateTime time2 = TimeTools.getNow();
		LocalDateTime time1 = time2.minusWeeks(1);
		return getLaboratoryForPrint(null, time1, time2);
	}

	public List<LaboratoryForPrint> getLaboratoryForPrint(String exam, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		List<LaboratoryForPrint> pLaboratory = new ArrayList<>();
		Iterable<Laboratory> laboritories = exam != null
			? repository.findByLabDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(
			TimeTools.truncateToSeconds(dateFrom),
			TimeTools.truncateToSeconds(dateTo),
			exam)
			: repository.findByLabDateBetweenOrderByExam_Examtype_DescriptionDesc(TimeTools.truncateToSeconds(dateFrom),
			TimeTools.truncateToSeconds(dateTo));

		for (Laboratory laboratory : laboritories) {
			pLaboratory.add(new LaboratoryForPrint(
				laboratory.getCode(),
				laboratory.getExam(),
				laboratory.getLabDate(),
				laboratory.getResult()));
		}
		return pLaboratory;
	}

	public List<LaboratoryForPrint> getLaboratoryForPrint(String exam, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		List<LaboratoryForPrint> pLaboratory = new ArrayList<>();
		List<Laboratory> laboritories = new ArrayList<>();
		if (exam != null && patient != null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionAndPatientCode(dateFrom, dateTo, exam, patient.getCode());
		}
		if (exam != null && patient == null) {
			laboritories = repository.findByLabDateBetweenAndExam_DescriptionOrderByLabDateDesc(dateFrom, dateTo, exam);
		}
		if (patient != null && exam == null) {
			laboritories = repository.findByLabDateBetweenAndPatientCode(dateFrom, dateTo, patient.getCode());
		}
		if (patient == null && exam == null) {
			laboritories = repository.findByLabDateBetweenOrderByLabDateDesc(dateFrom, dateTo);
		}
		for (Laboratory laboratory : laboritories) {

			pLaboratory.add(new LaboratoryForPrint(
				laboratory.getCode(),
				laboratory.getExam(),
				laboratory.getLabDate(),
				laboratory.getResult(),
				laboratory.getPatName(),
				laboratory.getPatient().getCode()));
		}
		return pLaboratory;
	}
}
