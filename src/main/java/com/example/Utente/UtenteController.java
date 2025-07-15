package com.example.Utente;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Spesa.Spesa;
import com.example.Spesa.SpesaService;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {
	
	private UtenteRepository utenteRepository;
	private final UtenteService utenteService;
	private final SpesaService spesaService;
	
	public UtenteController(UtenteRepository utenteRepository,UtenteService utenteService,SpesaService spesaService) {
		this.utenteRepository = utenteRepository;
		this.utenteService = utenteService;
		this.spesaService = spesaService;
	}
	
    //CREO NUOVO UTENTE(ADMIN)
	@PostMapping("/crea")
	public Utente creaUtente(@RequestBody Utente utente) {
		return utenteRepository.save(utente);
	}
	
	//VEDI TUTTI UTENTI(ADMIN)
	@GetMapping("/tutti")
    @PreAuthorize("hasRole('ADMIN')")
	public List<Utente> trovaTuttiUtenti() {
	    return utenteRepository.findAll();
	}
	
	//VEDI UTENTE CON LE SUE SPESE(UTENTE/ADMIN)
	@GetMapping("/{username}")
	public Utente trovaUtente(@PathVariable String username) {
	    return utenteService.trovaUtente(username);
	} 
	
	//AGGIUNGI SPESA A UTENTE(UTENTE/ADMIN)
	@PostMapping("/{username}/spese")
    public ResponseEntity<Spesa> aggiungiSpesaAdUtente(@PathVariable String username, @RequestBody Spesa spesa) {
    Spesa nuovaSpesa = spesaService.aggiungiSpesaAdUtente(username, spesa);
		  return ResponseEntity.ok(nuovaSpesa);
    }
	
    //ELIMINI UTENTE(ADMIN)
    @DeleteMapping("elimina/{id}")
	public ResponseEntity<String> eliminaUtente(@PathVariable Long id) {
	utenteService.eliminaUtente(id);
	     return ResponseEntity.ok("Utente eliminato!");
    }
    
	//MODIFICO UTENTE(UTENTE/ADMIN)
    //ESTRAGGO USERNAME DAL CONTESTO DI SICUREZZA(NON DIPENDO DA PAR. PASSATI DAL CLIENT)(+ SICURO)
    //@PreAuthorize("hasRole('ADMIN') or @utenteService.isOwner(#id)") DAREBBE UN DOPPIO CONTROLLO
	@PutMapping("/{id}")
	public ResponseEntity<String> modificaUtente(@PathVariable Long id,@RequestBody Utente nuoviDati){
	String username = SecurityContextHolder.getContext().getAuthentication().getName();
	utenteService.modificaUtente(username,id, nuoviDati);
	     return ResponseEntity.ok("Utente aggiornato!");
    }
    }
