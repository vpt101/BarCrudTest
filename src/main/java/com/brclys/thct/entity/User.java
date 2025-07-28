package com.brclys.thct.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @Column// (nullable = false)
    private String phoneNumber;

    @ManyToMany
    @JoinTable(
            name = "user_bank_account",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_account_id")
    )
    private Set<BankAccount> bankAccounts;

    @CreationTimestamp
    @Column// (nullable = false)
    private OffsetDateTime createdTimestamp;

    @UpdateTimestamp
    @Column// (nullable = false)
    private OffsetDateTime updatedTimestamp;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        if (this.createdTimestamp == null) { this.createdTimestamp =  OffsetDateTime.now(); }
        if (this.updatedTimestamp == null) { this.updatedTimestamp =  OffsetDateTime.now(); }
    }

}