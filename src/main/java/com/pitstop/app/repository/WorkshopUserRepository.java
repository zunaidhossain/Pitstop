package com.pitstop.app.repository;

import com.pitstop.app.constants.VehicleType;
import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.model.WorkshopUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WorkshopUserRepository extends MongoRepository<WorkshopUser, String> {
    Optional<WorkshopUser> findByUsername(String username);

    /*
    changed findUserByEmailOrUsername to just findByEmail
    because we're already finding it by username in above method
     */

    Optional<WorkshopUser> findByEmail(String email);

    //added for unit testing
    void deleteByUsername(String username);

    //Find all workshops that offer a particular service
    List<WorkshopUser> findByServicesOffered(WorkshopServiceType workshopServiceType);
    //Find all workshops that support a particular vehicle type
    List<WorkshopUser> findByVehicleTypeSupported(VehicleType workshopVehicleType);
}
