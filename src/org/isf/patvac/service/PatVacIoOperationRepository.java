package org.isf.patvac.service;

import org.isf.patvac.model.PatientVaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.GregorianCalendar;
import java.util.List;

@Repository
public interface PatVacIoOperationRepository extends JpaRepository<PatientVaccine, Integer>, PatVacIoOperationRepositoryCustom {

	@Query("select max(pv.progr) from PatientVaccine pv")
    Integer findMaxCode();
    
	@Query("select max(pv.progr) from PatientVaccine pv where pv.vaccineDate >= :yearStart and pv.vaccineDate < :yearEnd")
	Integer findMaxCodeWhereVaccineDate(@Param("yearStart") GregorianCalendar yearStart, @Param("yearEnd") GregorianCalendar yearEnd);

    List<PatientVaccine> findByPatient_code(int patientId);
}