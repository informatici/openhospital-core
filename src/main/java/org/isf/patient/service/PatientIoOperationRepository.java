package org.isf.patient.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.patient.model.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface PatientIoOperationRepository extends JpaRepository<Patient, Integer>, PatientIoOperationRepositoryCustom {
    
    List<Patient> findByDeletedOrDeletedIsNull(String deletionStatus);

    List<Patient> findAllByDeletedIsNullOrDeletedEqualsOrderByName(String patDeleted, Pageable pageable);
    
    @Query("select p from Patient p where p.name = :name and (p.deleted = :deletedStatus or p.deleted is null) order by p.secondName, p.firstName")
	List<Patient> findByNameAndDeletedOrderByName(@Param("name") String name, @Param("deletedStatus") String deletedStatus);

	@Query("select p from Patient p where p.code = :id and (p.deleted = :deletedStatus or p.deleted is null)")
	List<Patient> findAllWhereIdAndDeleted(@Param("id") Integer id, @Param("deletedStatus") String deletedStatus);
    
    @Modifying
    @Transactional
    @Query(value = "update Patient p set p.deleted = 'Y' where p.code = :id")
    int updateDeleted(@Param("id") Integer id);
            
    List<Patient> findByNameAndDeleted(String name, String deletedStatus);

    @Query(value = "select max(p.code) from Patient p")
    Integer findMaxCode();

	@Query(value = "SELECT * FROM PATIENT "
		+"LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID ORDER BY PEX_DATE DESC) "
		+"AS HW ON PAT_ID = HW.PEX_PAT_ID WHERE (PAT_DELETED='N' or PAT_DELETED is null) "
		+"AND (PAT_AFFILIATED_PERSON < 1 OR PAT_AFFILIATED_PERSON is null) "
		+"AND (PAT_IS_HEAD_AFFILIATION = 1) "
		+"ORDER BY PAT_ID DESC",
		nativeQuery= true)
	ArrayList<Patient> getPatientsHeadWithHeightAndWeight();
}