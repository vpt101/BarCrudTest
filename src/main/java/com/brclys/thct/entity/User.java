package com.brclys.thct.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    // @Pattern(regexp = "^usr-[A-Za-z0-9\\-]$")
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @Column
    private String phoneNumber;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_bank_accounts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_account_id")
    )
    private Set<BankAccount> bankAccounts;

    @CreationTimestamp
    @Column
    private OffsetDateTime createdTimestamp;

    @UpdateTimestamp
    @Column
    private OffsetDateTime updatedTimestamp;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        if (this.createdTimestamp == null) { this.createdTimestamp =  OffsetDateTime.now(); }
        if (this.updatedTimestamp == null) { this.updatedTimestamp =  OffsetDateTime.now(); }
        if (this.id == null) {
            this.id = "usr-" + java.util.UUID.randomUUID().toString().replaceAll("-", "");
        }
    }

    public void removeBankAccount(BankAccount bankAccount) {
        this.getBankAccounts().remove(bankAccount);
        bankAccount.getUsers().remove(this);
    }

}