package org.isf.visits.service;

import java.util.List;

import org.isf.visits.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VisitsIoOperationRepository extends JpaRepository<Visit, Integer> {
    List<Visit> findAllByOrderByPatient_CodeAscDateAsc();
    List<Visit> findAllByPatient_CodeOrderByPatient_CodeAscDateAsc(@Param("patient") Integer patient);
    @Modifying
    void deleteByPatient_Code(@Param("patient") Integer patient);
}