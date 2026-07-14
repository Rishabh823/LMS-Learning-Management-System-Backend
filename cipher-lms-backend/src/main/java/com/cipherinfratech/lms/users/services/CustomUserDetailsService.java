package com.cipherinfratech.lms.users.services;

import com.cipherinfratech.lms.users.repositories.UsersRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails user = usersRepo.findByEmailId(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}
