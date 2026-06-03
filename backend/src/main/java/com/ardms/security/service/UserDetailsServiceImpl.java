package com.ardms.security.service;

import com.ardms.entity.User;
import com.ardms.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(() -> {
                logger.warn("User not found with username or email: {}", usernameOrEmail);
                return new UsernameNotFoundException("User not found: " + usernameOrEmail);
            });

        if (!user.getIsActive()) {
            logger.warn("Attempt to login with inactive account: {}", usernameOrEmail);
            throw new UsernameNotFoundException("Account is deactivated: " + usernameOrEmail);
        }

        Collection<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .accountLocked(user.getIsLocked())
            .accountExpired(false)
            .credentialsExpired(false)
            .disabled(!user.getIsActive())
            .build();
    }
}
