package org.isf.medicalstockward.service;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.isf.medicalstockward.model.MovementWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovementPatWard extends JpaRepository<MovementWard, Integer>{      
    @Query(value = "SELECT * FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_PAT_ID = :patId", nativeQuery= true)
    public ArrayList<MovementWard> findWardMovementPat(@Param("patId") Integer patId);

}
