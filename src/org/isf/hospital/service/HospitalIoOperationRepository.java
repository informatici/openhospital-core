package org.isf.hospital.service;

import org.isf.hospital.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalIoOperationRepository extends JpaRepository<Hospital, String> {

   @Query(value = "select h.currencyCod from Hospital h")
	 List<String> findAllHospitalCurrencyCode();
    
}