package com.brclys.thct.repository;

import com.brclys.thct.entity.BankAccount;
import com.brclys.thct.entity.User;
import jakarta.transaction.Transactional;
import lombok.Synchronized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    // Custom query method to find account by account number and sort code
    BankAccount findByAccountNumberAndSortCode(String accountNumber, String sortCode);
    List<BankAccount> findDistinctByUsersIn(List<String> users);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    Optional<BankAccount> findByAccountNumberAndUsersIn(String accountNumber, List<User> user);
}