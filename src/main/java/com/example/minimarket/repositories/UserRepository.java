package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.model.services.UserRegisterServiceModel;
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

    @Query("select u.username from UserEntity as u")
    List<String> findAllUsername();

    @Modifying
    @Transactional
    @Query("update UserEntity as u set u.role = :role where u.id = :id")
    void setUserRole(@Param("role") UserRoleEntity userRoleEntity, @Param("id") Long id);
}
