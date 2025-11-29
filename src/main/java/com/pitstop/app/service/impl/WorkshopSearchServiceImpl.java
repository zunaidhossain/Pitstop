package com.pitstop.app.service.impl;

import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.constants.WorkshopVehicleType;
import com.pitstop.app.dto.WorkshopUserFilterRequest;
import com.pitstop.app.dto.WorkshopUserFilterResponse;
import com.pitstop.app.dto.WorkshopUserResponse;
import com.pitstop.app.exception.ResourceNotFoundException;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.WorkshopSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkshopSearchServiceImpl implements WorkshopSearchService {

    private final AppUserRepository appUserRepository;
    private final WorkshopUserRepository workshopUserRepository;

    @Override
    public List<WorkshopUserFilterResponse> filterWorkshopUsers(WorkshopUserFilterRequest workshopUserRequest) {
        try{

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            AppUser currentAppUser = appUserRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Address defaultAddress = getAppUserDefaultAddress(currentAppUser);
            log.info("Using user default address: {}", defaultAddress.getFormattedAddress());
            WorkshopVehicleType requestedVehicleType = parseWorkshopVehicleType(workshopUserRequest.getVehicleType());
            WorkshopServiceType requestedServiceType = parseWorkshopServiceType(workshopUserRequest.getServiceType());
            log.info("Filtering workshops for vehicleType={} and serviceType={}",
                    requestedVehicleType, requestedServiceType);
            List<WorkshopUser> shops = workshopUserRepository.findAll();
            log.info("Total workshops found: {}", shops.size());
            List<WorkshopUserFilterResponse> result = new ArrayList<>();
            for(WorkshopUser workshopUser : shops){
                if(workshopUser.getWorkshopAddress() == null){
                    log.warn("Workshop {} skipped: no address set",workshopUser.getUsername());
                    continue;
                }
                if(workshopUser.getVehicleTypeSupported() != requestedVehicleType){
                    continue;
                }
                if(workshopUser.getServicesOffered() == null || !workshopUser.getServicesOffered().contains(requestedServiceType)){
                    continue;
                }
                double distance = haversine(
                        defaultAddress.getLatitude(),
                        defaultAddress.getLongitude(),
                        workshopUser.getWorkshopAddress().getLatitude(),
                        workshopUser.getWorkshopAddress().getLongitude()
                );
                if(distance > workshopUserRequest.getMaxDistanceKm()) {
                    continue;
                }
                WorkshopUserFilterResponse response = new WorkshopUserFilterResponse();
                response.setWorkshopId(workshopUser.getId());
                String displayName = (workshopUser.getName() != null && !workshopUser.getName().isBlank())
                        ? workshopUser.getName()
                        : workshopUser.getUsername();
                response.setWorkshopName(displayName);
                response.setDistanceKm(distance);
                response.setVehicleType(requestedVehicleType);
                response.setServiceType(requestedServiceType);
                response.setFormattedAddress(workshopUser.getWorkshopAddress().getFormattedAddress());
                response.setLatitude(workshopUser.getWorkshopAddress().getLatitude());
                response.setLongitude(workshopUser.getWorkshopAddress().getLongitude());
                result.add(response);
            }
            sortByDistance(result);

            log.info("Workshop search completed, {} results found", result.size());
            return result;
        }
        catch (Exception e) {
            log.error("Error while searching workshops: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search workshops.");
        }
    }
    private WorkshopVehicleType parseWorkshopVehicleType(String workshopVehicleType) {
        try{
            return WorkshopVehicleType.valueOf(workshopVehicleType.toUpperCase());
        }
        catch(Exception e){
            log.error("Invalid vehicle type: {}", workshopVehicleType, e);
            throw new RuntimeException("Invalid vehicle type: " + workshopVehicleType);
        }

    }
    private WorkshopServiceType parseWorkshopServiceType(String workshopServiceType) {
        try{
            return WorkshopServiceType.valueOf(workshopServiceType.toUpperCase());
        }
        catch(Exception e){
            log.error("Invalid service type: {}", workshopServiceType, e);
            throw new RuntimeException("Invalid service type: " + workshopServiceType);
        }
    }
    public Address getAppUserDefaultAddress(AppUser appUser) {
        if(appUser.getUserAddress() == null || appUser.getUserAddress().isEmpty()) {
            log.error("Address required before searching workshops");
            throw new RuntimeException("Address required before searching workshops");
        }
        //if default address found return
        for(Address address : appUser.getUserAddress()) {
            if(address.isDefault()) {
                return address;
            }
        }
        //no default found return first
        return appUser.getUserAddress().get(0);
    }
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void sortByDistance(List<WorkshopUserFilterResponse> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getDistanceKm() > list.get(j).getDistanceKm()) {
                    WorkshopUserFilterResponse temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }
}
