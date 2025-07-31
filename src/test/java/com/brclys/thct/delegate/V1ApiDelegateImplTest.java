package com.brclys.thct.delegate;

import com.brclys.thct.delegate.exception.BrclysApiException;
import com.brclys.thct.entity.*;
import com.brclys.thct.openApiGenSrc.model.*;
import com.brclys.thct.repository.BankAccountRepository;
import com.brclys.thct.repository.TransactionRepository;
import com.brclys.thct.repository.UserRepository;
import com.brclys.thct.security.JwtUtil;
import org.junit.Ignore;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private NativeWebRequest nativeWebRequest;


    @InjectMocks
    private V1ApiDelegateImpl v1ApiDelegate;

    private User testUser;
    private BankAccount testAccount;


    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setBankAccounts(new HashSet<>());

        testAccount = new BankAccount();
        testAccount.setAccountNumber(TEST_ACCOUNT_NUMBER);
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setUsers(Set.of(testUser));

        testUser.getBankAccounts().add(testAccount);

        when(jwtUtil.getUsernameFromToken(any())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(nativeWebRequest.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
    }

    @Test
    void deleteUserByID_ShouldDeleteUser() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Void> response = v1ApiDelegate.deleteUserByID(TEST_USER_ID);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).delete(testUser);
    }

    @Test
    void createTransaction_ShouldCreateDeposit() {
        // Arrange
        CreateTransactionRequest request = new CreateTransactionRequest()
            .amount(100.0)
            .currency(CreateTransactionRequest.CurrencyEnum.GBP)
            .type(CreateTransactionRequest.TypeEnum.DEPOSIT)
            .reference("Test deposit");

        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));
        when(bankAccountRepository.findByAccountNumberAndUsersIn(TEST_ACCOUNT_NUMBER, List.of(testUser)))
                .thenReturn(Optional.of(testAccount));
        var mockedTransaction =Mockito.mock(Transaction.class);
        mockedTransaction.setId("txn-123");
        mockedTransaction.setAmount(new BigDecimal("100.00"));
        mockedTransaction.setType(TransactionType.DEPOSIT);
        mockedTransaction.setCurrency("GBP");
        mockedTransaction.setCreatedTimestamp(OffsetDateTime.now());
        mockedTransaction.setUser(testUser);
        mockedTransaction.setBankAccount(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockedTransaction);

        // Act
        ResponseEntity<TransactionResponse> response = v1ApiDelegate.createTransaction(
            TEST_ACCOUNT_NUMBER, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals("DEPOSIT", response.getBody().getType().getValue());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void listAccountTransaction_ShouldReturnTransactions() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId("txn-123");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCurrency("GBP");
        transaction.setCreatedTimestamp(OffsetDateTime.now());
        transaction.setUser(testUser);
        transaction.setBankAccount(testAccount);

        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
            .thenReturn(List.of(transaction));

        // Act
        ResponseEntity<ListTransactionsResponse> response = 
            v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTransactions().size());
        assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
    }

    @Test
    void listAccountTransaction_ShouldReturnTransactions_WhenUserIsOwner() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId("txn-123");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCurrency("GBP");
        transaction.setUser(testUser);
        transaction.setBankAccount(testAccount);
        transaction.setCreatedTimestamp(OffsetDateTime.now());

        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.findByAccountNumberAndUsersIn(eq(TEST_ACCOUNT_NUMBER), anyList()))
            .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
            .thenReturn(List.of(transaction));

        // Act
        ResponseEntity<ListTransactionsResponse> response = v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTransactions().size());
        assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
        verify(transactionRepository).findByBankAccountOrderByCreatedTimestampDesc(testAccount);
    }

    @Test
    void listAccountTransaction_ShouldThrowException_WhenAccountNotFound() {
        // Arrange
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.findByAccountNumberAndUsersIn(eq(TEST_ACCOUNT_NUMBER), anyList()))
            .thenReturn(Optional.empty());

        // Act & Assert
        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
            v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);
        });

        assertEquals("Account " + TEST_ACCOUNT_NUMBER + " not found", exception.getMessage());
        verify(transactionRepository, never()).findByBankAccountOrderByCreatedTimestampDesc(any());
    }

    @Test
    void listAccountTransaction_ShouldAllowAdminAccess_WhenNotOwner() {
        // Arrange
        User adminUser = new User();
        adminUser.setId("admin-123");
        adminUser.setUsername("admin");
        
        Transaction transaction = new Transaction();
        transaction.setId("txn-123");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCurrency("GBP");
        transaction.setUser(testUser);
        transaction.setBankAccount(testAccount);
        transaction.setCreatedTimestamp(OffsetDateTime.now());

        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        
        // First call (non-admin path) - should return empty
        when(bankAccountRepository.findByAccountNumberAndUsersIn(eq(TEST_ACCOUNT_NUMBER), anyList()))
            .thenReturn(Optional.empty());
            
        // Second call (admin path) - should return the account
        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));
            
        when(transactionRepository.findByBankAccountOrderByCreatedTimestampDesc(testAccount))
            .thenReturn(List.of(transaction));

        // Mock isAdmin to return true for admin user
        try (var mockedV1ApiDelegate = mockStatic(V1ApiDelegateImpl.class, CALLS_REAL_METHODS)) {
            mockedV1ApiDelegate.when(() -> v1ApiDelegate.isAdmin(adminUser)).thenReturn(true);

            // Act
            ResponseEntity<ListTransactionsResponse> response = v1ApiDelegate.listAccountTransaction(TEST_ACCOUNT_NUMBER);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getTransactions().size());
            assertEquals("txn-123", response.getBody().getTransactions().get(0).getId());
        }
    }

    @Test
    void deleteAccountByAccountNumber_ShouldDeleteAccount_WhenBalanceIsZero() {
        // Arrange
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setUsers(Set.of(testUser));
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));

        // Act
        ResponseEntity<Void> response = v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bankAccountRepository).delete(testAccount);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteAccountByAccountNumber_ShouldThrowException_WhenAccountNotFound() {
        // Arrange
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.empty());

        // Act & Assert
        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
        });
        
        assertEquals("Bank account not found with account number: " + TEST_ACCOUNT_NUMBER, exception.getMessage());
        verify(bankAccountRepository, never()).delete(any());
    }

    @Test
    void deleteAccountByAccountNumber_ShouldThrowException_WhenBalanceNotZero() {
        // Arrange
        testAccount.setBalance(new BigDecimal("100.00"));
        testAccount.setUsers(Set.of(testUser));
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
        });
        
        assertTrue(exception.getMessage().contains("Cannot delete account"));
        assertTrue(exception.getMessage().contains("with non-zero balance"));
        verify(bankAccountRepository, never()).delete(any());
    }

    @Test
    void deleteAccountByAccountNumber_ShouldThrowException_WhenNotAuthorized() {
        // Arrange
        testAccount.setBalance(BigDecimal.ZERO);
        User otherUser = new User();
        otherUser.setId("other-user");
        testAccount.setUsers(Set.of(otherUser));
        
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));

        // Act & Assert
        BrclysApiException exception = assertThrows(BrclysApiException.class, () -> {
            v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);
        });
        
        assertTrue(exception.getMessage().contains("is not authorized to delete account"));
        verify(bankAccountRepository, never()).delete(any());
    }
}
