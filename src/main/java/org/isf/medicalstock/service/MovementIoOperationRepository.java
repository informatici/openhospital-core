package org.isf.medicalstock.service;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementIoOperationRepository extends JpaRepository<Movement, Integer>, MovementIoOperationRepositoryCustom {    
    @Query(value = "select m from Movement m join m.medical med where med.code = :code")
    Movement findAllByMedicalCode(@Param("code") Integer code);

	@Query(value = "select m from Movement m join m.medical med where med.code = :code")
	Movement findAllByMedicalCodeOrderByLot_(@Param("code") Integer code);
    
    @Query(value = "select distinct med.code from Movement mov " +
			"join mov.medical med " +
			"join mov.type movtype " +
			"join mov.lot lot " +
			"where lot.code=:lot")
    List<Integer> findAllByLot(@Param("lot") String lot);
    
    @Query(value = "select mov from Movement mov " +
			"join mov.type movtype " +
			"left join mov.lot lot " +
			"left join mov.ward ward " +
			"where mov.refNo = :refNo order by mov.date, mov.refNo")
    List<Movement> findAllByRefNo(@Param("refNo") String refNo);

    List<Movement> findByLot(Lot lot);
    
    @Query(value = "select max(mov.date) from Movement mov")
	GregorianCalendar findMaxDate();

    @Query(value = "select mov.refNo from Movement mov where mov.refNo like :refNo")
    List<String> findAllWhereRefNo(@Param("refNo") String refNo);
}
