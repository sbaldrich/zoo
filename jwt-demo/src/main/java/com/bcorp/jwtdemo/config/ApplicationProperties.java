package com.bcorp.jwtdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Security security = new Security();

    public Security getSecurity() { return security; }

    public static class Security{

        private final Authentication authentication = new Authentication();

        public Authentication getAuthentication() { return authentication; }

        public static class Authentication {
            private final JWT jwt = new JWT();

            public JWT getJwt() { return jwt;}

            public static class JWT {
                @NotEmpty
                private String base64secret;
                private int tokenValidityInSeconds = 3600; // 60 minutes

                public String getBase64secret() { return base64secret;}

                public void setBase64secret(String base64secret) {this.base64secret = base64secret;}

                public int getTokenValidityInSeconds() { return tokenValidityInSeconds; }

                public void setTokenValidityInSeconds(int tokenValidityInSeconds) { this.tokenValidityInSeconds = tokenValidityInSeconds; }
            }
        }
    }
}
