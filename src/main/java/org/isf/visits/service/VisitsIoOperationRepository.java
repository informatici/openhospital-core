package org.isf.visits.service;

import java.util.List;

import org.isf.visits.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VisitsIoOperationRepository extends JpaRepository<Visit, Integer> {
    List<Visit> findAllByOrderByPatient_CodeAscDateAsc();
    List<Visit> findAllByPatient_CodeOrderByPatient_CodeAscDateAsc(@Param("patient") Integer patient);
    @Modifying
    void deleteByPatient_Code(@Param("patient") Integer patient);

	@Query(value = "SELECT * FROM VISITS WHERE VST_WRD_ID_A = :wardId ORDER BY VST_PAT_ID, VST_DATE", nativeQuery= true)
	List<Visit> findAllWhereWardByOrderPatientAndDateAsc(@Param("wardId") String wardId);

	@Query(value = "SELECT * FROM VISITS WHERE VST_WARD_ID = :ward ORDER BY VST_DATE", nativeQuery= true)
	List<Visit> findAllWhereWardByOrderDateAsc(@Param("ward") String ward);

	@Query(value = "SELECT * FROM VISITS WHERE VST_PAT_ID = :patient ORDER BY VST_PAT_ID, VST_DATE", nativeQuery= true)
	List<Visit> findAllWherePatientByOrderPatientAndDateAsc(@Param("patient") Integer patient);

}
