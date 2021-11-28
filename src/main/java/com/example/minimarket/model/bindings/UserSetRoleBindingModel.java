package com.example.minimarket.model.bindings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserSetRoleBindingModel {

    @NotBlank(message = "User name cannot be null or empty string!!!")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters!!!")
    private String username;

    @NotBlank(message = "Role name cannot be null or empty string!!!")
    private String roleName;

    public UserSetRoleBindingModel() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
