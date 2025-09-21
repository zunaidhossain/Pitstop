package com.pitstop.app.repository;

import com.pitstop.app.model.WorkshopUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkshopUserRepository extends MongoRepository<WorkshopUser, String> {
}
