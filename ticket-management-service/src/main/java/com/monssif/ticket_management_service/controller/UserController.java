package com.monssif.ticket_management_service.controller;

import com.monssif.ticket_management_service.dto.UserRequestDTO;
import com.monssif.ticket_management_service.dto.UserResponseDTO;
import com.monssif.ticket_management_service.dto.UserSummaryDTO;
import com.monssif.ticket_management_service.dto.UserUpdateDTO;
import com.monssif.ticket_management_service.enums.UserRole;
import com.monssif.ticket_management_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("REST request to create user: {}", requestDTO.getUsername());
        UserResponseDTO createdUser = userService.createUser(requestDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by ID: {}", id);
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<Page<UserSummaryDTO>> getAllUsers(
            @RequestParam(defaultValue = "false") boolean activeOnly,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get all users - activeOnly: {}", activeOnly);

        Page<UserSummaryDTO> users = activeOnly
                ? userService.getActiveUsers(pageable)
                : userService.getAllUsers(pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserSummaryDTO>> getUsersByRole(@PathVariable UserRole role) {
        log.info("REST request to get users by role: {}", role);
        List<UserSummaryDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/agents")
    public ResponseEntity<List<UserSummaryDTO>> getActiveAgents() {
        log.info("REST request to get all active agents");
        List<UserSummaryDTO> agents = userService.getActiveAgents();
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        log.info("REST request to get user by username: {}", username);
        UserResponseDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("REST request to get user by email: {}", email);
        UserResponseDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("REST request to update user with ID: {}", id);
        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete (deactivate) user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable Long id) {
        log.info("REST request to activate user with ID: {}", id);
        UserResponseDTO activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(activatedUser);
    }
}