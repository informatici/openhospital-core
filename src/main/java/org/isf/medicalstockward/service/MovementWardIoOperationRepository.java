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
    @Query(value = "select movWard from MovementWard movWard where movWard.wardTo=:idwardto and (movWard.date between :datefrom and :dateto)")
    ArrayList<MovementWard> findWardMovements(@Param("idwardto") String idWardTo,
                                              @Param("datefrom") GregorianCalendar dateFrom,
                                              @Param("dateto") GregorianCalendar dateTo);

    List<MovementWard> findByPatient_code(int code);
}
