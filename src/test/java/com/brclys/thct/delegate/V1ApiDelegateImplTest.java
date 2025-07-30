package com.brclys.thct.delegate;

import com.brclys.thct.entity.*;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    void deleteAccountByAccountNumber_ShouldDeleteAccount() {
        // Arrange
        testAccount.setBalance(BigDecimal.ZERO);
        when(bankAccountRepository.findByAccountNumber(TEST_ACCOUNT_NUMBER))
            .thenReturn(Optional.of(testAccount));

        // Act
        ResponseEntity<Void> response = v1ApiDelegate.deleteAccountByAccountNumber(TEST_ACCOUNT_NUMBER);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bankAccountRepository).delete(testAccount);
    }
}
