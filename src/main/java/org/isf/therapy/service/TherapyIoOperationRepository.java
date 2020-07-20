package org.isf.therapy.service;

import java.util.List;

import org.isf.therapy.model.TherapyRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface TherapyIoOperationRepository extends JpaRepository<TherapyRow, Integer> {
    List<TherapyRow> findAllByOrderByPatIDCodeAscTherapyIDAsc();
    List<TherapyRow> findByPatIDCodeOrderByPatIDCodeAscTherapyIDAsc(Integer patient);
    @Modifying
    void deleteByPatIDCode(@Param("patient") Integer patient);
}