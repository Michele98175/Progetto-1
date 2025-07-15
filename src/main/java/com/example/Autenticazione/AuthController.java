package com.example.Autenticazione;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@PostMapping("/registrazione")
	public ResponseEntity<String> registra(@RequestBody AuthRequest request){
		authService.registra(request.getUsername(),request.getPassword());
		return ResponseEntity.ok("Utente registrato!!!");
	}
	
	//OBIETTIVO: SE CREDENZIALI SONO CORRETTE RESTITUISCE JWT TOKEN
	//ALTRIMENTI DA ERRORE
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request){
        String token = authService.login(request.getUsername(),request.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);	}
	
	@PostMapping("/promuovi/{username}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> promuoviUtente(@PathVariable String username) {
		authService.promuoviUtente(username);
		return ResponseEntity.ok("Utente " + username + " è stato promosso ad ADMIN");
	}
}
