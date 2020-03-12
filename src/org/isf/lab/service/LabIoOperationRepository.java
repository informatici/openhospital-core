package org.isf.lab.service;

import java.util.GregorianCalendar;
import java.util.List;

import org.isf.lab.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabIoOperationRepository extends JpaRepository<Laboratory, Integer> {

    List<Laboratory> findByExamDateBetweenOrderByExamDateDesc(
            GregorianCalendar dateFrom,
            GregorianCalendar dateTo);

    List<Laboratory> findByExamDateBetweenAndExam_DescriptionOrderByExamDateDesc(
            GregorianCalendar dateFrom,
            GregorianCalendar dateTo,
            String exam);

    List<Laboratory> findByPatient_CodeOrderByRegistrationDate(Integer patient);

    List<Laboratory> findByExamDateBetweenOrderByExam_Examtype_DescriptionDesc(
            GregorianCalendar dateFrom,
            GregorianCalendar dateTo);

    List<Laboratory> findByExamDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(
            GregorianCalendar dateFrom,
            GregorianCalendar dateTo,
            String exam);

}