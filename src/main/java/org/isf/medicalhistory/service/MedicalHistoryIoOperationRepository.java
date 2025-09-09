package org.isf.medicalhistory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.medicalhistory.model.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalHistoryIoOperationRepository extends JpaRepository<MedicalHistory, Integer> {
	
	@Query(value = "select mh from MedicalHistory mh where mh.patient.code = :patientCode")
	MedicalHistory findByPatientCode(@Param("patientCode") int patientCode);

	@Query("select mh from MedicalHistory mh " +
		"where mh.patient.code = :patientCode " +
		"and (mh.createdDate >= :performedAt and mh.createdDate < :closedAt)")
	List<MedicalHistory> findByPatientCodeCreatedDateBetween(@Param("patientCode") Integer patientCode, @Param("performedAt") LocalDateTime performedAt,
																@Param("closedAt") LocalDateTime closedAt);
}
