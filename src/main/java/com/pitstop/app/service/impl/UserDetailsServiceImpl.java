package com.pitstop.app.service.impl;

import com.pitstop.app.model.AppUser;
import com.pitstop.app.model.BaseUser;
import com.pitstop.app.model.WorkshopUser;
import com.pitstop.app.repository.AdminUserRepository;
import com.pitstop.app.repository.AppUserRepository;
import com.pitstop.app.repository.WorkshopUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final WorkshopUserRepository workshopUserRepository;
    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting login for username: " + username);

        return appUserRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("Found AppUser: " + user.getUsername());
                    return mapToUserDetails(user);
                })
                .orElseGet(() -> workshopUserRepository.findByUsername(username)
                        .map(user -> {
                            System.out.println("Found WorkshopUser: " + user.getUsername());
                            return mapToUserDetails(user);
                        })
                        .orElseGet(() -> adminUserRepository.findByUsername(username)
                                .map(user -> {
                                    System.out.println("Found AdminUser: " + user.getUsername());
                                    return mapToUserDetails(user);
                                })
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))
                        )
                );
    }

    private UserDetails mapToUserDetails(BaseUser baseUser) {
        return User.builder()
                .username(baseUser.getUsername())
                .password(baseUser.getPassword())
                .roles(baseUser.getRoles().toArray(new String[0]))
                .build();
    }
}
