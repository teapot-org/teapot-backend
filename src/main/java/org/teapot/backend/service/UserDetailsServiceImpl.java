package org.teapot.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.teapot.backend.repository.UserRepository;

import java.util.Optional;


@Service
@Profile("security")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return Optional
                .ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User '%s' not found", username)));
    }
}
