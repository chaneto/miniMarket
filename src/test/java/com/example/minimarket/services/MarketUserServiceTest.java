package com.example.minimarket.services;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.repositories.UserRepository;
import com.example.minimarket.repositories.UserRoleRepository;
import com.example.minimarket.services.impl.MarketUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MarketUserServiceTest {
    @Autowired
    private MarketUserService marketUserService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    UserEntity userEntity;
    UserRoleEntity userRoleEntity;

    @Before
    public void setup(){
        userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserRole(UserRole.ADMIN);
        this.userRoleRepository.save(userRoleEntity);

        userEntity = new UserEntity();
        userEntity.setUsername("pencho");
        userEntity.setFirstName("Pencho");
        userEntity.setLastName("Penchev");
        userEntity.setEmail("pencho_80@abv.ng");
        userEntity.setPassword("12345");
        userEntity.setPhoneNumber("123456789");
        userEntity.setRole(userRoleEntity);
    }

    @Test
    public void testLoadUserByUsername(){
        this.userRepository.save(userEntity);
        this.marketUserService.loadUserByUsername(userEntity.getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameWithNonExistingUsername(){
        this.marketUserService.loadUserByUsername("Petko");
    }
}
