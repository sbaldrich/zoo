package com.bcorp.jwtdemo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DomainUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    public DomainUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private static class ApplicationUser {
        private String username;
        private List<String> authorities;

        public ApplicationUser(String username, List<String> authorities) {
            this.username = username;
            this.authorities = authorities;
        }
    }

    private static final Map<String, ApplicationUser> userRepository;

    static {
        userRepository = new HashMap<>();
        userRepository.put("user", new ApplicationUser("user",  Collections.singletonList(AuthoritiesConstants.USER)));
        userRepository.put("admin", new ApplicationUser("admin", Arrays.asList(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.get(username))
                .map(this::asUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found in the database"));
    }

    private User asUserDetails(ApplicationUser applicationUser){
        List<GrantedAuthority> grantedAuthorities = applicationUser.authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new User(applicationUser.username, passwordEncoder.encode(applicationUser.username), grantedAuthorities);
    }
}
