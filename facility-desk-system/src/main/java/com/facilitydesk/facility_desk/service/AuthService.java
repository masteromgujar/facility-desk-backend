package com.facilitydesk.facility_desk.service;

import com.facilitydesk.facility_desk.dto.AuthDto;
import com.facilitydesk.facility_desk.exception.BadRequestException;
import com.facilitydesk.facility_desk.model.Role;
import com.facilitydesk.facility_desk.model.User;
import com.facilitydesk.facility_desk.repository.RoleRepository;
import com.facilitydesk.facility_desk.repository.UserRepository;
import com.facilitydesk.facility_desk.security.JwtUtils;
import com.facilitydesk.facility_desk.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthDto.JwtResponse authenticateUser(AuthDto.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        log.info("User {} logged in successfully", userDetails.getUsername());

        return new AuthDto.JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);
    }

    @Transactional
    public AuthDto.MessageResponse registerUser(AuthDto.RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = resolveRoles(strRoles);
        user.setRoles(roles);
        userRepository.save(user);

        log.info("User {} registered successfully", user.getUsername());
        return new AuthDto.MessageResponse("User registered successfully!");
    }

    private Set<Role> resolveRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(findRole(Role.RoleName.ROLE_CUSTOMER));
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin" -> roles.add(findRole(Role.RoleName.ROLE_ADMIN));
                    case "vendor" -> roles.add(findRole(Role.RoleName.ROLE_VENDOR));
                    case "employee" -> roles.add(findRole(Role.RoleName.ROLE_EMPLOYEE));
                    default -> roles.add(findRole(Role.RoleName.ROLE_CUSTOMER));
                }
            });
        }
        return roles;
    }

    private Role findRole(Role.RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }
}
