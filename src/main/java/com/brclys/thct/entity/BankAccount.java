package com.brclys.thct.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

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
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @ManyToMany(mappedBy = "bankAccounts")
    // @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    private Set<User> users;

    @Column(nullable = false)
    private OffsetDateTime createdTimestamp;

    @Column(nullable = false)
    private OffsetDateTime updatedTimestamp;

}