package com.bcorp.jwtdemo.security.jwt;

import com.bcorp.jwtdemo.config.ApplicationProperties;
import com.bcorp.jwtdemo.security.AuthoritiesConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenProviderTest {

    private static final long ONE_MINUTE = 60000;

    private Key key;
    private TokenProvider tokenProvider;

    @BeforeEach
    public void setup() {
        tokenProvider = new TokenProvider( new ApplicationProperties());
        key = Keys.hmacShaKeyFor(Decoders.BASE64
            .decode("23qQVxtx1o6TNzHLOkYS4mp45vvc7gn/Gvc+Nuvd/blQoAA4JD3+dgvJ2XGbsiE4FIEX5Uxp30K7NBTJKoAXpg=="));

        ReflectionTestUtils.setField(tokenProvider, "key", key);
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", ONE_MINUTE);
    }

    @Test
    public void shouldConsiderValidAValidToken() {
        String jwt = tokenProvider.createToken(createAuthentication());
        boolean isTokenValid = tokenProvider.validateToken(jwt);
        assertThat(isTokenValid).isTrue();
    }

    @Test
    public void shouldConsiderInvalidATokenWithDifferentSignature() {
        boolean isTokenValid = tokenProvider.validateToken(createTokenWithDifferentSignature());

        assertThat(isTokenValid).isFalse();
    }

    @Test
    public void shouldConsiderInvalidAMalformedToken() {
        Authentication authentication = createAuthentication();
        String token = tokenProvider.createToken(authentication);
        String invalidToken = token.substring(5);
        boolean isTokenValid = tokenProvider.validateToken(invalidToken);

        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void shouldConsiderInvalidAnExpiredToken() {
        ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -ONE_MINUTE);

        Authentication authentication = createAuthentication();
        String token = tokenProvider.createToken(authentication);

        boolean isTokenValid = tokenProvider.validateToken(token);

        assertThat(isTokenValid).isEqualTo(false);
    }

    private Authentication createAuthentication() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
    }

    private String createTokenWithDifferentSignature() {
        Key otherKey = Keys.hmacShaKeyFor(Decoders.BASE64
            .decode("amFKDGqGyOkfz9G8f75CToHlPrjnCdCZ+b3U4jG1XJoK0VkB1VG8UJ0/w30VZSZoVFD9JMPdyjqJvGqkhELM9A=="));

        return Jwts.builder()
            .setSubject("anonymous")
            .signWith(otherKey, SignatureAlgorithm.HS512)
            .setExpiration(new Date(new Date().getTime() + ONE_MINUTE))
            .compact();
    }
}
