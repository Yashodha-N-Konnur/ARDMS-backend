package com.ardms.service.impl;

import com.ardms.dto.request.LoginRequest;
import com.ardms.dto.request.RegisterRequest;
import com.ardms.dto.response.AuthResponse;
import com.ardms.entity.RefreshToken;
import com.ardms.entity.Role;
import com.ardms.entity.User;
import com.ardms.exception.InvalidOperationException;
import com.ardms.exception.ResourceAlreadyExistsException;
import com.ardms.exception.ResourceNotFoundException;
import com.ardms.repository.RefreshTokenRepository;
import com.ardms.repository.RoleRepository;
import com.ardms.repository.UserRepository;
import com.ardms.security.jwt.JwtTokenProvider;
import com.ardms.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger("com.ardms.security");

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for: {}", request.getUsernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String username = authentication.getName();

        User user = userRepository.findByUsernameOrEmail(username, username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Update last login time
        userRepository.updateLastLoginTime(user.getId(), LocalDateTime.now());

        // Revoke old refresh tokens and create new one
        refreshTokenRepository.revokeAllUserTokens(user.getId());
        String refreshToken = createRefreshToken(user);

        Set<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        logger.info("Login successful for user: {}", username);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(roles)
            .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registration attempt for username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }

        Set<Role> roles = resolveRoles(request.getRoles());

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .isActive(true)
            .isLocked(false)
            .roles(roles)
            .createdBy("SELF_REGISTRATION")
            .build();

        user = userRepository.save(user);
        logger.info("New user registered: {}", user.getUsername());

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = createRefreshToken(user);

        Set<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(roleNames)
            .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new InvalidOperationException("Invalid refresh token"));

        if (storedToken.getIsRevoked()) {
            throw new InvalidOperationException("Refresh token has been revoked");
        }
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("Refresh token has expired");
        }

        User user = storedToken.getUser();
        List<String> roleNames = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());

        String newAccessToken = jwtTokenProvider.generateTokenFromUsername(
            user.getUsername(), roleNames, user.getId()
        );

        // Rotate refresh token
        storedToken.setIsRevoked(true);
        refreshTokenRepository.save(storedToken);
        String newRefreshToken = createRefreshToken(user);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(roleNames.stream().collect(Collectors.toSet()))
            .build();
    }

    @Override
    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            refreshTokenRepository.revokeAllUserTokens(user.getId());
            logger.info("User logged out: {}", username);
        });
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
            .isRevoked(false)
            .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_DEVELOPER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_DEVELOPER"));
            roles.add(defaultRole);
        } else {
            roleNames.forEach(roleName -> {
                String formattedName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
                Role role = roleRepository.findByName(formattedName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", formattedName));
                roles.add(role);
            });
        }
        return roles;
    }
}
