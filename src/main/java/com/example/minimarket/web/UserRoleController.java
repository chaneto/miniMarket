package com.example.minimarket.web;

import com.example.minimarket.model.bindings.UserSetRoleBindingModel;
import com.example.minimarket.services.UserRoleService;
import com.example.minimarket.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final UserService userService;

    public UserRoleController(UserRoleService userRoleService, UserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    @GetMapping("/set")
    public String setRole(Model model){
        if(!model.containsAttribute("userSetRoleBindingModel")){
            model.addAttribute("userSetRoleBindingModel", new UserSetRoleBindingModel());
            model.addAttribute("successfullyChangedRole", false);
            model.addAttribute("userNotExists", false);

        }
        model.addAttribute("allUsernames", this.userService.findAllUsername());
        return "set-role";
    }

    @PostMapping("/set")
    public String setRole(@Valid UserSetRoleBindingModel userSetRoleBindingModel,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userSetRoleBindingModel", userSetRoleBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userSetRoleBindingModel", bindingResult);
            return "redirect:set";
        }

        if(this.userService.userWithUsernameIsExists(userSetRoleBindingModel.getUsername())){
            redirectAttributes.addFlashAttribute("userSetRoleBindingModel", userSetRoleBindingModel);
            redirectAttributes.addFlashAttribute("successfullyChangedRole", true);
            this.userService.setUserRole(userSetRoleBindingModel.getUsername(), userSetRoleBindingModel.getRoleName());
            return "redirect:/roles/set";
        }
        else {
            redirectAttributes.addFlashAttribute("userSetRoleBindingModel", userSetRoleBindingModel);
            redirectAttributes.addFlashAttribute("userNotExists", true);
            return "redirect:set";
        }

    }
}
