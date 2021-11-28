package com.example.minimarket.services;

import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.repositories.UserRoleRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRoleServiceImplTest {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRoleService userRoleService;

    UserRoleEntity userRoleEntity1;
    UserRoleEntity userRoleEntity3;

    @Before
    public void setup(){
        this.userRoleRepository.deleteAll();
        userRoleEntity1 = new UserRoleEntity();
        userRoleEntity3 = new UserRoleEntity();
        userRoleEntity1.setUserRole(UserRole.ADMIN);
        userRoleEntity3.setUserRole(UserRole.USER);
        this.userRoleRepository.save(userRoleEntity1);
        this.userRoleRepository.save(userRoleEntity3);
    }

    @Test
    public void testFindAll(){
        Assert.assertEquals(2, this.userRoleRepository.count());
        List<UserRoleEntity> roles = this.userRoleService.findAll();
        Assert.assertEquals(2, roles.size());
    }

    @Test
    public void testFindAllByUserRole(){
        List<UserRoleEntity> roles = this.userRoleService.findAllByUserRole(UserRole.USER);
        Assert.assertEquals(1, roles.size());
    }

    @Test
    public void testSaveUserRoleEntity(){
        this.userRoleRepository.deleteAll();
        this.userRoleService.saveUserRoleEntity(userRoleEntity1);
        this.userRoleService.saveUserRoleEntity(userRoleEntity3);
        Assert.assertEquals(2, this.userRoleRepository.count());
    }
}
