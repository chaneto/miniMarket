package com.example.minimarket.web;

import com.example.minimarket.model.bindings.*;
import com.example.minimarket.model.services.UserLoginServiceModel;
import com.example.minimarket.model.services.UserRegisterServiceModel;
import com.example.minimarket.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper mapper;

    public UserController(UserService userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @ModelAttribute("userRegisterBindingModel")
    public UserRegisterBindingModel createBindingModel(){
        return new UserRegisterBindingModel();
    }

    @GetMapping("/login")
    public String login(Model model){
        if(!model.containsAttribute("userLoginBindingModel")){
            model.addAttribute("userLoginBindingModel", new UserLoginBindingModel());
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginConfirm(@Valid UserLoginBindingModel userLoginBindingModel,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userLoginBindingModel", userLoginBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userLoginBindingModel", bindingResult);
            return "redirect:login";
        }

        UserLoginServiceModel userLoginServiceModel = this.mapper.map(userLoginBindingModel, UserLoginServiceModel.class);
        this.userService.authenticate(userLoginServiceModel);
        return "redirect:/";
    }

    @PostMapping ("/login-error")
    public String falledLogin(@ModelAttribute (UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
                                    RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("bad_credentials", true);
        redirectAttributes.addFlashAttribute("username", username);
        return "redirect:login";
    }

    @GetMapping("/register")
    public String register(){
        return "/register";
    }

    @PostMapping("/register")
    public String registerConfirm(@Valid UserRegisterBindingModel userRegisterBindingModel,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel", bindingResult);
            return "redirect:register";
        }
        UserRegisterServiceModel userRegisterServiceModel = this.mapper.map(userRegisterBindingModel, UserRegisterServiceModel.class);
        if(this.userService.userWithUsernameIsExists(userRegisterServiceModel.getUsername())){
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("userWithUsernameIsExists", true);
            return "redirect:register";
        }

        if(this.userService.userWithEmailIsExists(userRegisterServiceModel.getEmail())){
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("userWithEmailIsExists", true);
            return "redirect:register";
        }
        if(!this.userService.passwordMatches(userRegisterBindingModel.getPassword(), userRegisterBindingModel.getConfirmPassword())){
            redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
            redirectAttributes.addFlashAttribute("passwordDontMatch", true);
            return "redirect:register";
        }

        this.userService.registerUser(userRegisterServiceModel);
        redirectAttributes.addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel);
        redirectAttributes.addFlashAttribute("successfulRegistration", true);
        return "redirect:login";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model){
        model.addAttribute("currentUser", this.userService.getCurrentUser());
        return "profile";
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model){
        if(!model.containsAttribute("changePasswordBindingModel")){
            model.addAttribute("changePasswordBindingModel", new ChangePasswordBindingModel());
            model.addAttribute("passwordNotMatch", false);
            model.addAttribute("successfulChangePassword", false);
        }
        return "change-password";
    }

    @PostMapping("/changePassword")
    public String changePasswordConfirm(@Valid ChangePasswordBindingModel changePasswordBindingModel,
                                        BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("changePasswordBindingModel", changePasswordBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordBindingModel", bindingResult);
            return "redirect:changePassword";
        }
        if(!this.userService.passwordMatches(changePasswordBindingModel.getPassword(), changePasswordBindingModel.getConfirmPassword())){
            redirectAttributes.addFlashAttribute("changePasswordBindingModel", changePasswordBindingModel);
            redirectAttributes.addFlashAttribute("passwordNotMatch", true);
            return "redirect:changePassword";
        }

        this.userService.changePassword(changePasswordBindingModel.getPassword());
        redirectAttributes.addFlashAttribute("changePasswordBindingModel", changePasswordBindingModel);
        redirectAttributes.addFlashAttribute("successfulChangePassword", true);
        return "redirect:/users/profile";
    }

    @GetMapping("/changeEmail")
    public String changeEmail(Model model){
        if(!model.containsAttribute("changeEmailBindingModel")){
            model.addAttribute("changeEmailBindingModel", new ChangeEmailBindingModel());
            model.addAttribute("userWithEmailIsExists", false);
            model.addAttribute("emailNotMatch", false);
            model.addAttribute("successfulChangeEmail", false);
        }
        return "change-email";
    }

    @PostMapping("/changeEmail")
    public String changeEmailConfirm(@Valid ChangeEmailBindingModel changeEmailBindingModel,
                                     BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("changeEmailBindingModel", changeEmailBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changeEmailBindingModel", bindingResult);
            return "redirect:changeEmail";
        }

        if(this.userService.userWithEmailIsExists(changeEmailBindingModel.getEmail())){
            redirectAttributes.addFlashAttribute("changeEmailBindingModel", changeEmailBindingModel);
            redirectAttributes.addFlashAttribute("userWithEmailIsExists", true);
            return "redirect:changeEmail";
        }

        if(!changeEmailBindingModel.getEmail().equals(changeEmailBindingModel.getConfirmEmail())){
            redirectAttributes.addFlashAttribute("changeEmailBindingModel", changeEmailBindingModel);
            redirectAttributes.addFlashAttribute("emailNotMatch", true);
            return "redirect:changeEmail";
        }

        this.userService.changeEmail(changeEmailBindingModel.getEmail());
        redirectAttributes.addFlashAttribute("changeEmailBindingModel", changeEmailBindingModel);
        redirectAttributes.addFlashAttribute("successfulChangeEmail", true);
        return "redirect:/users/profile";
    }

    @GetMapping("/all")
    public String getAllUsers(Model model){
        if(!model.containsAttribute("userDeleteBindingModel")){
            model.addAttribute("userDeleteBindingModel", new  UserDeleteBindingModel());
            model.addAttribute("successfullyDeleteUser", false);
            model.addAttribute("userNotExists", false);

        }
        model.addAttribute("allUsernames", this.userService.findAllUsername());
        model.addAttribute("allUsers", this.userService.getAllUsers());
        return "all-users";
    }

    @GetMapping("/delete/{username}")
    public String deleteByUsername(@PathVariable String username,RedirectAttributes redirectAttributes,
                                   UserDeleteBindingModel userDeleteBindingModel){
        this.userService.deleteUserByUsername(username);
        redirectAttributes.addFlashAttribute("userDeleteBindingModel", userDeleteBindingModel);
        redirectAttributes.addFlashAttribute("successfullyDeleteUser", true);
        return "redirect:/users/all";
    }

    @PostMapping("/delete")
    public String deleteByUsernameConfirm(@Valid UserDeleteBindingModel userDeleteBindingModel,
                                          BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute("userDeleteBindingModel", userDeleteBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDeleteBindingModel", bindingResult);
            return "redirect:/users/all";
        }

       else if(this.userService.userWithUsernameIsExists(userDeleteBindingModel.getUsername())){
            this.userService.deleteUserByUsername(userDeleteBindingModel.getUsername());
            redirectAttributes.addFlashAttribute("userDeleteBindingModel", userDeleteBindingModel);
            redirectAttributes.addFlashAttribute("successfullyDeleteUser", true);
            return "redirect:/users/all";
        }

       else {
           redirectAttributes.addFlashAttribute("userDeleteBindingModel", userDeleteBindingModel);
           redirectAttributes.addFlashAttribute("userNotExists", true);
           return "redirect:/users/all";
        }
    }

}
