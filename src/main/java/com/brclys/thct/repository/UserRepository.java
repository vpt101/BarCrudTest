package com.brclys.thct.repository;

import com.brclys.thct.entity.BankAccount;
import com.brclys.thct.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(@NotNull String name);
    Set<User> findByBankAccountsIn(Set<BankAccount> bankAccounts);
}
