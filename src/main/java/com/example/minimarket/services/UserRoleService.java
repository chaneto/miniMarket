package com.example.minimarket.services;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;

import java.util.List;

public interface UserRoleService {

    void seedUsersRoleEntity();

    List<UserRoleEntity> findAll();

    List<UserRoleEntity> findAllByUserRole(UserRole userRole);

    UserRoleEntity findByUserRole(UserRole userRole);

    void saveUserRoleEntity(UserRoleEntity userRoleEntity);

}
