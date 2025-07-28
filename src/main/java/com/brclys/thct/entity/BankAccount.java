package com.brclys.thct.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "bank_accounts")
@AllArgsConstructor
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 8)
    private String accountNumber;

    @Column(nullable = false, length = 8)
    @Pattern(regexp = "^[0-9]-[0-9]-[0-9]$")
    private String sortCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @ManyToMany(mappedBy = "bankAccounts")
    private Set<User> users;

    @Column(nullable = false)
    private OffsetDateTime createdTimestamp;
}