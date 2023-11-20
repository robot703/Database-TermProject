package com.termproject.demo.User;

import org.apache.catalina.User;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import com.termproject.demo.DataNotFoundException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public SiteUser create(String username, String name, String password, String email) {
        SiteUser user = new SiteUser();
        user.setName(name);  // 추가된 부분
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser findByUsername(String username) {
        Optional<SiteUser> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    public void updateUser(@Valid SiteUser currentUser) {
        // 사용자 정보 업데이트 로직 추가
        // userRepository.save(user);
    }

    @Transactional
    public void deleteByUsername(String username) {
        // 사용자가 존재하는지 확인
        SiteUser user = findByUsername(username);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public String getCurrentUsername() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                // 만약 UserDetails가 아닌 경우에 대한 처리
                username = authentication.getPrincipal().toString();
            }
        }

        return username;
    }
}
