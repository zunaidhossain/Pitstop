package com.pitstop.app.service.impl;

import com.pitstop.app.dto.AddressRequest;
import com.pitstop.app.dto.AppUserLoginRequest;
import com.pitstop.app.dto.WorkshopLoginRequest;
import com.pitstop.app.exception.UserAlreadyExistException;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.WorkshopUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("WorkshopUserServiceImpl Unit Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkshopUserServiceImplTest {

    @Autowired
    private WorkshopUserRepository workshopUserRepository;
    @Autowired
    private WorkshopUserServiceImpl workshopUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private WorkshopUser workshopUser;

    @BeforeAll
    void setUpOnce() {
        workshopUserRepository.deleteByUsername("xxxx_xxxx_workshop_user");

        workshopUser = new WorkshopUser();
        workshopUser.setName("Workshop Test Sample Name");
        workshopUser.setUsername("xxxx_xxxx_workshop_user");
        workshopUser.setEmail("xxxx_xxxx_workshop_user@xyz.com");
        workshopUser.setPassword("123456789");
    }

    @Order(1)
    @Test
    @DisplayName("WorkshopUser registering new Account")
    void saveWorkshopUserDetailsTest() {
        workshopUserService.saveWorkshopUserDetails(workshopUser);

        WorkshopUser found = workshopUserRepository.findByUsername("xxxx_xxxx_workshop_user")
                .orElseThrow(() -> new AssertionError("WorkshopUser was not saved"));

        assertEquals("Workshop Test Sample Name", found.getName());
        assertEquals("xxxx_xxxx_workshop_user", found.getUsername());
        assertEquals("xxxx_xxxx_workshop_user@xyz.com", found.getEmail());
        assertTrue(passwordEncoder.matches("123456789", found.getPassword()));
    }

    @Order(2)
    @Test
    @DisplayName("Should not register workshop user with duplicate username or email")
    void shouldNotRegisterDuplicateWorkshopUser() {
        WorkshopUser duplicate = new WorkshopUser();
        duplicate.setName("Duplicate Workshop");
        duplicate.setUsername("xxxx_xxxx_workshop_user");
        duplicate.setEmail("xxxx_xxxx_workshop_user@xyz.com");
        duplicate.setPassword("123456789");

        assertThrows(UserAlreadyExistException.class,
                () -> workshopUserService.saveWorkshopUserDetails(duplicate));
    }

    @Order(3)
    @DisplayName("WorkshopUser should login successfully")
    @Test
    void shouldLoginSuccessfully() {
        WorkshopLoginRequest loginRequest = new WorkshopLoginRequest();
        loginRequest.setUsername("xxxx_xxxx_workshop_user");
        loginRequest.setPassword("123456789");

        var response = workshopUserService.loginWorkshopUser(loginRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Login successful"));
    }

    @Order(4)
    @DisplayName("WorkshopUser should not login with wrong credentials")
    @Test
    void shouldNotLoginWithWrongCredentials() {
        WorkshopLoginRequest loginRequest = new WorkshopLoginRequest();
        loginRequest.setUsername("xxxx_xxxx_workshop_user");
        loginRequest.setPassword("wrong");

        var response = workshopUserService.loginWorkshopUser(loginRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Incorrect username or password"));
    }

    @Order(5)
    @DisplayName("Adding address to workshop user's address list")
    @Test
    void addAddress() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(workshopUser.getUsername(), workshopUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setLatitude(22.597693666787432);
        addressRequest.setLongitude(88.35945631449115);

        workshopUserService.addAddress(addressRequest);
        SecurityContextHolder.clearContext();

        WorkshopUser updated = workshopUserRepository.findByUsername(workshopUser.getUsername())
                .orElseThrow(() -> new AssertionError("WorkshopUser not found after address update"));

        assertNotNull(updated.getWorkshopAddress());
    }
}
