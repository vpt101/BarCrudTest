package com.brclys.thct.mapper;

import com.brclys.thct.entity.User;
import com.brclys.thct.entity.Address;
import com.brclys.thct.openApiGenSrc.model.CreateUserRequest;
import com.brclys.thct.openApiGenSrc.model.CreateUserRequestAddress;
import com.brclys.thct.openApiGenSrc.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    @Autowired
    private PasswordEncoder encoder;

    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getName())); // TODO: Use an actual password;
        
        if (request.getAddress() != null) {
            CreateUserRequestAddress requestAddress = request.getAddress();
            Address address = new Address(
                requestAddress.getLine1(),
                requestAddress.getLine2(),
                requestAddress.getLine3(),
                requestAddress.getTown(),
                requestAddress.getCounty(),
                requestAddress.getPostcode()
            );
            user.setAddress(address);
        }

        return user;
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId().toString());
        response.setName(user.getUsername());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setCreatedTimestamp(user.getCreatedTimestamp());
        response.setUpdatedTimestamp(user.getUpdatedTimestamp());
        
        if (user.getAddress() != null) {
            Address address = user.getAddress();
            CreateUserRequestAddress responseAddress = new CreateUserRequestAddress()
                .line1(address.getLine1())
                .line2(address.getLine2())
                .line3(address.getLine3())
                .town(address.getTown())
                .county(address.getCounty())
                .postcode(address.getPostcode());
            response.setAddress(responseAddress);
        }
        
        return response;
    }
}
