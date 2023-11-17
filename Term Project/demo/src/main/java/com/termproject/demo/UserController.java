package com.termproject.demo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("member", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(User member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        userRepository.save(member);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        // Spring Security에서 자동으로 처리됨
        return "redirect:/";
    }
}
