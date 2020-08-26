package com.bcorp.jwtdemo.security.jwt;

import com.bcorp.jwtdemo.config.ApplicationProperties;
import com.bcorp.jwtdemo.security.AuthoritiesConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JWTFilterTest {
    private TokenProvider tokenProvider;

    private JWTFilter jwtFilter;

    @BeforeEach
    public void setup() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        ApplicationProperties.Security.Authentication.JWT jwt = applicationProperties.getSecurity().getAuthentication().getJwt();
        jwt.setBase64secret("v+HzbA9XASouMa83/6rUHlSEMfzs0o+VuuOVQI+7THzNkft5Y+e07WdRl5LPyB3EOJDF+dv7wj6rXxHZu9zHug==");
        jwt.setTokenValidityInSeconds(60000);
        tokenProvider = new TokenProvider(applicationProperties);
        tokenProvider.init(); // called by @PostConstruct in normal circumstances
        jwtFilter = new JWTFilter(tokenProvider);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void shouldAuthenticateCorrectly() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "test-user",
                "test-password",
                Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER))
        );
        String jwt = tokenProvider.createToken(authentication);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, JWTFilter.AUTHORIZATION_SCHEME + jwt);
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("test-user");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString()).isEqualTo(jwt);
    }

    @Test
    public void shouldNotAuthenticateAnInvalidToken() throws Exception {
        String jwt = "useless-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JWTFilter.AUTHORIZATION_HEADER, JWTFilter.AUTHORIZATION_SCHEME + jwt);
        request.setRequestURI("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        jwtFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // More tests without token, with invalid scheme, without auth header, etc...
}