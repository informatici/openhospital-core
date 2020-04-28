package org.isf.patient.service;

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
    @Query(value = "UPDATE PATIENT SET PAT_DELETED = 'Y' WHERE PAT_ID = :id", nativeQuery = true)
    int updateDeleted(@Param("id") Integer id);
            
    List<Patient> findByNameAndDeleted(String name, String deletedStatus);

    @Query(value = "select max(p.code) from Patient p")
    Integer findMaxCode();
 		
    @Modifying
    @Transactional
    @Query(value = "UPDATE Patient p SET p.deleted = 'Y' WHERE p.code = :id")
    int updateDelete(@Param("id") Integer id); 		

}