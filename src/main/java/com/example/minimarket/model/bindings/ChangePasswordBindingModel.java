package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePasswordBindingModel {

    @NotBlank(message = "Password cannot be null or empty String!!!")
    @Size(min = 5, max = 30, message = "Password must be between 5 and 30 characters!!!")
    private String password;

    @NotBlank(message = "Password cannot be null or empty String!!!")
    @Size(min = 5, max = 30, message = "Password must be between 5 and 30 characters!!!")
    private String confirmPassword;

    public ChangePasswordBindingModel() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}


