package ma.inwi.msproject.security;

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
                //.authorizeHttpRequests(ar -> ar.requestMatchers("/project/all").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/filter").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/nextgate/*").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/delete/*").permitAll())

                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/not-affected-projects").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/interlocutors-impact").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/interlocutors-response").permitAll())

                .authorizeHttpRequests(ar -> ar.requestMatchers("/project/find/*").permitAll())

                //.authorizeHttpRequests(ar -> ar.requestMatchers("/project/**").hasAuthority("OWNER"))
                //.authorizeHttpRequests(ar -> ar.requestMatchers("/project/{projectId}/{username}").hasAuthority("SPOC"))

                .authorizeHttpRequests(ar -> ar.requestMatchers("/gate/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/departement/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/tracking/**").permitAll())


                .authorizeHttpRequests(ar -> ar.requestMatchers("/departement-gateproject/**")
                        .hasAnyAuthority("INTERLOCUTEUR_SIGNALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT", "OWNER", "SPOC"))

                //.authorizeHttpRequests(ar -> ar.requestMatchers("/departement-gateproject/**").hasAuthority("OWNER"))

                .authorizeHttpRequests(ar -> ar.requestMatchers("/dashboard/**")
                        .hasAnyAuthority("INTERLOCUTEUR_SIGNALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT", "OWNER", "SPOC"))


                //.authorizeHttpRequests(ar -> ar.requestMatchers("/required-action/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/gate-project/**").hasAuthority("OWNER"))
                .authorizeHttpRequests(ar -> ar.requestMatchers("/tracking-gate/**").permitAll())


                .authorizeHttpRequests(ar -> ar.requestMatchers("/tracking-gate/**").hasAuthority("ADMIN"))
                .authorizeHttpRequests(ar -> ar
                        .requestMatchers("/required-action/required-action-by-label/*")
                        .hasAnyAuthority("INTERLOCUTEUR_SIGNALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT")
                )
                .authorizeHttpRequests(ar -> ar
                        .requestMatchers("/required-action/**")
                        .hasAnyAuthority("INTERLOCUTEUR_SIGNALE_IMPACT", "SPOC", "OWNER", "INTERLOCUTEUR_RETOUR_IMPACT")
                )

                .authorizeHttpRequests(ar -> ar.requestMatchers("/gate-project/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/tracking-gate/**").permitAll())

                //.authorizeHttpRequests(ar -> ar.requestMatchers("/action/**").permitAll())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/api/config-required-actions/**").permitAll())

                .authorizeHttpRequests(ar -> ar.requestMatchers("/action/add").hasAnyAuthority("INTERLOCUTEUR_SIGNALE_IMPACT", "SPOC"))
                .authorizeHttpRequests(ar -> ar.requestMatchers("/action/respond/{id}").hasAuthority("INTERLOCUTEUR_RETOUR_IMPACT"))


                .authorizeHttpRequests(ar -> ar.requestMatchers("/dashboard/**").permitAll())

                .authorizeHttpRequests(ar -> ar.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll())
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