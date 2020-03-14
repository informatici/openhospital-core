package org.isf.exa.service;

import java.util.List;

import org.isf.exa.model.ExamRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRowIoOperationRepository extends JpaRepository<ExamRow, Integer> {
    List<ExamRow> findAllByCodeOrderByDescription(int code);
    List<ExamRow> findAllByDescriptionOrderByDescriptionAsc(String description);
    List<ExamRow> findAllByCodeAndDescriptionOrderByCodeAscDescriptionAsc(int code, String description);
    @Modifying
    void deleteByExam_Code(String code);
    List<ExamRow> findAllByExam_CodeOrderByDescription(String examCode);
}