package org.isf.examination.service;

import java.util.List;

import org.isf.examination.model.PatientExamination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationIoOperationRepository extends JpaRepository<PatientExamination, Integer> {
		@Query(value = "select p from PatientExamination p where p.patient.code = :patientCode")
		List<PatientExamination> findByPatient_CodeOrderByPexDateDesc(@Param("patientCode") int patientCode);
		@Query(value = "select p from PatientExamination p where p.patient.code = :patientCode")
		Page<PatientExamination> findByPatient_CodeOrderByPexDateDesc(@Param("patientCode") int patientCode, Pageable pageable);
}