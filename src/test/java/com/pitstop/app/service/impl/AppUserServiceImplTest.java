package com.pitstop.app.service.impl;

import com.pitstop.app.dto.AppUserLoginRequest;
import com.pitstop.app.dto.AppUserLoginResponse;
import com.pitstop.app.model.AppUser;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserServiceImpl Unit Test")
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AuthenticationManager manager;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserServiceImpl appUserService;  //this is what we want to test,The service behaves like real, but its dependencies are fake and under your control.

    @BeforeEach
    void setUp(){

    }

    @Test
    @DisplayName("Should save existing user without encoding password")
    void shouldSaveExistingUserWithoutPasswordEncoding(){
        AppUser user = new AppUser();
        user.setId("123");
        user.setUsername("Hecker Hubba");
        user.setPassword("plainpassword");

        when(appUserRepository.existsById("123")).thenReturn(true);
        appUserService.saveAppUserDetails(user);

        verify(appUserRepository, times(1)).save(user);
        verify(passwordEncoder,never()).encode(any());
        assertEquals("plainpassword", user.getPassword());
    }
    @Test
    @DisplayName("Should save new user by encoding password")
    void shouldSaveNewUserWithEncodedPassword(){
        AppUser appUser = new AppUser();
        appUser.setId("123");
        appUser.setUsername("zunaid hecker");
        appUser.setPassword("plainpassword");

        when(appUserRepository.existsById(any())).thenReturn(false);
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword");

        appUserService.saveAppUserDetails(appUser);

        verify(appUserRepository, times(1)).save(appUser);
        verify(passwordEncoder,times(1)).encode("plainpassword");
        assertEquals("encodedpassword",appUser.getPassword());
    }
    @Test
    @DisplayName("Address Should be added to the user's Address List")
    void checkAddressAddedInList(){
        AppUser appUser = new AppUser();
        appUser.setId("123");
        appUser.setUserAddress(new ArrayList<>());
    }
    @Test
    @DisplayName("Login test should return JWT Token and 200")
    void testLoginSuccess(){
        // Arrange
        AppUserLoginRequest loginRequest = new AppUserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testuser");

        // Mock authenticate() to return a token (non-void method)
        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken("testuser")).thenReturn("mockedJwtToken");

        // Act
        ResponseEntity<?> response = appUserService.loginAppUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AppUserLoginResponse);

        AppUserLoginResponse body = (AppUserLoginResponse) response.getBody();
        assertEquals("testuser", body.getUsername());
        assertEquals("mockedJwtToken", body.getToken());
        assertEquals("Login successful", body.getMessage());

        // Verify interactions
        verify(manager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername("testuser");
        verify(jwtUtil, times(1)).generateToken("testuser");
    }
}