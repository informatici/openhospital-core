package org.isf.operation.service;

import org.isf.operation.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface OperationIoOperationRepository extends JpaRepository<Operation, String> {
    List<Operation> findByOrderByDescriptionDesc();
	List<Operation> findAllByDescriptionContainsOrderByDescriptionDesc(String type);
    Operation findOneByDescriptionAndType_Code(String description, String type);
    Operation findByCode(String code);
	@Query(value = "SELECT * FROM OPERATION JOIN OPERATIONTYPE ON OPE_OCL_ID_A = OCL_ID_A WHERE OPE_FOR LIKE 1 OR  OPE_FOR LIKE 3  ORDER BY OPE_DESC", nativeQuery= true)
	ArrayList<Operation> findAllWithoutDescriptionOpd();
	@Query(value = "SELECT * FROM OPERATION JOIN OPERATIONTYPE ON OPE_OCL_ID_A = OCL_ID_A WHERE OPE_FOR LIKE 1 OR  OPE_FOR LIKE 2  ORDER BY OPE_DESC", nativeQuery= true)
	ArrayList<Operation> findAllWithoutDescriptionAdm();
}