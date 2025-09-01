package com.example.Spesa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import com.example.Utente.Utente;
import com.example.Utente.Utente.Ruolo;
import com.example.Utente.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SpesaService {

	private final SpesaRepository spesaRepository;
	
	private UtenteRepository utenteRepository;

	public SpesaService(SpesaRepository repository,UtenteRepository utenteRepository) {
		this.spesaRepository = repository;
		this.utenteRepository = utenteRepository;
	}
	
	public Optional<Spesa> trovaSpesaById(Long id){
		return spesaRepository.findById(id);
	}
	
	    @Transactional
	    public Page<SpesaDTO> trovaTutteSpesePaginato(int page, int size) {
	    	 Pageable pageable = PageRequest.of(page, size, Sort.by("data").descending());
	    	    Page<Spesa> spesePage = spesaRepository.findAllSpese(pageable);
	    	    return spesePage.map(SpesaDTO::fromSpesa);
	    }
	    @Transactional
	    public Page<SpesaDTO> trovaPerUtentePaginato(Utente utente, int page, int size) {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("data").descending()); // ordina per data
	        Page<Spesa> spesePage = spesaRepository.findByUtentePag(utente, pageable);
	        return spesePage.map(SpesaDTO::fromSpesa);
	    }
	
	@Transactional
	public List<SpesaDTO> getSpeseUtente() {
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    
	    List<Spesa> spese = spesaRepository.findByUtenteUsername(username);
	    return spese.stream().map(SpesaDTO::fromSpesa).collect(Collectors.toList());
	}
	
	//METODO PER SALVARE LA SPESA DI UN UTENTE 
	//SE UTENTE ESISTE ASSOCIO NUOVA SPESA A UTENTE
	//E SALVO NUOVA SPESA NEL DATABASE
	//la sessione dura per tutta l’esecuzione del metodo e
	// puoi lavorare serenamente con entità lazy(ovvero viene caricata solo quando la richedi)
	@Transactional 
	public Spesa aggiungiSpesaAdUtente(String username,Spesa nuovaSpesa) {
		Utente utente = utenteRepository.findByUsernameConSpese(username)
				.orElseThrow(()-> new UsernameNotFoundException("username non trovato"));
		nuovaSpesa.setUtente(utente);
		return spesaRepository.save(nuovaSpesa);
	}

	@Transactional
	public void eliminaSpesaByAdmin(Long id,Authentication authentication) {
				
		Spesa spesa = spesaRepository.findById(id)
		   .orElseThrow(() -> new RuntimeException("Spesa non trovata."));
		
	        boolean isAdmin = authentication.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	        if (!isAdmin ) {
	            throw new AccessDeniedException("Non autorizzato a eliminare questa spesa");
	        }else {

	        spesaRepository.delete(spesa);}
	    }
	
	@Transactional
	public void eliminaSpesaByUtente(Long id, Authentication authentication) {
	    Spesa spesa = spesaRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Spesa non trovata."));
	    
	    String username = authentication.getName();
	    
	    if (!spesa.getUtente().getUsername().equals(username)) {
	        throw new AccessDeniedException("Non autorizzato a eliminare questa spesa");
	    }else {
	    spesaRepository.delete(spesa);
	    System.out.println("Spesa eliminata: " + id);}
	}
	
	@Transactional
	public Spesa modificaSpesa(String username,Long idSpesa,Spesa nuovaSpesa) {
		Spesa spesa = spesaRepository.findById(idSpesa)
				.orElseThrow(()-> new EntityNotFoundException("Spesa con ID "+idSpesa +" non trovata!"));
		
	    // Verifica che la spesa appartenga all'utente giusto
	    if (!utenteHaRuolo("ADMIN") && !spesa.getUtente().getUsername().equals(username)) 
	        throw new AccessDeniedException("Non hai il permesso per modificare questa spesa");
		
		spesa.setMetodoPagamento(nuovaSpesa.getMetodoPagamento());
		spesa.setImporto(nuovaSpesa.getImporto());
		spesa.setData(nuovaSpesa.getData());
		spesa.setCategoria(nuovaSpesa.getCategoria());
		
		return spesaRepository.save(spesa);
	}
	
	//METODO PER OTTENERE TUTTE LE SPESE DI UN UTENTE
	public List<Spesa> trovaSpeseUtente(String username){
		Utente utente = utenteRepository.findByUsernameConSpese(username)
		.orElseThrow(() -> new UsernameNotFoundException("utente non trovato"));
		 return spesaRepository.findByUtenteUsername(username);
		}
	
	// PER EVITARE PROBLEMI DI LAZYINITIALIZATION EXCEPTION
	// ESEGUIRE LA MAPPATURA DA ENTITÀ A DTO NEL SERVICE
	// E USARE @TRANSACTIONAL PER TENERE APERTA LA SESSIONE JPA
	@Transactional(readOnly = true)
	public List<SpesaDTO> trovaByUtenteECategoria(String username, String categoria) {
	    return spesaRepository.findByUtenteUsernameAndCategoria(username, categoria)
	            .stream()
	            .map(SpesaDTO::fromSpesa)
	            .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<SpesaDTO> trovaByUtenteEData(String username, LocalDate a, LocalDate b) {
	    return spesaRepository.findByUtenteUsernameAndDataBetween(username, a, b)
	            .stream()
	            .map(SpesaDTO::fromSpesa)
	            .collect(Collectors.toList());
	}

	

	@Transactional(readOnly = true)
	public List<SpesaDTO> trovaByCategoria(String categoria) {
	    return spesaRepository.findByCategoria(categoria)
	            .stream()
	            .map(SpesaDTO::fromSpesa)
	            .collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<SpesaDTO> trovaByData(LocalDate a, LocalDate b) {
	    return spesaRepository.findByData(a, b)
	            .stream()
	            .map(SpesaDTO::fromSpesa)
	            .collect(Collectors.toList());
	}

	

	@Transactional(readOnly = true)
	public List<SpesaDTO> trovaByUtente(String username) {
	    Utente utente = utenteRepository.findByUsernameConSpese(username)
	            .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
	    return spesaRepository.findByUtenteFetch(utente)
	            .stream()
	            .map(SpesaDTO::fromSpesa)
	            .collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<SpesaDTO> trovaTutte() {
	    return spesaRepository.findAllConUtente().stream()
	            .map(SpesaDTO::fromSpesa)
	            .collect(Collectors.toList());
	}
    @Transactional
    public List<SpesaDTO> filtraSpese(String username, String categoria, LocalDate a,LocalDate b) {
        Utente utente = utenteRepository.findByUsernameConSpese(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        List<Spesa> spese = spesaRepository.filtra(utente, categoria,a,b);

        return spese.stream().map(SpesaDTO::fromSpesa).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Double totaleSpese(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        
        return isAdmin ? spesaRepository.findTotalSpese() 
                      : spesaRepository.findTotalByUtente(username);
    }
    
  
	
    private boolean utenteHaRuolo(String ruolo) {
	    return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
	        .stream()
	        .anyMatch(a -> a.getAuthority().equals("ROLE_" + ruolo));
	}
	
	public SpesaDTO convertToDTO(Spesa spesa) {
        return new SpesaDTO(
            spesa.getId(),
            spesa.getMetodoPagamento(),
            spesa.getImporto(),
            spesa.getCategoria(),
            spesa.getData(),
            spesa.getUtente() != null ? spesa.getUtente().getUsername() : null
        );
    }
}
