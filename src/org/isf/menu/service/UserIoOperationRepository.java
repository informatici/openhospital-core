package org.isf.menu.service;

import org.isf.menu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserIoOperationRepository extends JpaRepository<User, String> {
    List<User> findAllByOrderByUserNameAsc();

    User findByUserName(String userName);
    
    @Query(value = "select user from User user where user.userGroupName.code=:groupId order by user.userName")
    List<User> findAllWhereUserGroupNameByOrderUserNameAsc(@Param("groupId") String groupId);

    @Modifying
    @Transactional
    @Query(value = "update User user set user.desc=:description where user.userName=:id")
    int updateDescription(@Param("description") String description, @Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "update User set passwd=:password where userName=:id")
    int updatePassword(@Param("password") String password, @Param("id") String id);
}