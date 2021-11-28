package com.example.minimarket.model.bindings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserLoginBindingModel {

    @NotBlank(message = "Username cannot be empty String!!!")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters!!!")
    private String username;

    @NotBlank(message = "Password cannot be empty String!!!")
    @Size(min = 5, max = 30, message = "Password must be between 5 and 30 characters!!!")
    private String password;

    public UserLoginBindingModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
