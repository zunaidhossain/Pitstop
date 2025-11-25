package com.pitstop.app.service.impl;

import com.pitstop.app.constants.VehicleType;
import com.pitstop.app.dto.AddVehicleRequest;
import com.pitstop.app.dto.AddVehicleResponse;
import com.pitstop.app.dto.VehicleDetailsResponse;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.PaymentRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class VehicleImplTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private VehicleServiceImpl vehicleService;

    @Autowired
    private PaymentRepository paymentRepository;


    private final String username = "test_app_user_002";
    private final String name = "Vehicle Test User";
    private final String email = "test_app_user_002@example.com";
    private final String password = "test@123";

    private String twoWheelerId_1;
    private String twoWheelerId_2;
    private String fourWheelerId_1;
    private String fourWheelerId_2;

    @BeforeAll
    public void setUpOnce() {
        appUserRepository.deleteByUsername(username);
        // AppUser Set-Up
        AppUser appUser = new AppUser();
        appUser.setName(name);
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setPassword(password);
        appUserRepository.save(appUser);
    }

    @Order(1)
    @Test
    @DisplayName("Registering 4 vehicles - 2 (TwoWheeler) and 2 (FourWheeler)")
    void registerVehicleTest(){
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        AddVehicleResponse response1 = vehicleService.addTwoWheeler(new AddVehicleRequest("Honda", "Activa 6G", 110));
        assertEquals("OK", response1.getStatus());

        AddVehicleResponse response2 = vehicleService.addTwoWheeler(new AddVehicleRequest("Triumph", "Speed 400", 400));
        assertEquals("OK", response2.getStatus());

        AddVehicleResponse response3 = vehicleService.addFourWheeler(new AddVehicleRequest("Hyundai", "i10", 1100));
        assertEquals("OK", response3.getStatus());

        AddVehicleResponse response4 = vehicleService.addFourWheeler(new AddVehicleRequest("Mahindra", "XUV 800", 1800));
        assertEquals("OK", response4.getStatus());

        twoWheelerId_1 = response1.getVehicleId();
        twoWheelerId_2 = response2.getVehicleId();
        fourWheelerId_1 = response3.getVehicleId();
        fourWheelerId_2 = response4.getVehicleId();

        SecurityContextHolder.clearContext();
    }

    @Order(2)
    @Test
    @DisplayName("Get All vehicles")
    void getAllVehiclesTest(){
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<VehicleDetailsResponse> allVehicles = vehicleService.getAllVehicles();
        assertEquals(4, allVehicles.size());

        SecurityContextHolder.clearContext();
    }

    @Order(3)
    @Test
    @DisplayName("Get one vehicle by id")
    void getVehicleByIdTest(){
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        VehicleDetailsResponse vehicleById = vehicleService.getVehicleById(fourWheelerId_1);
        assertEquals(VehicleType.FOUR_WHEELER, vehicleById.getVehicleType());
        assertEquals("Hyundai", vehicleById.getBrand());
        assertEquals("i10", vehicleById.getModel());
        assertEquals(1100, vehicleById.getEngineCapacity());

        SecurityContextHolder.clearContext();
    }

    @Order(4)
    @Test
    @DisplayName("Remove vehicle by id")
    void removeVehicleByIdTest(){
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            vehicleService.deleteVehicle(fourWheelerId_1);
        } catch (Exception e) {
            fail("Should not have thrown an exception, but threw: " + e.getMessage());
        }

        SecurityContextHolder.clearContext();
    }

    @Order(5)
    @Test
    @DisplayName("Get All vehicles after deleting one")
    void getAllVehiclesAfterDeletingOneTest(){
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<VehicleDetailsResponse> allVehicles = vehicleService.getAllVehicles();
        assertEquals(3, allVehicles.size());

        SecurityContextHolder.clearContext();
    }

    @AfterAll
    public void tearDownAll() {
        SecurityContextHolder.clearContext();
        paymentRepository.deleteById(twoWheelerId_1);
        paymentRepository.deleteById(twoWheelerId_2);
        paymentRepository.deleteById(fourWheelerId_1);
        paymentRepository.deleteById(fourWheelerId_2);
        appUserRepository.deleteByUsername(username);
    }
}
