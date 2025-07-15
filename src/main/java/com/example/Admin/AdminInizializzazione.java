package com.example.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.Utente.Utente;
import com.example.Utente.UtenteRepository;

//CommandLineRunner esegue automaticamente una volta che app è avviata
@Component
public class AdminInizializzazione implements CommandLineRunner{
	
	@Value("${admin.username}")
	private String adminUsername;

	@Value("${admin.password}")
	private String adminPassword;

	@Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception{
        if (!utenteRepository.existsByUsername(adminUsername)) {
            Utente admin = new Utente();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword)); // Sicuro solo in dev
            admin.setRuolo(Utente.Ruolo.ADMIN);
            utenteRepository.save(admin);
            System.out.println("✔ Admin creato!");
        }
    }
}
