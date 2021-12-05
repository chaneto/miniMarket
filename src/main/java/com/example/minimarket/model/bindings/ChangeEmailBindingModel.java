package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangeEmailBindingModel {

    @NotBlank(message = "Email cannot be null or empty String!!!")
    @Email(regexp = "^(\\w+@\\w+)(.\\w+){2,}$")
    @Size(min = 3, message = "Email must be more than 3 characters!!!")
    private String email;

    @NotBlank(message = "Email cannot be null or empty String!!!")
    @Email(regexp = "^(\\w+@\\w+)(.\\w+){2,}$")
    @Size(min = 3, message = "Email must be more than 3 characters!!!")
    private String confirmEmail;

    public ChangeEmailBindingModel() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
    }
}
