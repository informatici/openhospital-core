package org.isf.medicalstockward.service;

import java.util.List;

import org.isf.medicalstockward.model.MedicalWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MedicalStockWardIoOperationRepository extends JpaRepository<MedicalWard, String>, MedicalStockWardIoOperationRepositoryCustom {      
	   
    @Query(value = "select medWard from MedicalWard medWard where medWard.id.ward.code=:ward " +
			"and medWard.id.medical.code=:medical")
    MedicalWard findOneWhereCodeAndMedical(@Param("ward") String ward, @Param("medical") int medical);
    
    @Query(value = "select sum(medWard.in_quantity-medWard.out_quantity) from MedicalWard medWard " +
			"where medWard.id.medical.code=:medical")
    Double findQuantityInWardWhereMedical(@Param("medical") int medical);
    @Query(value = "select sum(medWard.in_quantity-medWard.out_quantity) from MedicalWard medWard " +
			"where medWard.id.medical.code=:medical and medWard.id.ward.code=:ward")
    Double findQuantityInWardWhereMedicalAndWard(@Param("medical") int medical, @Param("ward") String ward);
	
    @Modifying 
    @Transactional
    @Query(value = "update MedicalWard set in_quantity=in_quantity+:quantity where id.ward.code=:ward and id.medical.code=:medical")
    void updateInQuantity(@Param("quantity") Double quantity, @Param("ward") String ward, @Param("medical") int medical);
    @Modifying 
    @Transactional
    @Query(value = "update MedicalWard set out_quantity=out_quantity+:quantity where id.ward.code=:ward and id.medical.code=:medical")
    void updateOutQuantity(@Param("quantity") Double quantity, @Param("ward") String ward, @Param("medical") int medical);
    @Modifying 
    @Transactional
    @Query(value = "INSERT INTO MEDICALDSRWARD (MDSRWRD_WRD_ID_A, MDSRWRD_MDSR_ID, MDSRWRD_IN_QTI, MDSRWRD_OUT_QTI) " +
    		"VALUES (?, ?, ?, '0')", nativeQuery= true)
    void insertMedicalWard(@Param("ward") String ward, @Param("medical") int medical, @Param("quantity") Double quantity);
        
    @Query(value = "select medWard from MedicalWard medWard where medWard.id.ward.code=:ward")
    List<MedicalWard> findAllWhereWard(@Param("ward") String wordCode);

}
