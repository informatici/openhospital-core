package org.isf.conditioning.service;

import org.isf.conditioning.model.Conditioning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditioningOperationRepository extends JpaRepository<Conditioning, Integer> {
}
