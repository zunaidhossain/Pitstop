package com.pitstop.app.repository;

import com.pitstop.app.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);

    /*
    changed findUserByEmailOrUsername to just findByEmail
    because we're already finding it by username in above method
     */

    Optional<AppUser> findByEmail(String email);

    //added for unit testing
    void deleteByUsername(String username);
}
