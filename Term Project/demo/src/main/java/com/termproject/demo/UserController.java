package com.termproject.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserRepository memberRepository;

    public UserController(UserRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("member", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(User member) {
        // 실제로는 비밀번호를 해싱하고 보안을 강화해야 합니다.
        memberRepository.save(member);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // 로그인 기능은 Spring Security 등을 이용하여 구현할 수 있습니다.
}
