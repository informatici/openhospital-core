package org.isf.operation.service;

import org.isf.operation.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationIoOperationRepository extends JpaRepository<Operation, String> {
    List<Operation> findByOrderByDescriptionDesc();
	List<Operation> findAllByDescriptionContainsOrderByDescriptionDesc(String type);
    Operation findOneByDescriptionAndType_Code(String description, String type);
    Operation findByCode(String code);
}