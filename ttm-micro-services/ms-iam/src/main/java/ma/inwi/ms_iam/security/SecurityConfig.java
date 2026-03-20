package ma.inwi.ms_iam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(ar -> ar.requestMatchers("/h2-console/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/auth/login/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/auth/refresh/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/auth/password/forgot/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/auth/signup/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/affectProject/*").permitAll())

                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/interlocutors-dep/*").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/users-by-department/*").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/reserved-for-spoc/*").permitAll())

                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/users/by-username/*").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/internal/users").permitAll())

                .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                .oauth2ResourceServer(o2 -> o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}