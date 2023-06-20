/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

	@Override
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