package pedometer.momo.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Profile("local")
    @EnableWebSecurity
    public static class SecurityDisabledConfig {
        @Bean
        public SecurityFilterChain noFilterChain(HttpSecurity http) throws Exception {

            http.authorizeRequests()
                    .mvcMatchers("/").permitAll();
            http.headers().frameOptions().disable();
            http.csrf().ignoringAntMatchers("/**"); // Ignore CSRF protection for all
            return http.build();
        }
    }

    @Profile("prod")
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @EnableWebSecurity
    public static class SecurityEnabledConfig {
        @Value("${auth0.audience}")
        private String audience;

        @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
        private String issuer;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http.authorizeRequests()
                    .mvcMatchers("/_/healthz").permitAll()
                    .mvcMatchers("/pedometer").authenticated()
                    .and().oauth2ResourceServer().jwt();
            http.csrf().ignoringAntMatchers("/pedometer");
            return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder() {

            NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

            OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
            OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
            OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

            jwtDecoder.setJwtValidator(withAudience);

            return jwtDecoder;
        }
    }
}
