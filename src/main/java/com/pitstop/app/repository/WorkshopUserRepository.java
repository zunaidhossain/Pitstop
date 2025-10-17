package com.pitstop.app.repository;

import com.pitstop.app.model.WorkshopUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkshopUserRepository extends MongoRepository<WorkshopUser, String> {
    Optional<WorkshopUser> findByUsername(String username);
}
