package com.termproject.demo.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.termproject.demo.DataNotFoundException;

import java.security.Principal;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    

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

        return "login_form";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        SiteUser currentUser = userService.findByUsername(principal.getName());
        model.addAttribute("currentUser", currentUser);
        return "editProfile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("currentUser") SiteUser updatedUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editProfile";
        }

        try {
            userService.updateUser(updatedUser);
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            bindingResult.reject("updateFailed", e.getMessage());
            return "editProfile";
        }

        return "redirect:/user/profile";
    }

    @Transactional
    public void updateUser(SiteUser updatedUser) {
        String currentUsername = getCurrentUsername();

        SiteUser currentUser = findByUsername(currentUsername);

        if (currentUser != null) {
            currentUser.setName(updatedUser.getName());
            currentUser.setEmail(updatedUser.getEmail());

            // 비밀번호가 비어 있지 않은 경우에만 업데이트
            if (!updatedUser.getPassword().isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userRepository.save(currentUser);
        } else {
            throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

    private SiteUser findByUsername(String currentUsername) {
        return userService.findByUsername(currentUsername);
    }

    private String getCurrentUsername() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("/delete")
    public String deleteUser(Principal principal, Model model, HttpServletRequest request) {
        // 현재 로그인한 사용자의 username 가져오기
        String username = principal.getName();

        // 회원 탈퇴 처리
        userService.deleteByUsername(username);

        // 세션 무효화 및 로그아웃 처리
        request.getSession().invalidate();

        // 로그아웃 후 로그인 페이지로 리다이렉트
        return "redirect:/user/login?logout";
    }

    @GetMapping("/delete")
    public String showDeletePage() {
        return "login_form";
    }

    @GetMapping("/profile")
public String userProfile(Model model, Principal principal) {
    SiteUser currentUser = userService.findByUsername(principal.getName());
    model.addAttribute("currentUser", currentUser);
    return "profile";
}
}
