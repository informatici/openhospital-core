package org.isf.opd.service;

import java.util.GregorianCalendar;
import java.util.List;

import org.isf.opd.model.Opd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpdIoOperationRepository extends JpaRepository<Opd, Integer>, OpdIoOperationRepositoryCustom {
	
    @Query("select o from Opd o order by o.prog_year")
	List<Opd> findAllOrderByProgYearDesc();
	
	@Query("select o from Opd o where o.patient.code = :code order by o.prog_year")
	List<Opd> findAllByPatient_CodeOrderByProgYearDesc(@Param("code") Integer code);
	
	@Query("select max(o.prog_year) from Opd o")
    Integer findMaxProgYear();

	@Query(value = "select max(o.prog_year) from Opd o where o.date >= :dateFrom and o.date < :dateTo")
    Integer findMaxProgYearWhereDateBetween(@Param("dateFrom") GregorianCalendar dateFrom, @Param("dateTo") GregorianCalendar dateTo);

    List<Opd> findTop1ByPatient_CodeOrderByDateDesc(Integer code);
	
	@Query("select o from Opd o where o.prog_year = :prog_year")
    List<Opd> findByProgYear(@Param("prog_year") Integer prog_year);
	
	@Query(value = "select op from Opd op where op.prog_year = :prog_year and op.visitDate >= :dateVisitFrom and op.visitDate < :dateVisitTo")
    List<Opd> findByProgYearAndVisitDateBetween(@Param("prog_year") Integer prog_year, @Param("dateVisitFrom") GregorianCalendar dateVisitFrom, @Param("dateVisitTo") GregorianCalendar dateVisitTo);
}
