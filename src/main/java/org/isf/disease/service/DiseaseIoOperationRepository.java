package org.isf.disease.service;

import java.util.List;

import org.isf.disease.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseIoOperationRepository extends JpaRepository<Disease, String> {

    Disease findOneByCode(@Param("code") String code);

	@Query(value = "select d FROM Disease d WHERE d.description = :description AND d.diseaseType.code = :code")
	Disease findOneByDescriptionAndTypeCode(@Param("description") String description, @Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code order BY d.description")
    List<Disease> findAllByDiseaseTypeCode(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.opdInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndOpd(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.ipdInInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndIpdIn(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndIpdOut(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.ipdInInclude=true and d.opdInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndOpdAndIpdIn(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.opdInclude=true and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndOpdAndIpdOut(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.ipdInInclude=true and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndIpdInAndIpdOut(@Param("code") String code);

	@Query(value = "select d FROM Disease d where d.diseaseType.code like :code and d.opdInclude=true and d.ipdInInclude=true and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByDiseaseTypeCodeAndOpdAndIpdInAndIpdOut(@Param("code") String code);

	@Query(value = "select d FROM Disease d order by d.description")
    List<Disease> findAll();

	@Query(value = "select d FROM Disease d where d.opdInclude=true order BY d.description")
    List<Disease> findAllByOpd();

	@Query(value = "select d FROM Disease d where d.ipdInInclude=true order BY d.description")
    List<Disease> findAllByIpdIn();

	@Query(value = "select d FROM Disease d where d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByIpdOut();

	@Query(value = "select d FROM Disease d where d.opdInclude=true and d.ipdInInclude=true order BY d.description")
    List<Disease> findAllByOpdAndIpdIn();

	@Query(value = "select d FROM Disease d where d.opdInclude=true and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByOpdAndIpdOut();

	@Query(value = "select d FROM Disease d where d.ipdInInclude=true and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByIpdInAndIpdOut();

	@Query(value = "select d FROM Disease d where d.opdInclude=true and d.ipdInInclude=true and d.ipdOutInclude=true order BY d.description")
    List<Disease> findAllByOpdAndIpdInAndIpdOut();
}