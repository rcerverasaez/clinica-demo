package es.demo.clinica.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Reglas de acceso. La reserva es pública; la recepción y la agenda requieren login.
 * Las contraseñas de la demo vienen de propiedades: son ficticias y se pueden cambiar con variables de entorno.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/error", "/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/treatments", "/api/specialists").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/appointments").permitAll()
                        .requestMatchers("/api/**").hasRole("RECEPCION")
                        .requestMatchers("/recepcion/**").hasRole("RECEPCION")
                        .requestMatchers("/agenda/**").hasRole("ESPECIALISTA")
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder,
            @Value("${clinica.demo.recepcion-password}") String recepcionPassword,
            @Value("${clinica.demo.especialista-password}") String especialistaPassword,
            @Value("${clinica.demo.vbote-password}") String vbotePassword) {
        return new InMemoryUserDetailsManager(
                User.withUsername("recepcion").password(encoder.encode(recepcionPassword)).roles("RECEPCION").build(),
                User.withUsername("especialista").password(encoder.encode(especialistaPassword)).roles("ESPECIALISTA").build(),
                // Usuario de prueba con acceso a recepción y a la agenda
                User.withUsername("vbote@vbote.com").password(encoder.encode(vbotePassword)).roles("RECEPCION", "ESPECIALISTA").build());
    }
}
