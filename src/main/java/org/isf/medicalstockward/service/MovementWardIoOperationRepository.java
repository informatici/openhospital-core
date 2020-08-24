package org.isf.medicalstockward.service;

import org.isf.medicalstockward.model.MovementWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Repository
public interface MovementWardIoOperationRepository extends JpaRepository<MovementWard, Integer>{
	@Query(value = "select movWard from MovementWard movWard where movWard.wardTo.code=:idWardTo and (movWard.date>= :dateFrom and movWard.date < :dateTo)")
	ArrayList<MovementWard> findWardMovements(@Param("idWardTo") String idWardTo,
											  @Param("dateFrom") GregorianCalendar dateFrom,
											  @Param("dateTo") GregorianCalendar dateTo);

	List<MovementWard> findByPatient_code(int code);

	@Query(value = "SELECT * FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_PAT_ID = :patId", nativeQuery= true)
	ArrayList<MovementWard> findWardMovementPat(@Param("patId") Integer patId);
}
