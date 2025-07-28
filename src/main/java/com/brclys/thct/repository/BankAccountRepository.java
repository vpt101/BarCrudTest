package com.brclys.thct.repository;

import com.brclys.thct.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    // Custom query method to find account by account number and sort code
    BankAccount findByAccountNumberAndSortCode(String accountNumber, String sortCode);
}