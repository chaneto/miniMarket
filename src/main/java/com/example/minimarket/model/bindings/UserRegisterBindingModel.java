package com.example.minimarket.model.bindings;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRegisterBindingModel {

    @Expose
    @NotBlank(message = "Username cannot be null or empty String!!!")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters!!!")
    private String username;

    @Expose
    @NotBlank(message = "First name cannot be mull or empty String!!!")
    @Column(name = "first_name")
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters!!!")
    private String firstName;

    @Expose
    @NotBlank(message = "Last name cannot be null or empty String!!!")
    @Column(name = "last_name")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters!!!")
    private String lastName;

    @Expose
    @NotBlank(message = "Email cannot be null or empty String!!!")
    @Email(regexp = "^(\\w+@\\w+)(.\\w+){2,}$")
    @Size(min = 3, message = "Email must be more than 3 characters!!!")
    private String email;

    @Expose
    @NotBlank(message = "Password cannot be null or empty String!!!")
    @Size(min = 5, max = 30, message = "Password must be between 5 and 30 characters!!!")
    private String password;

    @Expose
    @NotBlank(message = "Password cannot be null or empty String!!!")
    @Size(min = 5, max = 30, message = "Password must be between 5 and 30 characters!!!")
    private String confirmPassword;

    @Expose
    @NotBlank(message = "Phone number cannot be null or empty String!!!")
    @Size(min = 5, max = 20, message = "Phone number name must be between 5 and 20 characters!!!")
    private String phoneNumber;

    public UserRegisterBindingModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
