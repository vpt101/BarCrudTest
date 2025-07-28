package com.brclys.thct.controller;


import com.brclys.thct.dto.UserLoginDto;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.brclys.thct.AppConstants.API_BASE_URL;
import static com.brclys.thct.AppConstants.AUTH_URL_BASE;


@RestController
@RequestMapping(API_BASE_URL + AUTH_URL_BASE)
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;

    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * Authenticates a user with the provided login credentials.
     *
     * @param user the login credentials of the user
     * @return a JWT token if authentication is successful
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String authenticateUser(@RequestBody UserLoginDto user) {
        logger.warn("Login for " + user.getUsername());
        logger.warn(encoder.encode(user.getPassword()));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                        // encoder.encode(user.getPassword())
                )
        );
        logger.warn(">>>>>" + encoder.encode(user.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails.getUsername());
    }

}
