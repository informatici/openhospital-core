package org.isf.exa.service;

import java.util.List;

import org.isf.exa.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamIoOperationRepository extends JpaRepository<Exam, String> {
    List<Exam> findByOrderByExamtypeDescriptionAscDescriptionAsc();
    List<Exam> findByDescriptionContainingOrderByExamtypeDescriptionAscDescriptionAsc(String description);
}