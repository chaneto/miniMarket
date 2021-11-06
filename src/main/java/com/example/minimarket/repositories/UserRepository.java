package com.example.minimarket.repositories;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    UserEntity findByUsernameAndEmail(String username, String email);
}
