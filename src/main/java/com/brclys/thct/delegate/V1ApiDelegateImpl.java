package com.brclys.thct.delegate;

import com.brclys.thct.delegate.exception.UnauthorizedAccessAttemptedException;
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

}
