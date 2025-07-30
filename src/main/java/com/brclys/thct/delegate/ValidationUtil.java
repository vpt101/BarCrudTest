package com.brclys.thct.delegate;

import com.brclys.thct.exception.DuplicateUserException;
import com.brclys.thct.openApiGenSrc.model.CreateUserRequest;
import com.brclys.thct.repository.UserRepository;


public class ValidationUtil {

    static void  validateUserUniqueness(UserRepository userRepository, CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new DuplicateUserException("Email " + createUserRequest.getEmail() + " is already in use");
        }
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(createUserRequest.getPhoneNumber())) {
            throw new DuplicateUserException("Phone number " + createUserRequest.getPhoneNumber() + " is already in use");
        }
        if (userRepository.existsByUsername(createUserRequest.getName())) {
            throw new DuplicateUserException("Username " + createUserRequest.getName() + " is already in use");
        }
    }
}
