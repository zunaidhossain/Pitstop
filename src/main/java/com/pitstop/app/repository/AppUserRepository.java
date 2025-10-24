package com.pitstop.app.repository;

import com.pitstop.app.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByEmail(String email);
}
