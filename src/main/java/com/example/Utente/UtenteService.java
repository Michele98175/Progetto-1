package com.example.Utente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtenteService {

    private final PasswordEncoder passwordEncoder;

	@Autowired
    private UtenteRepository utenteRepository;

    UtenteService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Utente trovaUtente(String username) {
        return utenteRepository.findByUsernameConSpese(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }
    
    public void eliminaUtente(Long id) {
    	if (!utenteRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato");
        }
    	utenteRepository.deleteById(id);
    }
    
    @Transactional
    public Utente modificaUtente(String username,Long id,Utente nuoviDati) {
    	// Recupera utente esistente dal DB
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        
        // Controllo: solo admin o utente stesso possono modificare
        if (!utenteHaRuolo("ADMIN") && !utente.getUsername().equals(username)) {
            throw new AccessDeniedException("Non hai i permessi per modificare questo utente");
        }
        
    	if(nuoviDati.getUsername() != null)
    	utente.setUsername(nuoviDati.getUsername());
    	if(nuoviDati.getPassword() != null)
        	utente.setPassword(passwordEncoder.encode(nuoviDati.getPassword()));
    	if(nuoviDati.getEmail() != null)
        	utente.setEmail(nuoviDati.getEmail());
    	
    	return utenteRepository.save(utente);
    }
    
    private boolean utenteHaRuolo(String ruolo) {
	    return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
	        .stream()
	        .anyMatch(a -> a.getAuthority().equals("ROLE_" + ruolo));
	}
}
