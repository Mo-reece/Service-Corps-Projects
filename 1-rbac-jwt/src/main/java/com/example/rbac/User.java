package com.example.rbac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public enum Role { ADMIN, USER, GUEST }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore // never send the password hash back in a response
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // stored as 'ADMIN' / 'USER' / 'GUEST'
    @Column(nullable = false)
    private Role role;

    private String email;

    protected User() {} // required by JPA

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
