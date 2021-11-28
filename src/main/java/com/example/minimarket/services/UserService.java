package com.example.minimarket.services;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.entities.UserRoleEntity;
import com.example.minimarket.model.enums.UserRole;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.model.services.UserServiceModel;
import com.example.minimarket.model.views.OrderViewModel;
import org.springframework.data.repository.query.Param;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface UserService {

    void seedUsersFromJson() throws IOException;

    CartServiceModel getCurrentCart();

    List<OrderViewModel> getAllUserOrderByIsPaid(Boolean isPaid, Long id);

    UserRegisterServiceModel findByUsername(String username);

    UserEntity findByUsernameEntity(String username);

    UserRegisterServiceModel findByUsernameAndEmail(String username, String email);

    boolean userWithUsernameIsExists(String username);

    boolean userWithEmailIsExists(String email);

    void registerUser(UserRegisterServiceModel userRegisterServiceModel);

    void authenticate(UserLoginServiceModel userLoginServiceModel);

    int getCountAllUserOrders();

    BigDecimal getTotalPriceForAllOrders();

    Long getCartId();

    UserServiceModel getCurrentUser();

    List<String> findAllUsername();

    void setUserRole(String username, String roleName);

}
