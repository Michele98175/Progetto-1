package com.example.Autenticazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Token.JwtToken;
import com.example.Utente.Utente;
import com.example.Utente.UtenteRepository;

@Service
public class AuthService {

	@Autowired
	private UtenteRepository utenteRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtToken jwt;
    
	
	//LOGICA DI REGISTRAZIONE
	public void registra(String username,String password) {
		//VERIFICA SE UTENTE ESISTE GIA 
		if(utenteRepository.existsByUsername(username)) {
			throw new RuntimeException("username già esistente");
		}
		Utente utente=new Utente();
		utente.setUsername(username);
		utente.setPassword(passwordEncoder.encode(password));//CRIPTA PWD(CODIFICA SICURA)
        utente.setRuolo(Utente.Ruolo.USER); // assegna ruolo USER di default
		
		utenteRepository.save(utente);
	}
	//LOGICA LOGIN
	public String login(String username, String password) {
		
		//SE UTENTE è REGISTRATO
	    Utente utente = utenteRepository.findByUsernameConSpese(username)
	        .orElseThrow(() -> new RuntimeException("Utente non trovato"));

	    //VERIFICHI PWD
	    if (!passwordEncoder.matches(password, utente.getPassword())) {
	        throw new RuntimeException("Password errata");
	    }
         
	    //SE PWD è CORRETTA GENERO TOKEN
	    return jwt.generaToken(username,utente.getRuolo().name());    
	}	
	
	public void promuoviUtente(String username) {
	Utente utente = utenteRepository.findByUsernameConSpese(username)
	  .orElseThrow(() ->new UsernameNotFoundException("Utente non trovato"));
	
	  utente.setRuolo(Utente.Ruolo.ADMIN);
	  utenteRepository.save(utente);
	}
	}