package com.pitstop.app.service.impl;

import com.pitstop.app.constants.VehicleType;
import com.pitstop.app.constants.WorkshopServiceType;
import com.pitstop.app.constants.WorkshopStatus;
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
        workshopUserRepository.deleteByUsername("workshop1");
        workshopUserRepository.deleteByUsername("workshop2");
        workshopUserRepository.deleteByUsername("workshop3");
        appUserRepository.deleteByUsername("filter_test_user");

        Address defaultAddress = new Address(
                22.6000,
                88.4000,
                "Kolkata",
                true
        );

        appUser = new AppUser();
        appUser.setUsername("filter_test_user");
        appUser.setPassword("pass");
        appUser.setEmail("filter@test.com");
        appUser.setUserAddress(List.of(defaultAddress));

        appUserRepository.save(appUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), null)
        );

        WorkshopUser w1 = new WorkshopUser();
        w1.setUsername("workshop1");
        w1.setPassword("pass");
        w1.setEmail("w1@test.com");
        w1.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        w1.setVehicleTypeSupported(VehicleType.TWO_WHEELER);
        w1.setServicesOffered(new ArrayList<>(List.of(WorkshopServiceType.OIL_CHANGE)));
        w1.setWorkshopAddress(new Address(22.6020, 88.4020, "Near Kolkata", true));
        workshopUserRepository.save(w1);

        WorkshopUser w2 = new WorkshopUser();
        w2.setUsername("workshop2");
        w2.setPassword("pass");
        w2.setEmail("w2@test.com");
        w2.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        w2.setVehicleTypeSupported(VehicleType.TWO_WHEELER);
        w2.setServicesOffered(new ArrayList<>(List.of(WorkshopServiceType.TYRE_REPLACEMENT)));
        w2.setWorkshopAddress(new Address(22.6500, 88.4500, "Far Away", true));
        workshopUserRepository.save(w2);

        WorkshopUser w3 = new WorkshopUser();
        w3.setUsername("workshop3");
        w3.setPassword("pass");
        w3.setEmail("w3@test.com");
        w3.setCurrentWorkshopStatus(WorkshopStatus.OPEN);
        w3.setVehicleTypeSupported(VehicleType.TWO_WHEELER);
        w3.setServicesOffered(new ArrayList<>(List.of(WorkshopServiceType.OIL_CHANGE)));
        w3.setWorkshopAddress(new Address(22.8000, 88.3600, "Very Far", true));
        workshopUserRepository.save(w3);
    }
    @Order(1)
    @Test
    @DisplayName("Filter: Only matching workshops returned")
    void shouldFilterWorkshopsCorrectly() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), null));

        WorkshopUserFilterRequest req = new WorkshopUserFilterRequest();
        req.setVehicleType("TWO_WHEELER");
        req.setServiceType("OIL_CHANGE");
        req.setMaxDistanceKm(5.0);

        List<WorkshopUserFilterResponse> response =
                workshopSearchService.filterWorkshopUsers(req);

        assertEquals(1, response.size());
        assertEquals("workshop1", response.get(0).getWorkshopName());
    }
    @Order(2)
    @Test
    @DisplayName("Filter: Wrong service → empty list")
    void shouldReturnEmptyForWrongServiceType() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), null));

        WorkshopUserFilterRequest req = new WorkshopUserFilterRequest();
        req.setVehicleType("TWO_WHEELER");
        req.setServiceType("AC_REPAIR");
        req.setMaxDistanceKm(5);

        List<WorkshopUserFilterResponse> results =
                workshopSearchService.filterWorkshopUsers(req);

        assertTrue(results.isEmpty());
    }
    @Order(3)
    @Test
    @DisplayName("Filter: Wrong vehicle type → empty list")
    void shouldReturnEmptyForWrongVehicleType() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), null));

        WorkshopUserFilterRequest req = new WorkshopUserFilterRequest();
        req.setVehicleType("FOUR_WHEELER");
        req.setServiceType("OIL_CHANGE");
        req.setMaxDistanceKm(5);

        List<WorkshopUserFilterResponse> results =
                workshopSearchService.filterWorkshopUsers(req);

        assertTrue(results.isEmpty());
    }
    @Order(4)
    @Test
    @DisplayName("Filter: Should ignore far workshops")
    void shouldIgnoreFarWorkshops() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(appUser.getUsername(), null));

        WorkshopUserFilterRequest req = new WorkshopUserFilterRequest();
        req.setVehicleType("TWO_WHEELER");
        req.setServiceType("OIL_CHANGE");
        req.setMaxDistanceKm(1);

        List<WorkshopUserFilterResponse> results =
                workshopSearchService.filterWorkshopUsers(req);

        assertEquals(1, results.size());
        assertEquals("workshop1", results.get(0).getWorkshopName());
    }

    @AfterAll
    void tearDownAll() {
       workshopUserRepository.deleteByUsername("workshop1");
       workshopUserRepository.deleteByUsername("workshop2");
       workshopUserRepository.deleteByUsername("workshop3");
       appUserRepository.deleteByUsername("filter_test_user");
    }
}