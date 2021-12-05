package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserDeleteBindingModel {

    @Expose
    @NotBlank(message = "Username cannot be null or empty String!!!")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters!!!")
    private String username;

    public UserDeleteBindingModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
