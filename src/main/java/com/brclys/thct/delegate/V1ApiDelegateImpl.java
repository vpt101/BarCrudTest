package com.brclys.thct.delegate;

import com.brclys.thct.delegate.exception.UnauthorizedAccessAttemptedException;
import com.brclys.thct.entity.Address;
import com.brclys.thct.entity.BankAccount;
import com.brclys.thct.entity.User;
import com.brclys.thct.mapper.UserMapper;
import com.brclys.thct.openApiGenSrc.api.V1ApiDelegate;
import com.brclys.thct.openApiGenSrc.model.*;
import com.brclys.thct.repository.BankAccountRepository;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.brclys.thct.AppConstants.BEARER_TOKEN;
import static com.brclys.thct.delegate.Util.generateAccountNumber;
import static com.brclys.thct.delegate.Util.generateSortCode;
import static com.brclys.thct.delegate.ValidationUtil.validateUserUniqueness;

@Service
public class V1ApiDelegateImpl implements V1ApiDelegate {
    Logger logger = LoggerFactory.getLogger(V1ApiDelegateImpl.class);
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NativeWebRequest nativeWebRequest;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    @Transactional
    public ResponseEntity<BankAccountResponse> createAccount(CreateBankAccountRequest createBankAccountRequest) {
            String token = getBearerAuth();
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            
            // Create new bank account with the current user
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber(generateAccountNumber());
            bankAccount.setSortCode(BankAccountResponse.SortCodeEnum._10_10_10.getValue());
            bankAccount.setName(createBankAccountRequest.getName());
            bankAccount.setBalance(BigDecimal.ZERO);
            bankAccount.setCreatedTimestamp(OffsetDateTime.now());
            bankAccount.setUpdatedTimestamp(OffsetDateTime.now());
            if (bankAccount.getUsers() == null) {
                bankAccount.setUsers(new HashSet<>());
            }
            
            BankAccount savedAccount = bankAccountRepository.save(bankAccount);
            
            if (user.getBankAccounts() == null) {
                user.setBankAccounts(new HashSet<>());
            }
            user.getBankAccounts().add(savedAccount);
            userRepository.save(user);

            BankAccountResponse response = new BankAccountResponse()
                .accountNumber(savedAccount.getAccountNumber())
                .sortCode(BankAccountResponse.SortCodeEnum.fromValue(savedAccount.getSortCode()))
                .name(savedAccount.getName())
                .balance(savedAccount.getBalance().doubleValue())
                .currency(BankAccountResponse.CurrencyEnum.GBP)
                .accountType(BankAccountResponse.AccountTypeEnum.fromValue(createBankAccountRequest.getAccountType().getValue()))
                .createdTimestamp(savedAccount.getCreatedTimestamp())
                .updatedTimestamp(savedAccount.getCreatedTimestamp());
                
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            

    }

    @Override
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        try {
            // Get user from JWT token
            String token = getBearerAuth();
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            
            List<BankAccount> accounts = bankAccountRepository.findDistinctByUsersIn(Stream.of(user).map(User::getId).toList());
            
            List<BankAccountResponse> accountResponses = accounts.stream()
                .map(account -> new BankAccountResponse()
                    .accountNumber(account.getAccountNumber())
                    .sortCode(BankAccountResponse.SortCodeEnum.fromValue(account.getSortCode()))
                    .name(account.getName())
                    .balance(account.getBalance().doubleValue())
                    .currency(BankAccountResponse.CurrencyEnum.GBP)
                    .accountType(BankAccountResponse.AccountTypeEnum.PERSONAL)
                    .createdTimestamp(account.getCreatedTimestamp())
                    .updatedTimestamp(account.getUpdatedTimestamp()))
                .collect(Collectors.toList());
            
            ListBankAccountsResponse response = new ListBankAccountsResponse()
                .accounts(accountResponses);
                
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Failed to list bank accounts", e);
            throw new RuntimeException("Failed to list bank accounts", e);
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(nativeWebRequest);
    }

    public String getBearerAuth() {
        return Objects.requireNonNull(nativeWebRequest.getHeader("Authorization")).replace(BEARER_TOKEN, "");
    }


    @Override
    @Transactional
    public ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        // Check if email already exists
        validateUserUniqueness(userRepository, createUserRequest);

