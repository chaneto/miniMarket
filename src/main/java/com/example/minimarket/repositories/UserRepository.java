package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    UserEntity findByUsernameAndEmail(String username, String email);

    @Query("select u from UserEntity as u order by u.username")
    List<UserEntity> findAllUsersOrderByUsername();

    @Modifying
    @Transactional
    @Query("delete from UserEntity as u where u.username = :username")
    void removeByUsername(String username);

    @Query("select u.username from UserEntity as u")
    List<String> findAllUsername();

    @Modifying
    @Transactional
    @Query("update UserEntity as u set u.role = :role where u.id = :id")
    void setUserRole(@Param("role") UserRoleEntity userRoleEntity, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update UserEntity as u set u.password = :password where u.id = :id")
    void setUserPassword(@Param("password") String password, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update UserEntity as u set u.email = :email where u.id = :id")
    void setUserEmail(@Param("email") String email, @Param("id") Long id);
}
