package com.example.Spesa;

import java.time.LocalDate;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Utente.Utente;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/spese")
public class SpesaController {

    private final SpesaRepository spesaRepository;

	private final SpesaService spesaService;

	public SpesaController(SpesaService servizio, SpesaRepository spesaRepository) {
		this.spesaService = servizio;
		this.spesaRepository = spesaRepository;
	}
	
	//VEDI SPESA SPECIFICA(ADMIN)
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public Optional<Spesa> trovaSpesaById(@PathVariable Long id){
		return spesaService.trovaSpesaById(id);
	}
	
	//VEDI TUTTE SPESE(ADMIN)
	@GetMapping("/tutte")
	@PreAuthorize("hasRole('ADMIN')")
	public List<SpesaDTO> trovaTutteSpese(){
		return spesaService.trovaTutteSpese();
	}
	
	@GetMapping("/tutte-con-utente")
	public List<SpesaDTO> getTutteSpeseConUtente() {
	    return spesaService.trovaTutteSpeseDTOConUtente();
	}
	
	//VEDI SPESE SINGOLO UTENTE
	@GetMapping("/mie")
	public List<SpesaDTO> getSpeseUtente() {
	    return spesaService.getSpeseUtente();
	}
	@GetMapping("/mie-paginato")
	public Page<SpesaDTO> getSpeseUtentePaginato(
	        @AuthenticationPrincipal Utente utente,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "3") int size
	) {
	    return spesaService.trovaPerUtentePaginato(utente,page, size);
	}
	
	@GetMapping("/tutte-paginato")
	@PreAuthorize("hasRole('ADMIN')")
	public Page<SpesaDTO> getTutteSpesePaginato(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "3") int size
	) {
	    return spesaService.trovaTutteSpesePaginato(page, size);
	}

	
	//AGGIUNGO SPESA A UTENTE(UTENTE/ADMIN)
	//Principal=rappresenta l’identità dell’utente (di solito contiene solo lo username).
	@PostMapping
	public ResponseEntity<Spesa> aggiungiSpesaAdUtente(@RequestBody Spesa nuovaSpesa, Principal principal) {
	    String username = principal.getName(); // prende username dal token
		Spesa s = spesaService.aggiungiSpesaAdUtente(username,nuovaSpesa);
		return ResponseEntity.ok(s);
	}
	
	//MODIFICA SPESA(UTENTE/ADMIN)
	//ESTRAGGO USERNAME DAL CONTESTO DI SICUREZZA(NON DIPENDO DA PAR. PASSATI DAL CLIENT)(+ SICURO)
    	//	+ "or @utenteService.isOwner(#id)") DAREBBE UN DOPPIO CONTROLLO
	@PutMapping("/{idSpesa}")
	public ResponseEntity<Spesa> modificaSpesa(@PathVariable Long idSpesa,@RequestBody Spesa nuovaSpesa){
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Spesa spesa = spesaService.modificaSpesa(username,idSpesa, nuovaSpesa);
		return ResponseEntity.ok(spesa);
	}
	
	//ELIMINO SPESA(ADMIN,UTENTE)
	//Authentication=fornisce informazioni dettagliate sull’utente autenticato, i suoi ruoli, i permessi, lo stato di autenticazione, ecc.
	@DeleteMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> eliminaSpesaByAdmin(@PathVariable Long id,Authentication authentication) {
		try {
			 spesaService.eliminaSpesaByAdmin(id, authentication);
	            return ResponseEntity.ok("Spesa eliminata");
	        } catch (AccessDeniedException e) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi eliminare questa spesa");
	        } catch (EntityNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Spesa non trovata");
	        }
	    }
	
	@DeleteMapping("/utente/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> eliminaSpesaByUtente(@PathVariable Long id, Authentication authentication) {
	    try {
	        spesaService.eliminaSpesaByUtente(id, authentication);
	        return ResponseEntity.ok("Spesa eliminata");
	    } catch (AccessDeniedException e) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Non puoi eliminare questa spesa");
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Spesa non trovata");
	    }
	}
	
	//LEGGE SPESE UTENTE CHE EFFETTUA RICHIESTA
	@GetMapping
	public List<Spesa> speseUtenteLoggato() {
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    return spesaService.trovaSpeseUtente(username);
	}
	
	//TROVO SPESE UTENTE SPECIFICO(UTENTE/ADMIN)
		@GetMapping("/utente/{username}")
		@PreAuthorize("hasRole('ADMIN')")
		public List<Spesa> trovaSpeseUtente(@PathVariable String username){
			return spesaService.trovaSpeseUtente(username);
		}
		
	// CON @REQ.PARAM(REQ.=FALSE) RICEVI I PARAMETRI SOLO SE PRESENTI 
	//COSI POSSIAMO METTERE + ROTTE NELLO STESSO METODO
	//(UTENTE/ADMIN)
		@GetMapping("/filtro")
		public List<SpesaDTO> filtraSpese(
				@RequestParam(required = false) String utente,
		        @RequestParam(required = false) String categoria,
		        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate a,
		        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate b) {
		    
		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    String username = auth.getName();
		    boolean isAdmin = auth.getAuthorities().stream()
		                          .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

		    List<Spesa> spese;

		    if (isAdmin) {
		        // L'admin può vedere tutto, filtrando come prima
		    	if(utente != null&& !utente.isBlank()) {
		    		spese = spesaService.trovaByUtente(utente);
		    	} else if (categoria != null && a != null && b != null) {
		            spese = spesaService.trovaByCategoriaEData(categoria, a, b);
		        } else if (categoria != null) {
		            spese = spesaService.trovaByCategoria(categoria);
		        } else if (a != null && b != null) {
		            spese = spesaService.trovaByData(a, b);
		        } else {
		            spese = spesaRepository.findAll();
		        }
		    } else {
		    	if(utente != null) {
		    		throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Non puoi filtrare per utente");
		    	}
		        // L'utente normale vede solo le sue spese, eventualmente filtrate
		        if (categoria != null && a != null && b != null) {
		            spese = spesaService.trovaByUtenteCategoriaEData(username, categoria, a, b);
		        } else if (categoria != null) {
		            spese = spesaService.trovaByUtenteECategoria(username, categoria);
		        } else if (a != null && b != null) {
		            spese = spesaService.trovaByUtenteEData(username, a, b);
		        } else {
		            spese = spesaService.trovaByUtente(username);
		        }
		    }

		    return spese.stream()
		                .map(SpesaDTO::fromSpesa)
		                .collect(Collectors.toList());
		}
		
		@GetMapping("/totaleSpese")
		public Double totaleSpese() {
		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    String username = auth.getName();
		    return spesaService.totaleSpese(username);
		}

}
