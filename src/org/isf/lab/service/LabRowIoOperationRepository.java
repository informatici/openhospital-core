package org.isf.lab.service;

import org.isf.lab.model.LaboratoryRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.ArrayList;

public interface LabRowIoOperationRepository extends JpaRepository<LaboratoryRow, Integer> {
	@Modifying
  void deleteByLaboratory_Code(Integer code);
  ArrayList<LaboratoryRow> findByLaboratory_Code(Integer id);
}