package org.isf.medicalstock.service;

import java.util.List;

import org.isf.medicalstock.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LotIoOperationRepository extends JpaRepository<Lot, String> {
	@Query(value = "select l.code,l.preparationDate,l.dueDate,l.cost,mt.type,mov.quantity " +
			"from Movement mov join mov.lot l " +
			"join mov.medical m " +
			"join mov.type mt "+
			"where m.code=:code and mov.quantity <> 0 order by l.dueDate")
	List<Object[]> findAllWhereMedical(@Param("code") Integer code);
}
