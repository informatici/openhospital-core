package org.isf.medicalstock.service;

import java.util.List;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LotIoOperationRepository extends JpaRepository<Lot, String> {
	@Query("select l from Lot l left join l.movements m where m.medical.code = :medical group by l.code")
	List<Lot> findByMovements_MedicalOrderByDueDate(@Param("medical") int medicalCode);
}
