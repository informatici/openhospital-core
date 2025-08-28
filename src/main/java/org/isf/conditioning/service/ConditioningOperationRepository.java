package org.isf.conditioning.service;

import org.isf.conditioning.model.Conditioning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditioningOperationRepository extends JpaRepository<Conditioning, Integer> {
}
