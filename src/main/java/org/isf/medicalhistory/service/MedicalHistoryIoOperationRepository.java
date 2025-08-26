package org.isf.medicalhistory.service;

import java.util.List;

import org.isf.medicalhistory.model.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalHistoryIoOperationRepository extends JpaRepository<MedicalHistory, Integer> {
	List<MedicalHistory> findByPatientCode(Integer patientCode);
}
