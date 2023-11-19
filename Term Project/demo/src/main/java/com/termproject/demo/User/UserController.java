package com.termproject.demo.User;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getName(),
                    userCreateForm.getPassword1(), userCreateForm.getEmail());

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        // 현재 로그인한 사용자 정보를 모델에 추가
        SiteUser currentUser = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        return "profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        // 현재 로그인한 사용자 정보를 모델에 추가
        SiteUser currentUser = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        return "editProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("currentUser") SiteUser currentUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editProfile";
        }

        userService.updateUser(currentUser);
        return "profile";
    }

    @GetMapping("/delete")
    public String deleteUser(Principal principal) {
        userService.deleteByUsername(principal.getName());
        return "logout";
    }
}
