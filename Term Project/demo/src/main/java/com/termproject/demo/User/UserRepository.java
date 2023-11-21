package com.termproject.demo.User;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);
}
