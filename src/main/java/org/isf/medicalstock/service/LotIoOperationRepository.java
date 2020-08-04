package org.isf.medicalstock.service;

import java.util.List;

import org.isf.medicalstock.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LotIoOperationRepository extends JpaRepository<Lot, String> {

	@Query("select l from Lot l left join l.movements m where m.medical.code = :medical group by l.code")
	List<Lot> findByMovements_MedicalOrderByDueDate(@Param("medical") int medicalCode);

	@Query(value = "select LT_ID_A,LT_PREP_DATE,LT_DUE_DATE,LT_COST,"
			+ "SUM(IF(MMVT_TYPE LIKE '%+%',MMV_QTY,-MMV_QTY)) as quantity from "
			+ "((MEDICALDSRLOT join MEDICALDSRSTOCKMOV on MMV_LT_ID_A=LT_ID_A) join MEDICALDSR on MMV_MDSR_ID=MDSR_ID)"
			+ " join MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A "
			+ "where LT_ID_A=:code group by LT_ID_A order by LT_DUE_DATE", nativeQuery = true)
	List<Object[]> findAllWhereLot(@Param("code") String code);
}
