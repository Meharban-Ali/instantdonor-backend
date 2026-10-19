package com.blood_donor.security; // ✅ FIX 1

import com.blood_donor.repository.user_repository.UserRepository; // ✅ FIX 1
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + email
                ));

        // ✅ FIX 2: isActive() — primitive boolean
        // ✅ FIX 3: Set<Role> — sab roles map karo
        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),        // ✅ FIX 2
                true,
                true,
                true,
                user.getRoles().stream() // ✅ FIX 3
                        .map(role -> new SimpleGrantedAuthority(
                                "ROLE_" + role.name()
                        ))
                        .toList()
        );
    }
}