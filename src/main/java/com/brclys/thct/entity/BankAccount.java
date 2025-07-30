package com.brclys.thct.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "bank_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 8)
    private String accountNumber;

    @Column(nullable = false, length = 8)
    private String sortCode;

    @Column(nullable = false, length = 50)
    private String name; // Name of the branch??? What is this?

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @ManyToMany(mappedBy = "bankAccounts")
    private Set<User> users;

    @Column(nullable = false)
    private OffsetDateTime createdTimestamp;

    @Column(nullable = false)
    private OffsetDateTime updatedTimestamp;
}