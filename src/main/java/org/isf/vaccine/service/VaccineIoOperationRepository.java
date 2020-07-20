package org.isf.vaccine.service;

import java.util.List;

import org.isf.vaccine.model.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineIoOperationRepository extends JpaRepository<Vaccine, String> {
    List<Vaccine> findAllByOrderByDescriptionAsc();
    List<Vaccine> findByVaccineType_CodeOrderByDescriptionAsc(String code);
}