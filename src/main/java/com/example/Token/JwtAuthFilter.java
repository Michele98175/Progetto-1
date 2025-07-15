package com.example.Token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;


/* Spring Security ha bisogno di un filtro che:
    intercetti ogni richiesta HTTP
    legga il token JWT
    verifichi che sia valido
    e se lo è imposti l’utente autenticato nel contesto di sicurezza.
*/
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	@Autowired
    private JwtToken jwtToken;

    @Autowired
    private DettagliUtenteService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	String path = request.getServletPath();

        // Lista di rotte pubbliche da saltare (modifica e aggiungi le tue rotte pubbliche)
        if (path.equals("/auth/login") || 
            path.equals("/auth/registrazione") ||
          //  path.equals("/api/spese/filtro") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/img/")) {
            filterChain.doFilter(request, response);
            return;
        }
    	
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Header Authorization mancante o non valido");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // salta "Bearer "
        
        System.out.println("Auth Header: " + authHeader);
        System.out.println("Estratto JWT: " + jwt);
        
        if(jwt == null || jwt.isBlank()|| jwt.equals("undefined")) {
        	 filterChain.doFilter(request, response);
             return;
        }
       username = jwtToken.estraiUsername(jwt); // 

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtToken.isTokenValido(jwt, userDetails)) {

            	String ruolo = jwtToken.estraiRuolo(jwt);
                GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + ruolo);
                List<GrantedAuthority> authorities = List.of(authority);

            		    UsernamePasswordAuthenticationToken authToken =
            		            new UsernamePasswordAuthenticationToken(userDetails, null,authorities);
            		    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            		    SecurityContextHolder.getContext().setAuthentication(authToken);
            		}
            }
        filterChain.doFilter(request, response);
    }
}
 
