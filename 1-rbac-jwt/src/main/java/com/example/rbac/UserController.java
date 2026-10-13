package com.example.rbac;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

// Role checks for these endpoints live in SecurityConfig, so this class only holds business logic
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    public record ProfileUpdate(@Email String email) {}

    // Any logged-in role (ADMIN, USER, GUEST)
    @GetMapping("/me")
    public User me(Principal principal) {
        return find(principal.getName()); // principal name = the token's subject (username)
    }

    // ADMIN or USER: update your own profile
    @PutMapping("/me")
    public User updateMe(Principal principal, @Valid @RequestBody ProfileUpdate body) {
        User user = find(principal.getName());
        user.setEmail(body.email());
        return users.save(user);
    }

    // ADMIN only
    @GetMapping
    public List<User> all() {
        return users.findAll();
    }

    // ADMIN only
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!users.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        users.deleteById(id);
    }

    private User find(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
