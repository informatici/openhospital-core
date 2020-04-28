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

	@Query(value = "select f from FileDicom f WHERE f.patId = :id group by f.dicomInstanceUID")
    List<FileDicom> findAllWhereIdGroupByUid(@Param("id") int id);

	@Query(value = "select f from FileDicom f WHERE f.patId = :id AND f.dicomSeriesNumber = :file AND f.dicomInstanceUID = :uid")
	List<FileDicom> findAllWhereIdAndFileAndUid(@Param("id") int id, @Param("file") String file, @Param("uid") String uid);
    
    @Modifying
	@Query("delete from FileDicom fd WHERE fd.patId = :id AND fd.dicomSeriesNumber = :file")
	void deleteByIdAndNumber(@Param("id") int id, @Param("file") String file);
}