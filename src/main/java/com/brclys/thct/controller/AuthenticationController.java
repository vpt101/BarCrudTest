package com.brclys.thct.controller;


import com.brclys.thct.model.UserEntity;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    Logger logger = Logger.getLogger(AuthenticationController.class.getName());

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String authenticateUser(@RequestBody UserEntity user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        //user.getPassword()
                        encoder.encode(user.getPassword())
                )
        );
        logger.warning(encoder.encode(user.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails.getUsername());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String registerUser(@RequestBody UserEntity user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Error: Username unavailable";
        }
        // Create new user's account
        UserEntity newUser = new UserEntity(
                null,
                user.getUsername(),
                encoder.encode(user.getPassword())
        );
        userRepository.save(newUser);
        return "User registration complete.";
    }

    @GetMapping("/getUserId/{userName}")
    @ResponseStatus(HttpStatus.OK)
    public Long showUsers(@PathVariable("userName") String userName) {
        UserEntity userEntity = userRepository.findByUsername(userName);
        return userEntity.getId();
    }
}
