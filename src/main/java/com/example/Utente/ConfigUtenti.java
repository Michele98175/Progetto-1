package com.example.Utente;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.Spesa.Spesa;
import com.example.Spesa.SpesaRepository;
import com.example.Utente.Utente.Ruolo;

@Component
public class ConfigUtenti implements CommandLineRunner{
	
	private final UtenteRepository utenteRepository;
	private final SpesaRepository spesaRepository;
	private final PasswordEncoder passwordEncoder;

	
	 public ConfigUtenti(UtenteRepository utenteRepository,SpesaRepository spesaRepository,PasswordEncoder passwordEncoder) {
	        this.utenteRepository = utenteRepository;
	        this.spesaRepository = spesaRepository;
	        this.passwordEncoder = passwordEncoder;
	    }
	 
	 
	public  void run(String... args) throws Exception{

	    if (!utenteRepository.existsByUsername("Martina")) {

	Utente Martina = new Utente();
	Martina.setUsername("Martina");
	Martina.setRuolo(Ruolo.USER);
	
	String passwordInChiaro = "martina123";
	String encodedPwd = passwordEncoder.encode(passwordInChiaro);
	Martina.setPassword(encodedPwd);
	
	utenteRepository.save(Martina);
	
	Spesa spesa1 = new Spesa();
	
	spesa1.setUtente(Martina);
	spesa1.setDescrizione("Amazon");
	spesa1.setImporto(19.99);
	spesa1.setCategoria("Abbigliamento");
	spesa1.setData(LocalDate.of(2025, 05, 16));
	
	Spesa spesa2 = new Spesa();
	
	spesa2.setUtente(Martina);
	spesa2.setDescrizione("Pandora");
	spesa2.setImporto(129.49);
	spesa2.setCategoria("Gioielleria");
	spesa2.setData(LocalDate.of(2025, 04, 03));
	
	spesaRepository.save(spesa1);
	spesaRepository.save(spesa2);
	System.out.println("✔ Utente creato!");
}else {
    System.out.println("ℹ L'utente 'Martina' esiste già. Nessun dato inserito.");

}
	
}}