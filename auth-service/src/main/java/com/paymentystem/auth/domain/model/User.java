package com.paymentystem.auth.domain.model;

import com.paymentystem.shared.domain.exception.DomainException;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private String passwordHash;
    private String fullName;
    private Set<Role> roles;
    private UserStatus status;
    private int failedLoginAttempts;
    private Instant lastLoginAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(String id, String email, String passwordHash,
                 String fullName, Set<Role> roles, UserStatus status,
                 int failedLoginAttempts, Instant lastLoginAt,
                 Instant createdAt, Instant updatedAt) {
        this.id                   = id;
        this.email                = email;
        this.passwordHash         = passwordHash;
        this.fullName             = fullName;
        this.roles                = roles;
        this.status               = status;
        this.failedLoginAttempts  = failedLoginAttempts;
        this.lastLoginAt          = lastLoginAt;
        this.createdAt            = createdAt;
        this.updatedAt            = updatedAt;
    }

    // Factory method — única forma de crear un User nuevo
    public static User create(String email, String passwordHash, String fullName) {
        return new User(
                UUID.randomUUID().toString(),
                email,
                passwordHash,
                fullName,
                Set.of(Role.USER),
                UserStatus.ACTIVE,
                0,
                null,
                Instant.now(),
                Instant.now()
        );
    }

    // Reconstruct — para cuando JPA recupera el usuario de la BD
    public static User reconstruct(String id, String email, String passwordHash,
                                   String fullName, Set<Role> roles, UserStatus status,
                                   int failedLoginAttempts, Instant lastLoginAt,
                                   Instant createdAt, Instant updatedAt) {
        return new User(id, email, passwordHash, fullName, roles, status,
                failedLoginAttempts, lastLoginAt, createdAt, updatedAt);
    }

    // ── Reglas de negocio ──────────────────────────────────

    public void validateCanLogin() {
        if (status == UserStatus.BLOCKED) {
            throw new DomainException("AUTH_BLOCKED",
                    "Usuario bloqueado por demasiados intentos fallidos");
        }
        if (status == UserStatus.INACTIVE) {
            throw new DomainException("AUTH_INACTIVE",
                    "Cuenta inactiva — verifica tu email");
        }
        if (status == UserStatus.SUSPENDED) {
            throw new DomainException("AUTH_SUSPENDED",
                    "Cuenta suspendida — contacta a soporte");
        }
    }

    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        this.updatedAt = Instant.now();
        if (this.failedLoginAttempts >= 5) {
            this.status = UserStatus.BLOCKED;
        }
    }

    public void recordSuccessfulLogin() {
        this.failedLoginAttempts = 0;
        this.lastLoginAt         = Instant.now();
        this.updatedAt           = Instant.now();
    }

    public void unblock() {
        if (this.status != UserStatus.BLOCKED) {
            throw new DomainException("AUTH_NOT_BLOCKED", "El usuario no está bloqueado");
        }
        this.status              = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.updatedAt           = Instant.now();
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ── Getters ───────────────────────────────────────────

    public String getId()                  { return id; }
    public String getEmail()               { return email; }
    public String getPasswordHash()        { return passwordHash; }
    public String getFullName()            { return fullName; }
    public Set<Role> getRoles()            { return roles; }
    public UserStatus getStatus()          { return status; }
    public int getFailedLoginAttempts()    { return failedLoginAttempts; }
    public Instant getLastLoginAt()        { return lastLoginAt; }
    public Instant getCreatedAt()          { return createdAt; }
    public Instant getUpdatedAt()          { return updatedAt; }
}