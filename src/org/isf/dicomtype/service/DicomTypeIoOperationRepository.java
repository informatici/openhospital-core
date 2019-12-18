package org.isf.dicomtype.service;

import java.util.List;

import org.isf.dicomtype.model.DicomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DicomTypeIoOperationRepository extends JpaRepository<DicomType, String> {

	List<DicomType> findAllByOrderByDicomTypeDescriptionAsc();
}