package com.pitstop.app.repository;

import com.pitstop.app.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    List<Vehicle> findByIdInAndDeletedFalse(Collection<String> ids);

}