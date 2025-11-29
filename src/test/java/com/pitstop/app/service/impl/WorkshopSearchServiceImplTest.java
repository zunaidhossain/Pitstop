package com.pitstop.app.service.impl;

import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.constants.WorkshopVehicleType;
import com.pitstop.app.dto.WorkshopUserFilterRequest;
import com.pitstop.app.dto.WorkshopUserFilterResponse;
import com.pitstop.app.model.Address;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import com.pitstop.app.service.WorkshopSearchService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@DisplayName("WorkshopSearch Functionality Unit Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkshopSearchServiceImplTest {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private WorkshopUserRepository workshopUserRepository;
    @Autowired
    private WorkshopSearchService workshopSearchService;

    private AppUser appUser;

    @BeforeAll
    void setUp() {
        appUser = new AppUser();
        appUser.setUsername("filter_test_user");
        appUser.setPassword("pass");
        appUser.setEmail("filter@test.com");

        Address defaultAddress = new Address(
                22.6000,          // latitude
                88.4000,          // longitude
                "Kolkata",         // formatted
                true               // default
        );
        appUser.setUserAddress(List.of(defaultAddress));
        appUserRepository.save(appUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), null)
        );

        // Create Workshop 1 matching criteria
        WorkshopUser w1 = new WorkshopUser();
        w1.setUsername("workshop1");
        w1.setPassword("pass");
        w1.setEmail("w1@test.com");
        w1.setVehicleTypeSupported(WorkshopVehicleType.TWO_WHEELER);
        w1.setServicesOffered(new ArrayList<>(List.of(WorkshopServiceType.OIL_CHANGE)));
        w1.setWorkshopAddress(new Address(22.6020, 88.4020, "Near Kolkata", true)); // 0.3 km away
        workshopUserRepository.save(w1);

        // Create Workshop 2 - correct vehicle but wrong service
        WorkshopUser w2 = new WorkshopUser();
        w2.setUsername("workshop2");
        w2.setPassword("pass");
        w2.setEmail("w2@test.com");
        w2.setVehicleTypeSupported(WorkshopVehicleType.TWO_WHEELER);
        w2.setServicesOffered(new ArrayList<>(List.of(WorkshopServiceType.TYRE_REPLACEMENT)));
        w2.setWorkshopAddress(new Address(22.6500, 88.4500, "Far Away", true)); // 7 km away
        workshopUserRepository.save(w2);

        // Create Workshop 3 - right service, right vehicle, but too far
        WorkshopUser w3 = new WorkshopUser();
        w3.setUsername("workshop3");
        w3.setPassword("pass");
        w3.setEmail("w3@test.com");
        w3.setVehicleTypeSupported(WorkshopVehicleType.TWO_WHEELER);
        w3.setServicesOffered(new ArrayList<>(List.of(WorkshopServiceType.OIL_CHANGE)));
        w3.setWorkshopAddress(new Address(22.8000, 88.3600, "Very Far", true)); // >10 km away
        workshopUserRepository.save(w3);
    }
    @Order(1)
    @DisplayName("Filter : Only workshops matching vehicle type, service type, and within distance should return")
    @Test
    void shouldFilterWorkshopsCorrectly() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        WorkshopUserFilterRequest workshopUserFilterRequest = new WorkshopUserFilterRequest();
        workshopUserFilterRequest.setVehicleType("TWO_WHEELER");
        workshopUserFilterRequest.setServiceType("OIL_CHANGE");
        workshopUserFilterRequest.setMaxDistanceKm(5.0);

        List<WorkshopUserFilterResponse> response = workshopSearchService.filterWorkshopUsers(workshopUserFilterRequest);
        assertEquals(1,response.size());
        assertEquals("workshop1",response.get(0).getWorkshopName());
    }
    @Order(2)
    @Test
    @DisplayName("Filter: Wrong service type returns empty list")
    void shouldReturnEmptyForWrongServiceType() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        WorkshopUserFilterRequest request = new WorkshopUserFilterRequest();
        request.setVehicleType("TWO_WHEELER");
        request.setServiceType("AC_REPAIR"); // nobody supports this
        request.setMaxDistanceKm(5);

        List<WorkshopUserFilterResponse> results =
                workshopSearchService.filterWorkshopUsers(request);

        assertTrue(results.isEmpty());
    }
    @Order(3)
    @Test
    @DisplayName("Filter: Wrong vehicle type returns empty list")
    void shouldReturnEmptyForWrongVehicleType() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        WorkshopUserFilterRequest request = new WorkshopUserFilterRequest();
        request.setVehicleType("FOUR_WHEELER");
        request.setServiceType("OIL_CHANGE");
        request.setMaxDistanceKm(5);

        List<WorkshopUserFilterResponse> results =
                workshopSearchService.filterWorkshopUsers(request);

        assertTrue(results.isEmpty());
    }
    @Order(4)
    @Test
    @DisplayName("Filter: Should ignore workshops outside the max distance")
    void shouldIgnoreFarWorkshops() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        WorkshopUserFilterRequest request = new WorkshopUserFilterRequest();
        request.setVehicleType("TWO_WHEELER");
        request.setServiceType("OIL_CHANGE");
        request.setMaxDistanceKm(1); // only very close allowed

        List<WorkshopUserFilterResponse> results =
                workshopSearchService.filterWorkshopUsers(request);

        assertEquals(1, results.size()); // workshop1 is within 1 km
        assertEquals("workshop1", results.get(0).getWorkshopName());
    }
    @AfterAll
    public void tearDownAll() {
        SecurityContextHolder.clearContext();
        workshopUserRepository.deleteByUsername("workshop1");
        workshopUserRepository.deleteByUsername("workshop2");
        workshopUserRepository.deleteByUsername("workshop3");
        appUserRepository.deleteByUsername("filter_test_user");
    }
}