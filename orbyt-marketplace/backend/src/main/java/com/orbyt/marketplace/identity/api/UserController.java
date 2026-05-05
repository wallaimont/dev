package com.orbyt.marketplace.identity.api;

import com.orbyt.marketplace.identity.api.dto.UpsertUserRequest;
import com.orbyt.marketplace.identity.api.dto.UserResponse;
import com.orbyt.marketplace.identity.application.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('users.manage')")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('users.manage')")
    public UserResponse findById(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('users.manage')")
    public UserResponse create(@Valid @RequestBody UpsertUserRequest request) {
        return userService.create(request);
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasAuthority('users.manage')")
    public UserResponse update(@PathVariable UUID userId, @Valid @RequestBody UpsertUserRequest request) {
        return userService.update(userId, request);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('users.manage')")
    public void delete(@PathVariable UUID userId) {
        userService.delete(userId);
    }
}
