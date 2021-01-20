/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dicom.service;

import java.util.List;

import org.isf.dicom.model.FileDicom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DicomIoOperationRepository extends JpaRepository<FileDicom, Long> {
    List<FileDicom> findAllByOrderByFileNameAsc();

	@Query(value = "select f from FileDicom f WHERE f.patId = :id AND f.dicomSeriesNumber = :file order by f.fileName")
	List<FileDicom> findAllWhereIdAndNumberByOrderNameAsc(@Param("id") int id, @Param("file") String file);

	@Query(value = "select f from FileDicom f WHERE f.patId = :id group by f.dicomSeriesInstanceUID order by f.dicomSeriesDate desc")
    List<FileDicom> findAllWhereIdGroupBySeriesInstanceUIDOrderSerDateDesc(@Param("id") int id);

	@Query(value = "select f from FileDicom f WHERE f.patId = :id AND f.dicomSeriesNumber = :file AND f.dicomInstanceUID = :uid")
	List<FileDicom> findAllWhereIdAndFileAndUid(@Param("id") int id, @Param("file") String file, @Param("uid") String uid);
    
    @Modifying
	@Transactional
	@Query("delete from FileDicom fd WHERE fd.patId = :id AND fd.dicomSeriesNumber = :file")
	void deleteByIdAndNumber(@Param("id") int id, @Param("file") String file);

	@Query(value = "SELECT * FROM DICOM WHERE DM_FILE_SER_NUMBER = :dicomSeriesNumber", nativeQuery= true)
	List<FileDicom> findAllWhereDicomSeriesNumber(@Param("dicomSeriesNumber") String dicomSeriesNumber);

	@Query(value = "SELECT COUNT(*) FROM DICOM WHERE DM_FILE_SER_INST_UID = :dicomSeriesNumberId", nativeQuery=true)
	int countFramesinSeries(@Param("dicomSeriesNumberId") String dicomSeriesInstanceUID);
}