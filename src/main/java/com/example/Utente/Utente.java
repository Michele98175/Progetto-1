package com.example.Utente;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.Spesa.Spesa;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;


@Entity
public class Utente implements UserDetails{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String username;
	private String password;
	private String email;
	
	//RELAZIONE UNO A MOLTI TRA UTENTE E SPESA(UTENTE PUO AVERE TANTE SPESE)
	//mappedBy = "utente"(RELAZIONE GIA MAPPATA NELLA CLASSE SPESA)
	//cascade = CascadeType.ALL(QUANDO SALVI UTENTE SALVI ANCHE TUTTE LE SUE SPESE,IDEM SE LO ELIMINI (ELIMINA TUTTE SPESE))
	@OneToMany(mappedBy = "utente",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference  // serve per gestire la serializzazione bidirezionale
	private List<Spesa> spese;
	
	@Enumerated(EnumType.STRING)
	private Ruolo ruolo;
	
	public enum Ruolo {
	    USER,
	    ADMIN
	}
	
	public Utente() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String nome) {
		this.username = nome;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Spesa> getSpese() {
		return spese;
	}

	public void setSpese(List<Spesa> spese) {
		this.spese = spese;
	}
	
	
	public Ruolo getRuolo() {
	    return ruolo;
	}
	
	public void setRuolo(Ruolo ruolo) {
		this.ruolo=ruolo;
	}

	// 👇 Permessi/ruoli (per ora vuoti o con un solo ruolo standard)
	// 👇 Metodo che restituisce il ruolo dell'utente
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	return List.of(new SimpleGrantedAuthority("ROLE_" + ruolo.name())); // oppure restituisci un ruolo se lo usi
    }

    // 👇 Stato account (puoi sempre restituire true se non hai logica avanzata)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

