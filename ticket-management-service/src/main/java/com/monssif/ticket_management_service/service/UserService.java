package com.monssif.ticket_management_service.service;

import com.monssif.ticket_management_service.dto.UserRequestDTO;
import com.monssif.ticket_management_service.dto.UserResponseDTO;
import com.monssif.ticket_management_service.dto.UserSummaryDTO;
import com.monssif.ticket_management_service.dto.UserUpdateDTO;
import com.monssif.ticket_management_service.entity.User;
import com.monssif.ticket_management_service.enums.UserRole;
import com.monssif.ticket_management_service.exception.InvalidTicketOperationException;
import com.monssif.ticket_management_service.mapper.UserMapper;
import com.monssif.ticket_management_service.repository.CommentRepository;
import com.monssif.ticket_management_service.repository.TicketRepository;
import com.monssif.ticket_management_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        log.info("Creating new user with username: {}", requestDTO.getUsername());

        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new InvalidTicketOperationException(
                    "Username already exists: " + requestDTO.getUsername());
        }

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new InvalidTicketOperationException(
                    "Email already exists: " + requestDTO.getEmail());
        }

        User user = User.builder()
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .passwordHash(requestDTO.getPassword())
                .fullName(requestDTO.getFullName())
                .role(requestDTO.getRole())
                .isActive(requestDTO.getIsActive())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return enrichUserResponse(savedUser);
    }


    public UserResponseDTO getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with ID: " + userId));

        return enrichUserResponse(user);
    }


    public Page<UserSummaryDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userMapper::toSummaryDTO);
    }

    public Page<UserSummaryDTO> getActiveUsers(Pageable pageable) {
        log.info("Fetching active users");

        Page<User> usersPage = userRepository.findAllActive(pageable);
        return usersPage.map(userMapper::toSummaryDTO);
    }

    public List<UserSummaryDTO> getUsersByRole(UserRole role) {
        log.info("Fetching users with role: {}", role);

        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public List<UserSummaryDTO> getActiveAgents() {
        log.info("Fetching all active agents");

        List<User> agents = userRepository.findAllActiveAgents();
        return agents.stream()
                .map(userMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateDTO) {
        log.info("Updating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with ID: " + userId));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new InvalidTicketOperationException(
                        "Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isBlank()) {
            user.setPasswordHash(updateDTO.getPassword());
        }

        if (updateDTO.getFullName() != null && !updateDTO.getFullName().isBlank()) {
            user.setFullName(updateDTO.getFullName());
        }

        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }

        if (updateDTO.getIsActive() != null) {
            user.setIsActive(updateDTO.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", userId);

        return enrichUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully with ID: {}", userId);
    }

    @Transactional
    public UserResponseDTO activateUser(Long userId) {
        log.info("Activating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with ID: " + userId));

        user.setIsActive(true);
        User activatedUser = userRepository.save(user);

        log.info("User activated successfully with ID: {}", userId);

        return enrichUserResponse(activatedUser);
    }

    public UserResponseDTO getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with username: " + username));

        return enrichUserResponse(user);
    }

    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with email: " + email));

        return enrichUserResponse(user);
    }


    private UserResponseDTO enrichUserResponse(User user) {
        UserResponseDTO dto = userMapper.toResponseDTO(user);

        if (user.isAgent() || user.isAdmin()) {
            dto.setAssignedTicketsCount(ticketRepository.countByAssignedAgentId(user.getId()));
        } else {
            dto.setAssignedTicketsCount(0L);
        }

        Long createdTickets = ticketRepository.findByCustomer(user, Pageable.unpaged()).getTotalElements();
        dto.setCreatedTicketsCount(createdTickets);

        Long commentsCount = commentRepository.findByUserId(user.getId(), Pageable.unpaged()).getTotalElements();
        dto.setCommentsCount(commentsCount);

        return dto;
    }
}