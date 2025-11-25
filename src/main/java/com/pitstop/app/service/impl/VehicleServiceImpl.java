package com.pitstop.app.service.impl;

import com.pitstop.app.constants.VehicleType;
import com.pitstop.app.dto.AddVehicleRequest;
import com.pitstop.app.dto.AddVehicleResponse;
import com.pitstop.app.dto.VehicleDetailsResponse;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.Vehicle;
import com.pitstop.app.repository.VehicleRepository;
import com.pitstop.app.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final AppUserServiceImpl appUserService;

    @Override
    public Vehicle saveVehicle(Vehicle vehicle) {
        try {
            return vehicleRepository.save(vehicle);
        } catch (Exception e) {
            log.error("Vehicle Details Cannot be saved :: {}", vehicle.toString());
            throw new RuntimeException("Error occurred trying to save Vehicle Details.");
        }
    }

    @Override
    public void deleteVehicle(String vehicleId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser currentAppUser = appUserService.getAppUserByUsername(username);

            boolean exists = currentAppUser.getVehicleList().stream()
                    .anyMatch(v -> v.getId().equals(vehicleId));

            if(!exists) {
                log.error("Requested Vehicle ID for deletion does not belongs to logged in user. Vehicle id :: {}", vehicleId);
                throw new RuntimeException("Requested Vehicle ID for deletion does not belongs to logged in user.");
            }

            Optional<Vehicle> v = vehicleRepository.findById(vehicleId);
            if(v.isEmpty()) {
                log.error("Requested Vehicle ID not found. Vehicle id :: {}", vehicleId);
                throw new RuntimeException("Requested Vehicle ID not found.");
            }

            v.get().setDeleted(true);
            saveVehicle(v.get());

        } catch (Exception e) {
            log.error("Vehicle Cannot be removed :: {}", vehicleId);
            throw new RuntimeException("Error occurred trying to remove Vehicle Details.");
        }
    }

    @Override
    public AddVehicleResponse addTwoWheeler(AddVehicleRequest addVehicleRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser currentAppUser = appUserService.getAppUserByUsername(username);

            Vehicle newVehicle = new Vehicle(VehicleType.TWO_WHEELER, addVehicleRequest.getBrand(),
                    addVehicleRequest.getModel(), addVehicleRequest.getEngineCapacity());

            newVehicle = saveVehicle(newVehicle);
            currentAppUser.getVehicleList().add(newVehicle);
            appUserService.updateAppUserDetails(currentAppUser);

            return new AddVehicleResponse(newVehicle.getId(), "OK");
        } catch (Exception e) {
            log.error("Error occurred while trying to add Two Wheeler");
            throw new RuntimeException("Exception occurred while trying to add vehicle. "+e.getMessage());
        }
    }

    @Override
    public AddVehicleResponse addFourWheeler(AddVehicleRequest addVehicleRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser currentAppUser = appUserService.getAppUserByUsername(username);

            Vehicle newVehicle = new Vehicle(VehicleType.FOUR_WHEELER, addVehicleRequest.getBrand(),
                    addVehicleRequest.getModel(), addVehicleRequest.getEngineCapacity());

            newVehicle = saveVehicle(newVehicle);
            currentAppUser.getVehicleList().add(newVehicle);
            appUserService.updateAppUserDetails(currentAppUser);

            return new AddVehicleResponse(newVehicle.getId(), "OK");
        } catch (Exception e) {
            log.error("Error occurred while trying to add Four Wheeler");
            throw new RuntimeException("Exception occurred while trying to add vehicle. "+e.getMessage());
        }
    }

    @Override
    public List<VehicleDetailsResponse> getAllVehicles() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser currentAppUser = appUserService.getAppUserByUsername(username);

            List<String> vehicleIds = currentAppUser.getVehicleList().stream()
                    .map(Vehicle::getId)
                    .filter(Objects::nonNull)
                    .toList();

            if (vehicleIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<Vehicle> vehicles = vehicleRepository.findByIdInAndDeletedFalse(vehicleIds);

            List<VehicleDetailsResponse> result = new ArrayList<>();
            for (Vehicle v : vehicles) {
                if (!v.isDeleted()) {
                    result.add(new VehicleDetailsResponse(v.getId(), v.getVehicleType(),
                            v.getBrand(), v.getModel(), v.getEngineCapacity()));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error occurred while trying to fetch all vehicles");
            throw new RuntimeException("Exception occurred while trying to fetch all vehicles. "+e.getMessage());
        }
    }

    @Override
    public VehicleDetailsResponse getVehicleById(String vehicleId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            AppUser currentAppUser = appUserService.getAppUserByUsername(username);

            boolean exists = currentAppUser.getVehicleList().stream()
                    .anyMatch(v -> v.getId().equals(vehicleId));

            if(!exists) {
                log.error("Requested Vehicle ID does not belongs to logged in user. Vehicle id :: {}", vehicleId);
                throw new RuntimeException("Requested Vehicle ID does not belongs to logged in user.");
            }

            Optional<Vehicle> vehicleDetails = vehicleRepository.findById(vehicleId);
            if (vehicleDetails.isPresent() && !vehicleDetails.get().isDeleted()) {
                return new VehicleDetailsResponse(vehicleDetails.get().getId(),
                        vehicleDetails.get().getVehicleType(),
                        vehicleDetails.get().getBrand(), vehicleDetails.get().getModel(),
                        vehicleDetails.get().getEngineCapacity());
            } else {
                log.error("Vehicle not found with vehicle id :: {}", vehicleId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while trying to fetch vehicle id :: {} :: {}", vehicleId, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
