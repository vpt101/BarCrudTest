package com.brclys.thct.repository;


import com.brclys.thct.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brclys.thct.entity.BankAccount;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByBankAccountOrderByCreatedTimestampDesc(BankAccount bankAccount);
}
