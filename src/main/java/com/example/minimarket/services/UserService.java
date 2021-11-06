package com.example.minimarket.services;

import com.example.minimarket.model.entities.UserEntity;
import com.example.minimarket.model.services.CartServiceModel;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;

import java.math.BigDecimal;

public interface UserService {

    CartServiceModel getCurrentCart();

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

    UserEntity getCurrentUser();
}
