package com.example.Autenticazione;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Token.JwtAuthFilter;

@Configuration
@EnableWebSecurity//PERMETTE USO DI @PREAUTHORIZE NEI CONTROLLER
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
    private final JwtAuthFilter jwtAuthFilter;
    
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }


	//OGGETTO CHE SERVE PER CODIFICARE PASSWORD 
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// DEFINISCI MANUALMENTE L'AUTHENTICATION MANAGER
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
    
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager,
	                                               JwtAuthFilter jwtAuthFilter) throws Exception {
	    return http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                    // ACCESSO PUBBLICO
	            		.requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
	            		.requestMatchers(HttpMethod.POST, "/auth/registrazione").permitAll()
	                    .requestMatchers("/index.html","/Registrazione.html", "/", "/css/**", "/js/**", "/img/**").permitAll()
                        // ADMIN/UTENTE
	                    .requestMatchers(HttpMethod.GET, "/api/spese/filtro").hasAnyRole("ADMIN","USER")
	                    .requestMatchers(HttpMethod.GET, "/api/spese").hasAnyRole("USER", "ADMIN")
	                    .requestMatchers(HttpMethod.GET, "/api/spese/totaleSpese").hasAnyRole("USER", "ADMIN")
	                    .requestMatchers("/api/spese/tutte-con-utente-paginato").hasAnyRole("USER","ADMIN")

	                    // SOLO ADMIN	 
	                    .requestMatchers("/api/spese/tutte-paginato").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.POST, "/api/utenti/crea").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.GET, "/api/utenti/tutti").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.DELETE, "/api/utenti/**").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.GET, "/api/spese/tutte").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.DELETE, "/api/spese/admin/**").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.DELETE, "/api/utenti/elimina/**").hasRole("ADMIN")
	                    //.requestMatchers(HttpMethod.GET, "/api/spese/{id}").hasRole("ADMIN")
		                // .requestMatchers("/auth/promuovi/**").hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.DELETE, "/api/spese/utente/**").hasRole("USER")
	                    //PER UTENTI AUTENTICATI
	                    //.requestMatchers(HttpMethod.GET, "/api/spese").authenticated()
	                    // TUTTO IL RESTO DEVE ESSERE AUTENTICATO
	                    .anyRequest().authenticated()
	            )
	            .authenticationManager(authManager)
	            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}

}


