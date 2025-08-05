package com.brclys.thct.delegate;

import com.brclys.thct.delegate.exception.BrclysApiErrorType;
import com.brclys.thct.delegate.exception.BrclysApiException;
import com.brclys.thct.entity.*;
import com.brclys.thct.mapper.UserMapper;
import com.brclys.thct.openApiGenSrc.api.V1ApiDelegate;
import com.brclys.thct.openApiGenSrc.model.*;
import com.brclys.thct.repository.BankAccountRepository;
import com.brclys.thct.repository.TransactionRepository;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.brclys.thct.AppConstants.BEARER_TOKEN;
import static com.brclys.thct.delegate.Util.generateAccountNumber;
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

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<BankAccountResponse> createAccount(CreateBankAccountRequest createBankAccountRequest) {
        String token = getBearerAuth();
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Username (%s) not found", username)));


        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(generateAccountNumber());
        bankAccount.setSortCode(BankAccountResponse.SortCodeEnum._10_10_10.getValue());
        bankAccount.setName(createBankAccountRequest.getName());
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setCreatedTimestamp(OffsetDateTime.now());
        bankAccount.setUpdatedTimestamp(OffsetDateTime.now());

        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        logger.warn("BankAccounts: {}", user.getBankAccounts());
        user.getBankAccounts().add(bankAccount);
        // logger.warn("User's bankAccounts: {}", user.getBankAccounts());
        User savedUser = userRepository.save(user);
        userRepository.flush();
        logger.warn("Saved user: {}; bankAccount: {}", savedUser, savedAccount);
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
    // Not tested manually
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        try {
            String token = getBearerAuth();
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Username (%s) not found", username)));

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
    @Transactional
    // Partially tested manually
    public ResponseEntity<Void> deleteUserByID(String userId) {
        try {
            String usernameFromToken = jwtUtil.getUsernameFromToken(getBearerAuth());
            User loggedInUser = userRepository.findByUsername(usernameFromToken)
                    .orElseThrow(() -> new UsernameNotFoundException("Logged in user not found"));

            // Check if the logged-in user is trying to delete themselves or has admin rights
            if (!loggedInUser.getId().equals(userId) && !isAdmin(loggedInUser)) {
                throw new BrclysApiException(BrclysApiErrorType.FORBIDDEN,
                        String.format("User %s is not authorized to delete user %s", usernameFromToken, userId));
            }

            User userToDelete = userRepository.findById(userId)
                    .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND,
                            String.format("UserId (%s) not found", userId)));

            List<BankAccount> bankAccounts = bankAccountRepository.findDistinctByUsersIn(Stream.of(userToDelete).map(User::getId).toList());
            if (bankAccounts != null && !bankAccounts.isEmpty()) {
                throw new BrclysApiException(BrclysApiErrorType.BAD_REQUEST,
                        String.format("User %s has bank accounts and cannot be deleted", userId));
            }

            // Delete the user
            userRepository.delete(userToDelete);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (BrclysApiException e) {
            logger.warn("Cannot delete user with bank accounts: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while deleting the user", e);
        }
    }

    private boolean isAdmin(User user) {
        // Implement admin check logic here
        // For now, returning false as we don't have admin roles implemented
        return false;
    }

    // Partially tested
    @Override
    // @Transactional(readOnly = true)
    public ResponseEntity<ListTransactionsResponse> listAccountTransaction(String accountNumber) {
        try {
            // Get the current user from the token
            String username = jwtUtil.getUsernameFromToken(getBearerAuth());
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Username (%s) not found", username)));

            List<Transaction> transactions = transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(
                    bankAccountRepository.findByAccountNumberAndUsersIn(accountNumber, Stream.of(currentUser).toList())
                            .orElseGet(() -> {
                                if (isAdmin(currentUser)) {
                                    return bankAccountRepository.findByAccountNumber(accountNumber)
                                            .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Account %s not found", accountNumber)));
                                } else {
                                    throw new BrclysApiException(BrclysApiErrorType.FORBIDDEN, String.format("User %s is not authorized to view transactions for account %s", username, accountNumber));
                                }
                            }));
            return ResponseEntity.ok(buildListOfTTransactionResponses(transactions));
        } catch (BrclysApiException e) {
            logger.warn("Error retrieving transactions: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving transactions: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while retrieving transactions", e);
        }
    }

    private static ListTransactionsResponse buildListOfTTransactionResponses(List<Transaction> transactions) {
        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(tx -> new TransactionResponse()
                        .id(tx.getId())
                        .amount(tx.getAmount().doubleValue())
                        .currency(TransactionResponse.CurrencyEnum.fromValue(tx.getCurrency()))
                        .type(TransactionResponse.TypeEnum.fromValue(tx.getType().toString().toLowerCase()))
                        .reference(tx.getReference())
                        .userId(tx.getUser().getId())
                        .createdTimestamp(tx.getCreatedTimestamp()))
                .collect(Collectors.toList());

        // Create and return the response
        ListTransactionsResponse response = new ListTransactionsResponse();
        response.setTransactions(transactionResponses);
        return response;
    }

    // Not tested manually
    @Override
    @Transactional
    public ResponseEntity<TransactionResponse> createTransaction(String accountNumber, CreateTransactionRequest createTransactionRequest) {
        try {
            // Get the current user from the token
            String username = jwtUtil.getUsernameFromToken(getBearerAuth());
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Username (%s) not found", username)));

            BigDecimal amount = BigDecimal.valueOf(createTransactionRequest.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BrclysApiException(BrclysApiErrorType.BAD_REQUEST,
                        "Transaction amount must be greater than zero");
            }
            TransactionType transactionType = TransactionType.valueOf(createTransactionRequest.getType().toString().toUpperCase());
            switch (transactionType) {
                case DEPOSIT:
                    TransactionResponse transactionResponse = getTransactionResponseObject(transactionRepository.save(createTransactionEntityObject(createTransactionRequest,
                            amount, transactionType, currentUser, depositToAccount(accountNumber, amount))), currentUser);
                    return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
                case WITHDRAWAL:
                    Transaction savedTransaction;
                    BankAccount bankAccount = bankAccountRepository.findByAccountNumberAndUsersIn(accountNumber,
                                    Stream.of(currentUser).toList())
                            .orElseGet(() -> {
                                if (isAdmin(currentUser)) {
                                    return bankAccountRepository.findByAccountNumber(accountNumber)
                                            .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND,
                                                    String.format("Account %s not found", accountNumber)));
                                } else {
                                    throw new BrclysApiException(BrclysApiErrorType.FORBIDDEN,
                                            String.format("User %s is not authorized to withdraw from account %s", username, accountNumber));
                                }
                            });
                    BankAccount bankAccountAfterWithdrawal = withdrawFromAccount(bankAccount, amount);
                    Transaction transaction = createTransactionEntityObject(createTransactionRequest, amount, transactionType, currentUser, bankAccountAfterWithdrawal);
                    savedTransaction = transactionRepository.save(transaction);
                    return new ResponseEntity<>(getTransactionResponseObject(savedTransaction, currentUser), HttpStatus.CREATED);
                default:
                    throw new BrclysApiException(BrclysApiErrorType.BAD_REQUEST,
                            String.format("Unsupported transaction type: %s", transactionType));
            }

        } catch (BrclysApiException e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while creating the transaction", e);
        }
    }

    private BankAccount withdrawFromAccount(BankAccount bankAccount, BigDecimal amount) {
        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new BrclysApiException(BrclysApiErrorType.BAD_REQUEST,
                    "Insufficient funds for withdrawal");
        }
        BigDecimal newBalance = bankAccount.getBalance().subtract(amount);
        bankAccount.setBalance(newBalance);
        bankAccount.setUpdatedTimestamp(OffsetDateTime.now());
        return bankAccountRepository.save(bankAccount);
    }

    private BankAccount depositToAccount(String accountNumber, BigDecimal amount) {
        BigDecimal newBalance;
        BankAccount bankAccount = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.BAD_REQUEST,
                        String.format("Bank account not found with account number: %s", accountNumber)));
        newBalance = bankAccount.getBalance().add(amount);
        bankAccount.setBalance(newBalance);
        bankAccount.setUpdatedTimestamp(OffsetDateTime.now());
        return bankAccountRepository.save(bankAccount);
    }

    private static TransactionResponse getTransactionResponseObject(Transaction savedTransaction, User currentUser) {
        return new TransactionResponse()
                .id(savedTransaction.getId())
                .amount(savedTransaction.getAmount().doubleValue())
                .currency(TransactionResponse.CurrencyEnum.fromValue(savedTransaction.getCurrency()))
                .type(TransactionResponse.TypeEnum.fromValue(savedTransaction.getType().toString().toLowerCase()))
                .reference(savedTransaction.getReference())
                .userId(currentUser.getId())
                .createdTimestamp(savedTransaction.getCreatedTimestamp());
    }

    private static Transaction createTransactionEntityObject(CreateTransactionRequest createTransactionRequest, BigDecimal amount, TransactionType transactionType, User currentUser, BankAccount bankAccount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setCurrency(createTransactionRequest.getCurrency().toString());
        transaction.setType(transactionType);
        transaction.setReference(createTransactionRequest.getReference());
        transaction.setUser(currentUser);
        transaction.setBankAccount(bankAccount);
        transaction.setCreatedTimestamp(OffsetDateTime.now());
        return transaction;
    }

    // Not tested manually
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> deleteAccountByAccountNumber(String accountNumber) {
        try {
            String username = jwtUtil.getUsernameFromToken(getBearerAuth());
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Username (%s) not found", username)));


            BankAccount bankAccount = bankAccountRepository.findByAccountNumberAndUsersIn(accountNumber, Collections.singletonList(currentUser))
                    .orElseGet(() -> {
                        var account =bankAccountRepository.findByAccountNumber(accountNumber)
                                .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Account %s not found", accountNumber)));
                        if (isAdmin(currentUser)) {
                            return account;
                        } else {
                            throw new BrclysApiException(BrclysApiErrorType.FORBIDDEN,
                                    String.format("User %s is not authorized to delete account %s", username, accountNumber));
                        }
                    });

            if (bankAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException(
                        String.format("Cannot delete account %s with non-zero balance: %s",
                                accountNumber, bankAccount.getBalance()));
            }
            Set<User> users = userRepository.findByBankAccountsIn(new HashSet<>(Collections.singletonList(bankAccount)));
            logger.warn("BankAccounts: {}", bankAccount.getUsers());
            for (User user: users) {
                logger.warn("Accounts: {}", user.getBankAccounts());
                user.removeBankAccount(bankAccount);
                // user.setUpdatedTimestamp(OffsetDateTime.now());
                logger.warn("User's bankAccounts: {}", user.getBankAccounts());
                userRepository.save(user);
                userRepository.flush();
            }

            bankAccountRepository.delete(bankAccount);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BrclysApiException e) {
            logger.warn("Cannot delete account: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting bank account: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while deleting the bank account", e);
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
    // Not tested manually
    public ResponseEntity<UserResponse> fetchUserByID(String userId) {
        String usernameFromToken = jwtUtil.getUsernameFromToken(getBearerAuth());
        String loggedInUserId = userRepository.findByUsername(usernameFromToken).orElseThrow(() -> new RuntimeException("Logged in User not found")).getId();
        if (!loggedInUserId.equals(userId)) {
            throw new BrclysApiException(BrclysApiErrorType.FORBIDDEN, String.format("User %s is not allowed to access user %s",
                    usernameFromToken, userId));
        }
        return userRepository.findById(userId).map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND,
                        String.format("User %s not found", userId)));

    }

    @Override
    @Transactional
    // Not tested manually
    public ResponseEntity<UserResponse> updateUserByID(String userId, UpdateUserRequest updateUserRequest) {
        String usernameFromToken = jwtUtil.getUsernameFromToken(getBearerAuth());
        User loggedInUser = userRepository.findByUsername(usernameFromToken)
                .orElseThrow(() -> new UsernameNotFoundException("Logged in user not found"));

        if (!loggedInUser.getId().equals(userId)) {
            throw new BrclysApiException(BrclysApiErrorType.FORBIDDEN,
                    String.format("User %s is not authorized to update user %s", usernameFromToken, userId));
        }


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND,
                        String.format("UserId %s not found", userId)));


        if (updateUserRequest.getName() != null) {
            user.setUsername(updateUserRequest.getName());
        }

        if (updateUserRequest.getEmail() != null) {
            // Check if email is already taken by another user
            userRepository.findByEmail(updateUserRequest.getEmail())
                    .filter(u -> !u.getId().equals(userId))
                    .ifPresent(u -> {
                        throw new BrclysApiException(BrclysApiErrorType.BAD_REQUEST,
                                String.format("Email %s is in use.", updateUserRequest.getEmail()));
                    });
            user.setEmail(updateUserRequest.getEmail());
        }

        if (updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }

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
    // Not tested manually
    public ResponseEntity<BankAccountResponse> updateAccountByAccountNumber(
            String accountNumber,
            UpdateBankAccountRequest updateBankAccountRequest) {

        String username = jwtUtil.getUsernameFromToken(getBearerAuth());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        BankAccount bankAccount = bankAccountRepository.findByAccountNumberAndUsersIn(accountNumber, Collections.singletonList(user))
                .orElseThrow(() -> new BrclysApiException(BrclysApiErrorType.NOT_FOUND, String.format("Bank account not found with account number: %s", accountNumber)));

        if (updateBankAccountRequest.getName() != null) {
            bankAccount.setName(updateBankAccountRequest.getName());
        }

        if (updateBankAccountRequest.getAccountType() != null) {
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
