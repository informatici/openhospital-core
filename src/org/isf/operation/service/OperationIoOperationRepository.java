package org.isf.operation.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.operation.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationIoOperationRepository extends JpaRepository<Operation, String> {
    List<Operation> findByOrderByDescriptionDesc();
	List<Operation> findAllByDescriptionContainsOrderByDescriptionDesc(String type);
   // @Query(value = "SELECT * FROM OPERATION WHERE OPE_DESC = :description AND OPE_OCL_ID_A = :type", nativeQuery= true)
    Operation findOneByDescriptionAndType_Code(@Param("description") String description, @Param("type") String type);
    
    Operation findByCode(String code);
}