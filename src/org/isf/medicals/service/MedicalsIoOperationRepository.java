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
    List<Medical> findAllWhereDescriptionOrderByDescription(@Param("description") String description);
	@Query(value = "SELECT m FROM Medical m JOIN m.type mt order BY m.description")
    List<Medical> findAllByOrderByDescription();
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where mt.description like :type order BY m.description")
    List<Medical> findAllWhereTypeOrderByDescription(@Param("type") String type);
      
    
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where (m.description like %:description% OR m.prod_code like %:description%) and (mt.code=:type) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY mt.description, m.description")
    List<Medical> findAllWhereDescriptionAndTypeAndCriticalOrderByTypeAndDescritpion(@Param("description") String description, @Param("type") String type);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where (m.description like %:description% OR m.prod_code like %:description%) and (mt.code=:type) order BY mt.description, m.description")
    List<Medical> findAllWhereDescriptionAndTypeOrderByTypeAndDescritpion(@Param("description") String description, @Param("type") String type);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where (m.description like %:description% OR m.prod_code like %:description%) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY mt.description, m.description")
    List<Medical> findAllWhereDescriptionAndCriticalOrderByTypeAndDescritpion(@Param("description") String description);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where (m.description like %:description% OR m.prod_code like %:description%) order BY mt.description, m.description")
    List<Medical> findAllWhereDescriptionOrderByTypeAndDescritpion(@Param("description") String description);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where (mt.code=:type) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY mt.description, m.description")
    List<Medical> findAllWhereTypeAndCriticalOrderByTypeAndDescritpion(@Param("type") String type);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where (mt.code=:type) order BY mt.description, m.description")
    List<Medical> findAllWhereTypeOrderByTypeAndDescritpion(@Param("type") String type);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt where ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY mt.description, m.description")
    List<Medical> findAllWhereCriticalOrderByTypeAndDescritpion();
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt order BY mt.description, m.description")
    List<Medical> findAllByOrderByTypeAndDescritpion();
    
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt WHERE mt.code = :type AND m.description = :description")
    Medical findOneWhereDescriptionAndType(@Param("description") String description, @Param("type") String type);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt WHERE mt.code = :type AND m.description = :description AND m.code <> :id")
    Medical findOneWhereDescriptionAndType(@Param("description") String description, @Param("type") String type, @Param("id") Integer id);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt WHERE m.description LIKE :description")
    List<Medical> findAllWhereDescriptionSoundsLike(@Param("description") String description);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt WHERE m.description LIKE :description AND m.code <> :id")
    List<Medical> findAllWhereDescriptionSoundsLike(@Param("description") String description, @Param("id") Integer id);
    @Query(value = "SELECT m FROM Medical m WHERE m.prod_code = :prod_code")
    Medical findOneWhereProductCode(@Param("prod_code") String prod_code);
    @Query(value = "SELECT m FROM Medical m WHERE m.prod_code = :prod_code AND m.code <> :id")
    Medical findOneWhereProductCode(@Param("prod_code") String prod_code, @Param("id") Integer id);
	
    
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt WHERE mt.description LIKE %:type% ORDER BY LENGTH(m.prod_code), m.prod_code, m.description")
    ArrayList<Medical> findAllWhereTypeOrderBySmartCodeAndDescription(@Param("type") String type);
    @Query(value = "SELECT m FROM Medical m JOIN m.type mt ORDER BY LENGTH(m.prod_code), m.prod_code, m.description")
    ArrayList<Medical> findAllOrderBySmartCodeAndDescription();
	
}
