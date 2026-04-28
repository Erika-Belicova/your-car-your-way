package com.yourcaryourway.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the system.
 * Contains user credentials, additional information, external authentication information,
 * acceptance of the given terms, and associated support conversations.
 * Tracks creation and update timestamps automatically.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "language", nullable = false)
    private String language = "en";

    @Column(name = "auth_provider")
    private String authProvider;

    @Column(name = "external_auth_id")
    private String externalAuthId;

    @Column(name = "terms_accepted_at", nullable = false)
    private OffsetDateTime termsAcceptedAt;

    @Column(name = "privacy_accepted_at", nullable = false)
    private OffsetDateTime privacyAcceptedAt;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "support_access", nullable = false)
    private Boolean supportAccess = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // support conversations belonging to this user
    private List<SupportConversation> supportConversations = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}