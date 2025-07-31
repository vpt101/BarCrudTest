//package com.brclys.thct.delegate;
//
//import com.brclys.thct.delegate.exception.BrclysApiException;
//import com.brclys.thct.entity.*;
//import com.brclys.thct.exception.DuplicateUserException;
//import com.brclys.thct.openApiGenSrc.model.*;
//import com.brclys.thct.repository.BankAccountRepository;
//import com.brclys.thct.repository.TransactionRepository;
//import com.brclys.thct.repository.UserRepository;
//import com.brclys.thct.security.JwtUtil;
//import org.junit.Ignore;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.context.request.NativeWebRequest;
//
//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class V1ApiDelegateImplTest {
//
//    private static final String TEST_USER_ID = "usr-123";
//    private static final String TEST_ACCOUNT_NUMBER = "12345678";
//    private static final String TEST_TOKEN = "test.token";
//    private static final String TEST_USERNAME = "testuser";
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private BankAccountRepository bankAccountRepository;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private JwtUtil jwtUtil;
//    @Mock
//    private NativeWebRequest nativeWebRequest;
//
//
//    @InjectMocks
//    private V1ApiDelegateImpl v1ApiDelegate;
//
//    private User testUser;
//    private BankAccount testAccount;
//
//
//    @BeforeEach
//    void setUp() {
//        testUser = new User();
//        testUser.setId(TEST_USER_ID);
//        testUser.setUsername(TEST_USERNAME);
//        testUser.setBankAccounts(new HashSet<>());
//
//        testAccount = new BankAccount();
//        testAccount.setAccountNumber(TEST_ACCOUNT_NUMBER);
//        testAccount.setBalance(BigDecimal.ZERO);
//        testAccount.setUsers(Set.of(testUser));
//
//        testUser.getBankAccounts().add(testAccount);
//
//        when(jwtUtil.getUsernameFromToken(any())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(nativeWebRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
//    }
//
//    @Test
//    void deleteUserByID_ShouldDeleteUser() {
//        // Arrange
//        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
//
//        // Act
//        ResponseEntity<Void> response = v1ApiDelegate.deleteUserByID(TEST_USER_ID);
//
//        // Assert
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(userRepository).delete(testUser);
//    }
//
//    @Test
//    void createTransaction_ShouldCreateDeposit() {
//        // Arrange
//        CreateTransactionRequest request = new CreateTransactionRequest()
//            .amount(100.0)
//            .currency(CreateTransactionRequest.CurrencyEnum.GBP)
//            .type(CreateTransactionRequest.TypeEnum.DEPOSIT)
//            .reference("Test deposit");
//
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//        when(bankAccountRepository.findByAccountNumberAndUsersIn(TEST_ACCOUNT_NUMBER, List.of(testUser)))
//                .thenReturn(Optional.of(testAccount));
//        var mockedTransaction =Mockito.mock(Transaction.class);
//        mockedTransaction.setId("txn-123");
//        mockedTransaction.setAmount(new BigDecimal("100.00"));
//        mockedTransaction.setType(TransactionType.DEPOSIT);
//        mockedTransaction.setCurrency("GBP");
//        mockedTransaction.setCreatedTimestamp(OffsetDateTime.now());
//        mockedTransaction.setUser(testUser);
//        mockedTransaction.setBankAccount(testAccount);
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockedTransaction);
//
//        // Act
//        ResponseEntity<TransactionResponse> response = v1ApiDelegate.createTransaction(
//            TEST_ACCOUNT_NUMBER, request);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(100.0, response.getBody().getAmount());
//        assertEquals("DEPOSIT", response.getBody().getType().getValue());
//        verify(transactionRepository).save(any(Transaction.class));
//    }
//
//    @Test
//    void listAccountTransaction_ShouldReturnTransactions() {
//        // Arrange
//        Transaction transaction = new Transaction();
//        transaction.setId("txn-123");
//        transaction.setAmount(new BigDecimal("100.00"));
//        transaction.setType(TransactionType.DEPOSIT);
//        transaction.setCurrency("GBP");
//        transaction.setCreatedTimestamp(OffsetDateTime.now());
//        transaction.setUser(testUser);
//        transaction.setBankAccount(testAccount);
//
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
//            .thenReturn(List.of(transaction));
//
//        // Act
//        ResponseEntity<ListTransactionsResponse> response =
//            v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().getTransactions().size());
//        assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
//    }
//
//    @Test
//    void listAccountTransaction_ShouldReturnTransactions_WhenUserIsOwner() {
//        // Arrange
//        Transaction transaction = new Transaction();
//        transaction.setId("txn-123");
//        transaction.setAmount(new BigDecimal("100.00"));
//        transaction.setType(TransactionType.DEPOSIT);
//        transaction.setCurrency("GBP");
//        transaction.setUser(testUser);
//        transaction.setBankAccount(testAccount);
//        transaction.setCreatedTimestamp(OffsetDateTime.now());
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumberAndUsersIn(eq(TEST_ACCOUNT_NUMBER), anyList()))
//            .thenReturn(Optional.of(testAccount));
//        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
//            .thenReturn(List.of(transaction));
//
//        // Act
//        ResponseEntity<ListTransactionsResponse> response = v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().getTransactions().size());
//        assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
//        verify(transactionRepository).findByBankAccountOrderByCreatedTimestampDesc(testAccount);
//    }
//
//    @Test
//    void listAccountTransaction_ShouldThrowException_WhenAccountNotFound() {
//        // Arrange
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumberAndUsersIn(eq(TEST_ACCOUNT_NUMBER), anyList()))
//            .thenReturn(Optional.empty());
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);
//        });
//
//        assertEquals("Account " + TEST_ACCOUNT_NUMBER + " not found", exception.getMessage());
//        verify(transactionRepository, never()).findByBankAccountOrderByCreatedTimestampDesc(any());
//    }
//
//    @Test
//    void listAccountTransaction_ShouldAllowAdminAccess_WhenNotOwner() {
//        // Arrange
//        User adminUser = new User();
//        adminUser.setId("admin-123");
//        adminUser.setUsername("admin");
//
//        Transaction transaction = new Transaction();
//        transaction.setId("txn-123");
//        transaction.setAmount(new BigDecimal("100.00"));
//        transaction.setType(TransactionType.DEPOSIT);
//        transaction.setCurrency("GBP");
//        transaction.setUser(testUser);
//        transaction.setBankAccount(testAccount);
//        transaction.setCreatedTimestamp(OffsetDateTime.now());
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("admin");
//        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
//
//        // First call (non-admin path) - should return empty
//        when(bankAccountRepository.findByAccountNumberAndUsersIn(eq(TEST_ACCOUNT_NUMBER), anyList()))
//            .thenReturn(Optional.empty());
//
//        // Second call (admin path) - should return the account
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
//            .thenReturn(List.of(transaction));
//
//        // Mock isAdmin to return true for admin user
//        try (var mockedV1ApiDelegate = mockStatic(V1ApiDelegateImpl.class, CALLS_REAL_METHODS)) {
//            mockedV1ApiDelegate.when(() -> v1ApiDelegate.isAdmin(adminUser)).thenReturn(true);
//
//            // Act
//            ResponseEntity<ListTransactionsResponse> response = v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertNotNull(response.getBody());
//            assertEquals(1, response.getBody().getTransactions().size());
//            assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
//        }
//    }
//
//    @Test
//    void deleteAccountByAccountNumber_ShouldDeleteAccount_WhenBalanceIsZero() {
//        // Arrange
//        testAccount.setBalance(BigDecimal.ZERO);
//        testAccount.setUsers(Set.of(testUser));
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        // Act
//        ResponseEntity<Void> response = v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
//
//        // Assert
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        verify(bankAccountRepository).delete(testAccount);
//        verify(userRepository, times(1)).save(testUser);
//    }
//
//    @Test
//    void deleteAccountByAccountNumber_ShouldThrowException_WhenAccountNotFound() {
//        // Arrange
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.empty());
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
//        });
//
//        assertEquals("Bank account not found with account number: " + TEST_ACCOUNT_NUMBER, exception.getMessage());
//        verify(bankAccountRepository, never()).delete(any());
//    }
//
//    @Test
//    void deleteAccountByAccountNumber_ShouldThrowException_WhenBalanceNotZero() {
//        // Arrange
//        testAccount.setBalance(new BigDecimal("100.00"));
//        testAccount.setUsers(Set.of(testUser));
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        // Act & Assert
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
//        });
//
//        assertTrue(exception.getMessage().contains("Cannot delete account"));
//        assertTrue(exception.getMessage().contains("with non-zero balance"));
//        verify(bankAccountRepository, never()).delete(any());
//    }
//
//    @Test
//    void deleteAccountByAccountNumber_ShouldThrowException_WhenNotAuthorized() {
//        // Arrange
//        testAccount.setBalance(BigDecimal.ZERO);
//        User otherUser = new User();
//        otherUser.setId("other-user");
//        testAccount.setUsers(Set.of(otherUser));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
//        });
//
//        assertTrue(exception.getMessage().contains("is not authorized to delete account"));
//        verify(bankAccountRepository, never()).delete(any());
//    }
//
//    @Test
//    void updateAccountByAccountNumber_ShouldUpdateAccount_WhenUserIsOwner() {
//        // Arrange
//        String newName = "New Account Name";
//        UpdateBankAccountRequest updateRequest = new UpdateBankAccountRequest()
//            .name(newName);
//
//        testAccount.setUsers(Set.of(testUser));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation ->
//            invocation.getArgument(0)
//        );
//
//        // Act
//        ResponseEntity<BankAccountResponse> response = v1ApiDelegate.updateAccountByAccountNumber(
//            TEST_ACCOUNT_NUMBER, updateRequest);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(newName, response.getBody().getName());
//        assertEquals(TEST_ACCOUNT_NUMBER, response.getBody().getAccountNumber());
//        verify(bankAccountRepository).save(argThat(account ->
//            account.getName().equals(newName) &&
//            account.getAccountNumber().equals(TEST_ACCOUNT_NUMBER)
//        ));
//    }
//
//    @Test
//    void updateAccountByAccountNumber_ShouldThrowException_WhenAccountNotFound() {
//        // Arrange
//        UpdateBankAccountRequest updateRequest = new UpdateBankAccountRequest()
//            .name("New Name");
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.empty());
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.updateAccountByAccountNumber(TEST_ACCOUNT_NUMBER, updateRequest);
//        });
//
//        assertEquals("Bank account not found with account number: " + TEST_ACCOUNT_NUMBER,
//            exception.getMessage());
//        verify(bankAccountRepository, never()).save(any());
//    }
//
//    @Test
//    void updateAccountByAccountNumber_ShouldThrowException_WhenNotAuthorized() {
//        // Arrange
//        String newName = "New Account Name";
//        UpdateBankAccountRequest updateRequest = new UpdateBankAccountRequest()
//            .name(newName);
//
//        User otherUser = new User();
//        otherUser.setId("other-user");
//        testAccount.setUsers(Set.of(otherUser));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.updateAccountByAccountNumber(TEST_ACCOUNT_NUMBER, updateRequest);
//        });
//
//        assertTrue(exception.getMessage().contains("is not authorized to update account"));
//        verify(bankAccountRepository, never()).save(any());
//    }
//
//    @Test
//    void listAccounts_ShouldReturnUserAccounts() {
//        // Arrange
//        BankAccount account1 = new BankAccount();
//        account1.setAccountNumber("12345678");
//        account1.setName("Savings");
//        account1.setBalance(new BigDecimal("1000.00"));
//        account1.setSortCode("10-10-10");
//        account1.setCreatedTimestamp(OffsetDateTime.now());
//        account1.setUpdatedTimestamp(OffsetDateTime.now());
//
//        BankAccount account2 = new BankAccount();
//        account2.setAccountNumber("87654321");
//        account2.setName("Checking");
//        account2.setBalance(new BigDecimal("500.00"));
//        account2.setSortCode("10-10-10");
//        account2.setCreatedTimestamp(OffsetDateTime.now());
//        account2.setUpdatedTimestamp(OffsetDateTime.now());
//
//        List<BankAccount> accounts = List.of(account1, account2);
//        testUser.setBankAccounts(new HashSet<>(accounts));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findDistinctByUsersIn(anyList()))
//            .thenReturn(accounts);
//
//        // Act
//        ResponseEntity<ListBankAccountsResponse> response = v1ApiDelegate.listAccounts();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(2, response.getBody().getAccounts().size());
//
//        List<BankAccountResponse> accountResponses = response.getBody().getAccounts();
//        assertEquals("12345678", accountResponses.get(0).getAccountNumber());
//        assertEquals("Savings", accountResponses.get(0).getName());
//        assertEquals(1000.0, accountResponses.get(0).getBalance());
//
//        assertEquals("87654321", accountResponses.get(1).getAccountNumber());
//        assertEquals("Checking", accountResponses.get(1).getName());
//        assertEquals(500.0, accountResponses.get(1).getBalance());
//    }
//
//    @Test
//    void listAccounts_ShouldReturnEmptyList_WhenNoAccounts() {
//        // Arrange
//        testUser.setBankAccounts(new HashSet<>());
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findDistinctByUsersIn(anyList()))
//            .thenReturn(Collections.emptyList());
//
//        // Act
//        ResponseEntity<ListBankAccountsResponse> response = v1ApiDelegate.listAccounts();
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertTrue(response.getBody().getAccounts().isEmpty());
//    }
//
//    @Test
//    void listAccounts_ShouldThrowException_WhenUserNotFound() {
//        // Arrange
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("nonexistent");
//        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.listAccounts();
//        });
//
//        assertEquals("Username (nonexistent) not found", exception.getMessage());
//        verify(bankAccountRepository, never()).findDistinctByUsersIn(anyList());
//    }
//
//    @Test
//    void fetchUserByID_ShouldReturnUser_WhenAuthorized() {
//        // Arrange
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
//
//        // Act
//        ResponseEntity<UserResponse> response = v1ApiDelegate.fetchUserByID(TEST_USER_ID);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(TEST_USER_ID, response.getBody().getId());
//        assertEquals(TEST_USERNAME, response.getBody().getName());
//    }
//
//    @Test
//    void fetchUserByID_ShouldAllowAdminAccess_WhenNotSelf() {
//        // Arrange
//        String otherUserId = "other-user-123";
//        User otherUser = new User();
//        otherUser.setId(otherUserId);
//        otherUser.setUsername("otheruser");
//
//        User adminUser = new User();
//        adminUser.setId("admin-123");
//        adminUser.setUsername("admin");
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("admin");
//        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
//        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
//
//        // Mock isAdmin to return true for admin user
//        try (var mockedV1ApiDelegate = mockStatic(V1ApiDelegateImpl.class, CALLS_REAL_METHODS)) {
//            mockedV1ApiDelegate.when(() -> v1ApiDelegate.isAdmin(adminUser)).thenReturn(true);
//
//            // Act
//            ResponseEntity<UserResponse> response = v1ApiDelegate.fetchUserByID(otherUserId);
//
//            // Assert
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertNotNull(response.getBody());
//            assertEquals(otherUserId, response.getBody().getId());
//            assertEquals("otheruser", response.getBody().getName());
//        }
//    }
//
//    @Test
//    void fetchUserByID_ShouldThrowException_WhenUserNotFound() {
//        // Arrange
//        String nonExistentUserId = "nonexistent-user";
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.fetchUserByID(nonExistentUserId);
//        });
//
//        assertEquals("User " + nonExistentUserId + " not found", exception.getMessage());
//    }
//
//    @Test
//    void fetchUserByID_ShouldThrowException_WhenNotAuthorized() {
//        // Arrange
//        String otherUserId = "other-user-123";
//        User otherUser = new User();
//        otherUser.setId(otherUserId);
//        otherUser.setUsername("otheruser");
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(otherUser));
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.fetchUserByID(otherUserId);
//        });
//
//        assertTrue(exception.getMessage().contains("is not allowed to access user"));
//    }
//
//    @Test
//    void createUser_ShouldCreateNewUser_WithValidData() {
//        // Arrange
//        CreateUserRequest request = new CreateUserRequest()
//            .name("newuser")
//            .email("newuser@example.com")
//            .phoneNumber("+1234567890");
//
//        User newUser = new User();
//        newUser.setId("new-user-123");
//        newUser.setUsername("newuser");
//        newUser.setEmail("newuser@example.com");
//        newUser.setPhoneNumber("+1234567890");
//        newUser.setPassword("hashedpassword");
//
//        // Mock validation to pass
//        when(userRepository.existsByUsername("newuser")).thenReturn(false);
//        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
//        when(userRepository.existsByPhoneNumber("+1234567890")).thenReturn(false);
//        when(userRepository.save(any(User.class))).thenReturn(newUser);
//
//        // Act
//        ResponseEntity<UserResponse> response = v1ApiDelegate.createUser(request);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("new-user-123", response.getBody().getId());
//        assertEquals("newuser", response.getBody().getName());
//        assertEquals("newuser@example.com", response.getBody().getEmail());
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void createUser_ShouldThrowException_WhenUsernameExists() {
//        // Arrange
//        CreateUserRequest request = new CreateUserRequest()
//            .name("existinguser")
//            .email("newemail@example.com")
//            .phoneNumber("+1234567890");
//
//        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
//
//        // Act & Assert
//        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
//            v1ApiDelegate.createUser(request);
//        });
//
//        assertEquals("Username existinguser is already in use", exception.getMessage());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void createUser_ShouldThrowException_WhenEmailExists() {
//        // Arrange
//        CreateUserRequest request = new CreateUserRequest()
//            .name("newuser")
//            .email("existing@example.com")
//            .phoneNumber("+1234567890");
//
//        when(userRepository.existsByUsername("newuser")).thenReturn(false);
//        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
//
//        // Act & Assert
//        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
//            v1ApiDelegate.createUser(request);
//        });
//
//        assertEquals("Email existing@example.com is already in use", exception.getMessage());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void createUser_ShouldThrowException_WhenPhoneNumberExists() {
//        // Arrange
//        CreateUserRequest request = new CreateUserRequest()
//            .name("newuser")
//            .email("newuser@example.com")
//            .phoneNumber("+1987654321");
//
//        when(userRepository.existsByUsername("newuser")).thenReturn(false);
//        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
//        when(userRepository.existsByPhoneNumber("+1987654321")).thenReturn(true);
//
//        // Act & Assert
//        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
//            v1ApiDelegate.createUser(request);
//        });
//
//        assertEquals("Phone number +1987654321 is already in use", exception.getMessage());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void createAccount_ShouldCreateNewAccount_WithValidData() {
//        // Arrange
//        CreateBankAccountRequest request = new CreateBankAccountRequest()
//            .name("New Savings Account")
//            ;
//
//        BankAccount newAccount = new BankAccount();
//        newAccount.setAccountNumber("12345678");
//        newAccount.setName("New Savings Account");
//        newAccount.setSortCode("12-34-56");
//        newAccount.setBalance(BigDecimal.ZERO);
//        newAccount.setUsers(new HashSet<>(List.of(testUser)));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(newAccount);
//
//        // Act
//        ResponseEntity<BankAccountResponse> response = v1ApiDelegate.createAccount(request);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("New Savings Account", response.getBody().getName());
//        assertEquals("12-34-56", response.getBody().getSortCode());
//        assertEquals(0.0, response.getBody().getBalance());
//        verify(bankAccountRepository).save(argThat(account ->
//            account.getName().equals("New Savings Account") &&
//            account.getSortCode().equals("12-34-56") &&
//            account.getUsers().contains(testUser) &&
//            account.getBalance().equals(BigDecimal.ZERO)
//        ));
//    }
//
//    @Test
//    void createAccount_ShouldThrowException_WhenUserNotFound() {
//        // Arrange
//        CreateBankAccountRequest request = new CreateBankAccountRequest()
//            .name("New Account");
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("nonexistent");
//        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.createAccount(request);
//        });
//
//        assertEquals("Username (nonexistent) not found", exception.getMessage());
//        verify(bankAccountRepository, never()).save(any(BankAccount.class));
//    }
//
///*
//    @Test
//    void createAccount_ShouldSetDefaultName_WhenNameNotProvided() {
//        // Arrange
//        CreateBankAccountRequest request = new CreateBankAccountRequest();
//
//        BankAccount newAccount = new BankAccount();
//        newAccount.setAccountNumber("12345678");
//        newAccount.setName("Current Account"); // Default name
//        newAccount.setSortCode("12-34-56");
//
//        assertEquals("Current Account", response.getBody().getName());
//        verify(bankAccountRepository).save(argThat(account ->
//            account.getName() != null && !account.getName().trim().isEmpty()
//        ));
//    }
//*/
//
//    @Test
//    void createTransaction_ShouldCreateDeposit_WithValidData() {
//        // Arrange
//        CreateTransactionRequest request = new CreateTransactionRequest()
//
//            .type(CreateTransactionRequest.TypeEnum.DEPOSIT)
//            .amount(100.00);
//
//        testAccount.setBalance(new BigDecimal("500.00"));
//        testAccount.setUsers(Set.of(testUser));
//
//        Transaction newTransaction = new Transaction();
//        newTransaction.setId("txn-123");
//        newTransaction.setBankAccount(testAccount);
//        newTransaction.setType(TransactionType.DEPOSIT);
//        newTransaction.setAmount(new BigDecimal("100.00"));
//
//        newTransaction.setCreatedTimestamp(OffsetDateTime.now());
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(newTransaction);
//
//        // Act
//        ResponseEntity<TransactionResponse> response = v1ApiDelegate.createTransaction(TEST_ACCOUNT_NUMBER, request);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("txn-123", response.getBody().getId());
//
//        assertEquals(100.0, response.getBody().getAmount());
//        assertEquals("CREDIT", response.getBody().getType().getValue());
//
//        // Verify account balance was updated
//        verify(bankAccountRepository).save(argThat(account ->
//            account.getAccountNumber().equals(TEST_ACCOUNT_NUMBER) &&
//            account.getBalance().compareTo(new BigDecimal("600.00")) == 0
//        ));
//    }
//
//    @Test
//    void createTransaction_ShouldCreateWithdrawal_WithSufficientBalance() {
//        // Arrange
//        CreateTransactionRequest request = new CreateTransactionRequest()
//
//
//            .type(CreateTransactionRequest.TypeEnum.WITHDRAWAL)
//            .amount(200.00);
//
//        testAccount.setBalance(new BigDecimal("500.00"));
//        testAccount.setUsers(Set.of(testUser));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
//            Transaction t = invocation.getArgument(0);
//            t.setId("txn-124");
//            return t;
//        });
//
//        // Act
//        ResponseEntity<TransactionResponse> response = v1ApiDelegate.createTransaction(TEST_ACCOUNT_NUMBER, request);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("txn-124", response.getBody().getId());
//
//        assertEquals(200.0, response.getBody().getAmount());
//        assertEquals("DEBIT", response.getBody().getType().getValue());
//
//        // Verify account balance was updated
//        verify(bankAccountRepository).save(argThat(account ->
//            account.getAccountNumber().equals(TEST_ACCOUNT_NUMBER) &&
//            account.getBalance().compareTo(new BigDecimal("300.00")) == 0
//        ));
//    }
//
//    @Test
//    void createTransaction_ShouldThrowException_WhenInsufficientBalance() {
//        // Arrange
//        CreateTransactionRequest request = new CreateTransactionRequest()
//
//            .type(CreateTransactionRequest.TypeEnum.WITHDRAWAL)
//            .amount(600.00);
//
//        testAccount.setBalance(new BigDecimal("500.00"));
//        testAccount.setUsers(Set.of(testUser));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.createTransaction(TEST_ACCOUNT_NUMBER, request);
//        });
//
//        assertEquals("Insufficient funds for this transaction", exception.getMessage());
//        verify(bankAccountRepository, never()).save(any(BankAccount.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//
//    @Test
//    void createTransaction_ShouldThrowException_WhenNotAuthorized() {
//        // Arrange
//        CreateTransactionRequest request = new CreateTransactionRequest()
//
//            .type(CreateTransactionRequest.TypeEnum.DEPOSIT)
//            .amount(100.00);
//
//        User otherUser = new User();
//        otherUser.setId("other-user");
//        testAccount.setUsers(Set.of(otherUser));
//
//        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
//        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
//        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
//            .thenReturn(Optional.of(testAccount));
//
//        // Act & Assert
//        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
//            v1ApiDelegate.createTransaction(TEST_ACCOUNT_NUMBER,request);
//        });
//
//        assertTrue(exception.getMessage().contains("is not authorized to access account"));
//        verify(bankAccountRepository, never()).save(any(BankAccount.class));
//        verify(transactionRepository, never()).save(any(Transaction.class));
//    }
//}
