package com.example.Token;

import com.example.Utente.UtenteRepository;
import com.example.Utente.Utente;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DettagliUtenteService implements UserDetailsService{
 
	@Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByUsernameConSpese(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
        return utente;
    }
    /*
     // Converti il ruolo enum in una authority con prefisso "ROLE_"
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().name());

    return new org.springframework.security.core.userdetails.User(
            utente.getUsername(),
            utente.getPassword(),
            List.of(authority)
    );
     * */
   
}