        User user = userMapper.toEntity(createUserRequest);
        User savedUser = userRepository.save(user);
        UserResponse response = userMapper.toResponse(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserResponse> fetchUserByID(String userId) {
        String usernameFromToken = jwtUtil.getUsernameFromToken(getBearerAuth());
        String loggedInUserId = userRepository.findByUsername(usernameFromToken).orElseThrow(() -> new RuntimeException("Logged in User not found")).getId();
        if(!loggedInUserId.equals(userId)) {
            throw new UnauthorizedAccessAttemptedException(String.format("User %s is not allowed to access user %s",
                    usernameFromToken, userId));
        }
        return userRepository.findById(userId).map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional
    public ResponseEntity<UserResponse> updateUserByID(String userId, UpdateUserRequest updateUserRequest) {
        // Verify user exists and has permission
        String usernameFromToken = jwtUtil.getUsernameFromToken(getBearerAuth());
        User loggedInUser = userRepository.findByUsername(usernameFromToken)
            .orElseThrow(() -> new UsernameNotFoundException("Logged in user not found"));
            
        if (!loggedInUser.getId().equals(userId)) {
            throw new UnauthorizedAccessAttemptedException(
                String.format("User %s is not authorized to update user %s", usernameFromToken, userId));
        }
        
        // Find the user to update
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        // Update fields if they are provided in the request
        if (updateUserRequest.getName() != null) {
            user.setUsername(updateUserRequest.getName());
        }
        
        if (updateUserRequest.getEmail() != null) {
            // Check if email is already taken by another user
            userRepository.findByEmail(updateUserRequest.getEmail())
                .filter(u -> !u.getId().equals(userId))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Email is already in use by another user");
                });
            user.setEmail(updateUserRequest.getEmail());
        }
        
        if (updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }
        
        // Update address if provided
        if (updateUserRequest.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(new Address());
            }
            if (updateUserRequest.getAddress().getLine1() != null) {
                user.getAddress().setLine1(updateUserRequest.getAddress().getLine1());
            }
            if (updateUserRequest.getAddress().getLine2() != null) {
                user.getAddress().setLine2(updateUserRequest.getAddress().getLine2());
            }
            if (updateUserRequest.getAddress().getTown() != null) {
                user.getAddress().setTown(updateUserRequest.getAddress().getTown());
            }
            if (updateUserRequest.getAddress().getPostcode() != null) {
                user.getAddress().setPostcode(updateUserRequest.getAddress().getPostcode());
            }

        }
        
        user.setUpdatedTimestamp(OffsetDateTime.now());
        User updatedUser = userRepository.save(user);
        
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Override
    @Transactional
    public ResponseEntity<BankAccountResponse> updateAccountByAccountNumber(
            String accountNumber, 
            UpdateBankAccountRequest updateBankAccountRequest) {
        
        // Get the current user from the token
        String username = jwtUtil.getUsernameFromToken(getBearerAuth());
        logger.warn(">>>>>>>>User {} is updating account {}", username, accountNumber);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        // Find the bank account and verify the user has access to it
        logger.warn(">>>>>>>>User {} has id {}", username, user.getId());
        BankAccount bankAccount = bankAccountRepository.findByAccountNumberAndUsersIn(accountNumber, Collections.singletonList(user))
            .orElseThrow(() -> new RuntimeException("Bank account not found with number: " + accountNumber));
        Set<User> users = bankAccount.getUsers();

        if (updateBankAccountRequest.getName() != null) {
            bankAccount.setName(updateBankAccountRequest.getName());
        }
        
        if (updateBankAccountRequest.getAccountType() != null) {
            // In a real app, you might have logic to handle different account types
            // For now, we'll just log it as we don't have a direct mapping in the entity
            logger.debug("Account type update requested to: {}", updateBankAccountRequest.getAccountType());
        }
        
        bankAccount.setUpdatedTimestamp(OffsetDateTime.now());
        BankAccount updatedAccount = bankAccountRepository.save(bankAccount);
        
        // Convert to response DTO
        BankAccountResponse response = new BankAccountResponse()
            .accountNumber(updatedAccount.getAccountNumber())
            .sortCode(BankAccountResponse.SortCodeEnum.fromValue(updatedAccount.getSortCode()))
            .name(updatedAccount.getName())
            .balance(updatedAccount.getBalance().doubleValue())
            .currency(BankAccountResponse.CurrencyEnum.GBP)
            .accountType(BankAccountResponse.AccountTypeEnum.PERSONAL) // Default for now
            .createdTimestamp(updatedAccount.getCreatedTimestamp())
            .updatedTimestamp(updatedAccount.getUpdatedTimestamp());
            
        return ResponseEntity.ok(response);
    }
}
