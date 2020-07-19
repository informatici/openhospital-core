package org.isf.medicals.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.medicals.model.Medical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalsIoOperationRepository extends JpaRepository<Medical, Integer> {
	@Query(value = "SELECT m FROM Medical m where m.description like :description order BY m.description")
	ArrayList<Medical> findAllWhereDescriptionOrderByDescription(@Param("description") String description);
	@Query(value = "SELECT m FROM Medical m order BY m.description")
    ArrayList<Medical> findAllByOrderByDescription();
    @Query(value = "SELECT m FROM Medical m where m.type.description like :type order BY m.description")
    ArrayList<Medical> findAllWhereTypeOrderByDescription(@Param("type") String type);
      
    
    @Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) and (m.type.code=:type) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
    List<Medical> findAllWhereDescriptionAndTypeAndCriticalOrderByTypeAndDescription(@Param("description") String description, @Param("type") String type);
    @Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) and (m.type.code=:type) order BY m.type.description, m.description")
    List<Medical> findAllWhereDescriptionAndTypeOrderByTypeAndDescription(@Param("description") String description, @Param("type") String type);
    @Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
    List<Medical> findAllWhereDescriptionAndCriticalOrderByTypeAndDescription(@Param("description") String description);
    @Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) order BY m.type.description, m.description")
    List<Medical> findAllWhereDescriptionOrderByTypeAndDescription(@Param("description") String description);
    @Query(value = "SELECT m FROM Medical m where (m.type.code=:type) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
    List<Medical> findAllWhereTypeAndCriticalOrderByTypeAndDescription(@Param("type") String type);
    @Query(value = "SELECT m FROM Medical m where (m.type.code=:type) order BY m.type.description, m.description")
    List<Medical> findAllWhereTypeOrderByTypeAndDescription(@Param("type") String type);
    @Query(value = "SELECT m FROM Medical m where ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
    List<Medical> findAllWhereCriticalOrderByTypeAndDescription();
    @Query(value = "SELECT m FROM Medical m order BY m.type.description, m.description")
    List<Medical> findAllByOrderByTypeAndDescription();
    
    @Query(value = "SELECT m FROM Medical m WHERE m.type.code = :type AND m.description = :description")
    Medical findOneWhereDescriptionAndType(@Param("description") String description, @Param("type") String type);
    @Query(value = "SELECT m FROM Medical m WHERE m.type.code = :type AND m.description = :description AND m.code <> :id")
    Medical findOneWhereDescriptionAndType(@Param("description") String description, @Param("type") String type, @Param("id") Integer id);
    @Query(value = "SELECT m FROM Medical m WHERE m.description LIKE :description")
    List<Medical> findAllWhereDescriptionSoundsLike(@Param("description") String description);
    @Query(value = "SELECT m FROM Medical m WHERE m.description LIKE :description AND m.code <> :id")
    List<Medical> findAllWhereDescriptionSoundsLike(@Param("description") String description, @Param("id") Integer id);
    @Query(value = "SELECT m FROM Medical m WHERE m.prod_code = :prod_code")
    Medical findOneWhereProductCode(@Param("prod_code") String prod_code);
    @Query(value = "SELECT m FROM Medical m WHERE m.prod_code = :prod_code AND m.code <> :id")
    Medical findOneWhereProductCode(@Param("prod_code") String prod_code, @Param("id") Integer id);
	
    
    @Query(value = "SELECT m FROM Medical m WHERE m.type.description LIKE %:type% ORDER BY LENGTH(m.prod_code), m.prod_code, m.description")
    ArrayList<Medical> findAllWhereTypeOrderBySmartCodeAndDescription(@Param("type") String type);
    @Query(value = "SELECT m FROM Medical m ORDER BY LENGTH(m.prod_code), m.prod_code, m.description")
    ArrayList<Medical> findAllOrderBySmartCodeAndDescription();
	
}
