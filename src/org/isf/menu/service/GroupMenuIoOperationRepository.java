package org.isf.menu.service;

import org.isf.menu.model.GroupMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupMenuIoOperationRepository extends JpaRepository<GroupMenu, Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete from GroupMenu where userGroup=:id")
    void deleteWhereUserGroup(@Param("id") String id);
       
}