package com.pitstop.app.service.impl;

import com.pitstop.app.exception.UserAlreadyExistException;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.WorkshopUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkshopUserServiceImplTest {
    @Mock
    private WorkshopUserRepository workshopUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private WorkshopUserServiceImpl workshopUserService;

    @Test
    @DisplayName("WorkshopUser cannot register with same email or username")
    void saveWorkshopUserDetails() {
        WorkshopUser newUser = new WorkshopUser();
        newUser.setUsername("newWorkShop");
        newUser.setEmail("new@gmail.com");
        newUser.setPassword("test123");

        WorkshopUser existingUser = new WorkshopUser();
        existingUser.setUsername("existingWorkShop");
        existingUser.setEmail("existing@gmail.com");

        when(workshopUserRepository.findByUsernameOrEmail("newWorkShop", "new@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        UserAlreadyExistException exception = assertThrows(
                UserAlreadyExistException.class,
                () -> workshopUserService.saveWorkshopUserDetails(newUser)
        );

        assertEquals("WorkshopUser already exists", exception.getMessage());
        Mockito.verify(workshopUserRepository, never()).save(any());
    }
}