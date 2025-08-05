package com.brclys.thct.service;

import com.brclys.thct.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            com.brclys.thct.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found", username)));
        return new User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}