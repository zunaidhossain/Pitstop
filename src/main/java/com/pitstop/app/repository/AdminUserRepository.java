package com.pitstop.app.repository;

import com.pitstop.app.model.AdminUser;
import com.pitstop.app.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminUserRepository extends MongoRepository<AdminUser,String> {
    Optional<AdminUser> findByUsername(String username);
}
