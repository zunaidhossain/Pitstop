package com.pitstop.app.service;

import com.pitstop.app.dto.AddVehicleRequest;
import com.pitstop.app.dto.AddVehicleResponse;
import com.pitstop.app.dto.VehicleDetailsResponse;
import com.pitstop.app.model.Vehicle;
import java.util.List;

public interface VehicleService {
    Vehicle saveVehicle(Vehicle vehicle);
    void deleteVehicle(String vehicleId);
    AddVehicleResponse addTwoWheeler(AddVehicleRequest addVehicleRequest);
    AddVehicleResponse addFourWheeler(AddVehicleRequest addVehicleRequest);
    List<VehicleDetailsResponse> getAllVehicles();
    VehicleDetailsResponse getVehicleById(String vehicleId);
}
