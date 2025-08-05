package com.brclys.thct.delegate;

import com.brclys.thct.delegate.exception.BrclysApiErrorType;
import com.brclys.thct.delegate.exception.BrclysApiException;
import com.brclys.thct.entity.*;
import com.brclys.thct.exception.DuplicateUserException;
import com.brclys.thct.mapper.UserMapper;
import com.brclys.thct.openApiGenSrc.model.*;
import com.brclys.thct.repository.BankAccountRepository;
import com.brclys.thct.repository.TransactionRepository;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class V1ApiDelegateImplTest {

    private static final String TEST_USER_ID = "usr-123";
    private static final String TEST_ACCOUNT_NUMBER = "12345678";
    private static final String TEST_TOKEN = "test.token";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "+1234567890";
    private static final String TEST_PASSWORD = "password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private NativeWebRequest nativeWebRequest;

    @InjectMocks
    private V1ApiDelegateImpl v1ApiDelegate;

    private User testUser;
    private BankAccount testAccount;
    private User adminUser;


    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPhoneNumber(TEST_PHONE);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setBankAccounts(new HashSet<>());

        // Setup admin user
        adminUser = new User();
        adminUser.setId("admin-123");
        adminUser.setUsername("root");
        adminUser.setEmail("root@obey.me");


        // Setup test account
        testAccount = new BankAccount();
        testAccount.setAccountNumber(TEST_ACCOUNT_NUMBER);
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setUsers(Set.of(testUser));
        testAccount.setSortCode("10-10-10");
        testAccount.setCreatedTimestamp(OffsetDateTime.now());
        testAccount.setUpdatedTimestamp(OffsetDateTime.now());

        testUser.getBankAccounts().add(testAccount);

        // Common mock behaviors
        when(jwtUtil.getUsernameFromToken(any())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(nativeWebRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
    }
    @Test
    void createAccount_ShouldCreateNewAccount() {
        CreateBankAccountRequest request = new CreateBankAccountRequest()
            .name("Savings Account")
            .accountType(CreateBankAccountRequest.AccountTypeEnum.PERSONAL);

        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> {
            BankAccount account = invocation.getArgument(0);
            account.setAccountNumber("NEW-ACC-123");
            return account;
        });
        ResponseEntity<BankAccountResponse> response = v1ApiDelegate.createAccount(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NEW-ACC-123", response.getBody().getAccountNumber());
        assertEquals("Savings Account", response.getBody().getName());
        assertEquals(0.0, response.getBody().getBalance());
        verify(bankAccountRepository).save(any(BankAccount.class));
        verify(userRepository).save(testUser);
    }
    @Test
    void listAccounts_ShouldReturnAllAccountsForUser() {
        when(bankAccountRepository. findDistinctByUsersIn(anyList())).thenReturn(List.of(testAccount));
        ResponseEntity<ListBankAccountsResponse> response = v1ApiDelegate.listAccounts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getAccounts().size());
        assertEquals(TEST_ACCOUNT_NUMBER, response.getBody().getAccounts().get(0).getAccountNumber());
    }
    @Test
    void deleteUserByID_ShouldDeleteUserWhenAuthorized() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        ResponseEntity<Void> response = v1ApiDelegate.deleteUserByID(TEST_USER_ID);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).delete(testUser);
    }
    @Test
    void createTransaction_ShouldProcessDeposit() {
        CreateTransactionRequest request = new CreateTransactionRequest()
            .amount(100.0)
            .currency(CreateTransactionRequest.CurrencyEnum.GBP)
            .type(CreateTransactionRequest.TypeEnum.DEPOSIT)
            .reference("Test deposit");

        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId("txn-123");
            return t;
        });
        ResponseEntity<TransactionResponse> response = v1ApiDelegate.createTransaction(
            TEST_ACCOUNT_NUMBER, request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals("deposit", response.getBody().getType().getValue());
        verify(transactionRepository).save(any(Transaction.class));
    }
    @Test
    void listAccountTransaction_ShouldReturnTransactionsForAccount() {
        Transaction transaction = new Transaction();
        transaction.setId("txn-123");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCurrency("GBP");
        transaction.setCreatedTimestamp(OffsetDateTime.now());
        transaction.setUser(testUser);
        transaction.setBankAccount(testAccount);

        when(bankAccountRepository.findByAccountNumberAndUsersIn(TEST_ACCOUNT_NUMBER, List.of(testUser)))
            .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
            .thenReturn(List.of(transaction));
        ResponseEntity<ListTransactionsResponse> response = 
            v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTransactions().size());
        assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
    }
    @Test
    void deleteAccountByAccountNumber_ShouldDeleteAccountWhenBalanceIsZero() {
        // Test case 1: Regular user with zero balance account they own
        testAccount.setBalance(BigDecimal.ZERO);
        when(bankAccountRepository.findByAccountNumberAndUsersIn(TEST_ACCOUNT_NUMBER, List.of(testUser)))
            .thenReturn(Optional.of(testAccount));
        
        ResponseEntity<Void> response = v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bankAccountRepository).delete(testAccount);
    }
    
    @Test
    void deleteAccountByAccountNumber_ShouldAllowAdminToDeleteAnyAccount() {
        Mockito.reset(userRepository);
        BankAccount otherUserAccount = new BankAccount();
        otherUserAccount.setAccountNumber("OTHER-ACC-123");
        otherUserAccount.setBalance(BigDecimal.ZERO);
        otherUserAccount.setUsers(Set.of(testUser));
        
        // First findByAccountNumberAndUsersIn returns empty (user doesn't own the account)
        when(bankAccountRepository.findByAccountNumberAndUsersIn("OTHER-ACC-123", List.of(adminUser)))
            .thenReturn(Optional.empty());
        // Then findByAccountNumber returns the account (admin path)
        when(bankAccountRepository.findByAccountNumber("OTHER-ACC-123"))
            .thenReturn(Optional.of(otherUserAccount));
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("root");
        when(userRepository.findByUsername("root")).thenReturn(Optional.of(adminUser));

        ResponseEntity<Void> response = v1ApiDelegate.deleteAccountByAccountNumber("OTHER-ACC-123");
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bankAccountRepository).delete(otherUserAccount);
    }
    
    @Test
    void deleteAccountByAccountNumber_ShouldThrowExceptionWhenAccountNotFound() {
        // Test case 3: Account doesn't exist
        when(bankAccountRepository.findByAccountNumberAndUsersIn("NON-EXISTENT", List.of(testUser)))
            .thenReturn(Optional.empty());
        when(bankAccountRepository.findByAccountNumber("NON-EXISTENT"))
            .thenReturn(Optional.empty());
        
        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
            v1ApiDelegate.deleteAccountByAccountNumber("NON-EXISTENT");
        });
        
        assertEquals("User or Resource not found: Account NON-EXISTENT not found", exception.getMessage());
        verify(bankAccountRepository, never()).delete(any());
    }
    
    @Test
    void deleteAccountByAccountNumber_ShouldThrowForbiddenForNonOwnerNonAdmin() {
        // Test case 4: Regular user tries to delete someone else's account
        BankAccount otherUserAccount = new BankAccount();
        otherUserAccount.setAccountNumber("OTHER-ACC-123");
        otherUserAccount.setUsers(Set.of(new User())); // Different user
        
        when(bankAccountRepository.findByAccountNumberAndUsersIn("OTHER-ACC-123", List.of(testUser)))
            .thenReturn(Optional.empty());
        when(bankAccountRepository.findByAccountNumber("OTHER-ACC-123"))
            .thenReturn(Optional.of(otherUserAccount));
        // User is not admin
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        
        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
            v1ApiDelegate.deleteAccountByAccountNumber("OTHER-ACC-123");
        });
        
        assertTrue(exception.getMessage().contains("is not authorized to delete account"));
        verify(bankAccountRepository, never()).delete(any());
    }
    
    @Test
    void deleteAccountByAccountNumber_ShouldThrowWhenBalanceNotZero() {
        // Test case 5: Account has non-zero balance
        testAccount.setBalance(new BigDecimal("100.00"));
        when(bankAccountRepository.findByAccountNumberAndUsersIn(TEST_ACCOUNT_NUMBER, List.of(testUser)))
            .thenReturn(Optional.of(testAccount));
        
        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
        });
        assertEquals(String.format("Bad request: Cannot delete account %s with non-zero balance. Current balance: %s",
                TEST_ACCOUNT_NUMBER, "100.00"), exception.getMessage());

        verify(bankAccountRepository, never()).delete(any());
    }
    @Test
    void createUser_ShouldCreateNewUser() {
        CreateUserRequest request = new CreateUserRequest()
            .name("newuser")
            .email("newuser@example.com")
            .phoneNumber("+1987654321");

        Mockito.reset(jwtUtil, userRepository, nativeWebRequest);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenAnswer(invocation -> {
            User user = new User();
            user.setUsername(((CreateUserRequest)invocation.getArgument(0)).getName());
            user.setEmail(((CreateUserRequest)invocation.getArgument(0)).getEmail());
            user.setPhoneNumber(((CreateUserRequest)invocation.getArgument(0)).getPhoneNumber());
            return user;
        });
        when(userMapper.toResponse(any(User.class))).thenCallRealMethod();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("new-user-123");
            return user;
        });
        ResponseEntity<UserResponse> response = v1ApiDelegate.createUser(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newuser", response.getBody().getName());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void fetchUserByID_ShouldReturnUserWhenAuthorized() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(new UserResponse()
            .id(TEST_USER_ID)
            .name(TEST_USERNAME)
            .email(TEST_EMAIL));
        ResponseEntity<UserResponse> response = v1ApiDelegate.fetchUserByID(TEST_USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_USERNAME, response.getBody().getName());
        assertEquals(TEST_EMAIL, response.getBody().getEmail());
    }
    @Test
    void updateUserByID_ShouldUpdateUserWhenAuthorized() {
        UpdateUserRequest request = new UpdateUserRequest()
            .email("updated@example.com")
            .phoneNumber("+1987654321");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserResponse()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber());
        });
        ResponseEntity<UserResponse> response = v1ApiDelegate.updateUserByID(TEST_USER_ID, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updated@example.com", response.getBody().getEmail());
        assertEquals("+1987654321", response.getBody().getPhoneNumber());
    }
    @Test
    void updateAccountByAccountNumber_ShouldUpdateAccountWhenAuthorized() {
        UpdateBankAccountRequest request = new UpdateBankAccountRequest()
            .name("Updated Account Name");

        BankAccount testAccount = new BankAccount();
        testAccount.setAccountNumber(TEST_ACCOUNT_NUMBER);
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setUsers(Set.of(testUser));
        testAccount.setCreatedTimestamp(OffsetDateTime.now());
        testAccount.setUpdatedTimestamp(OffsetDateTime.now());
        testAccount.setSortCode("10-10-10");
        when(bankAccountRepository.findByAccountNumberAndUsersIn(TEST_ACCOUNT_NUMBER, List.of(testUser)))
            .thenReturn(Optional.of(testAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation ->
                testAccount);
        ResponseEntity<BankAccountResponse> response = v1ApiDelegate.updateAccountByAccountNumber(
            TEST_ACCOUNT_NUMBER, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Account Name", response.getBody().getName());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }
    @Test
    void getBearerAuth_ShouldReturnTokenWithoutBearerPrefix() {
        Mockito.reset(jwtUtil, userRepository);
        when(nativeWebRequest.getHeader("Authorization")).thenReturn("Bearer test.token");
        String token = v1ApiDelegate.getBearerAuth();
        assertEquals("test.token", token);
    }
    @Test
    void createUser_ShouldThrowExceptionWhenUsernameExists() {
        Mockito.reset(jwtUtil, userRepository, nativeWebRequest);
        CreateUserRequest request = new CreateUserRequest()
            .name("existinguser")
            .email("test@example.com");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
            v1ApiDelegate.createUser(request);
        });


        assertEquals("Username existinguser is already in use", exception.getMessage());
    }
}
