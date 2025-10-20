package com.pitstop.app.repository;

import com.pitstop.app.model.BaseUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BaseUserRepository<T extends BaseUser> extends MongoRepository<T,String> {
    Optional<T> findUserByUsername(String username);
}
