package com.brclys.thct.controller;

import com.brclys.thct.dto.UserLoginDto;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TAX_ID = "123-45-6789";
    private static final String TEST_JWT = "test.jwt.token";
    private static final Long TEST_USER_ID = 1L;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticationController authenticationController;


    private UserLoginDto userLoginDto;

    @BeforeEach
    void setUp() {
        userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(TEST_USERNAME);
        userLoginDto.setPassword(TEST_PASSWORD);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnToken() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = new User(TEST_USERNAME, TEST_PASSWORD, Collections.emptyList());
        
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(TEST_USERNAME)).thenReturn(TEST_JWT);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");

        String token = authenticationController.authenticateUser(userLoginDto);

        assertNotNull(token);
        assertEquals(TEST_JWT, token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(TEST_USERNAME);
    }

}
