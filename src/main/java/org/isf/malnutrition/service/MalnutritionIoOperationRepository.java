package org.isf.malnutrition.service;

import java.util.List;

import org.isf.malnutrition.model.Malnutrition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MalnutritionIoOperationRepository extends JpaRepository<Malnutrition, Integer> {

    @Query(value = "SELECT m FROM Malnutrition m WHERE m.admission.id = :id ORDER BY m.dateSupp")
    List<Malnutrition> findAllWhereAdmissionByOrderDate(@Param("id") int admissionID);
    
    @Query(value = "SELECT m FROM Malnutrition m WHERE m.admission.id = :id ORDER BY m.dateSupp")
    List<Malnutrition> findAllWhereAdmissionByOrderDateDesc(@Param("id") int admissionID);
    
}