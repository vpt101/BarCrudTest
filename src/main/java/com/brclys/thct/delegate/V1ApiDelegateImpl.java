package com.brclys.thct.delegate;

import com.brclys.thct.entity.User;
import com.brclys.thct.exception.DuplicateUserException;
import com.brclys.thct.mapper.UserMapper;
import com.brclys.thct.openApiGenSrc.api.V1ApiDelegate;
import com.brclys.thct.openApiGenSrc.model.CreateUserRequest;
import com.brclys.thct.openApiGenSrc.model.UserResponse;
import com.brclys.thct.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class V1ApiDelegateImpl implements V1ApiDelegate {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new DuplicateUserException("Email " + createUserRequest.getEmail() + " is already in use");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(createUserRequest.getPhoneNumber())) {
            throw new DuplicateUserException("Phone number " + createUserRequest.getPhoneNumber() + " is already in use");
        }
        User user = userMapper.toEntity(createUserRequest);
        User savedUser = userRepository.save(user);
        UserResponse response = userMapper.toResponse(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
