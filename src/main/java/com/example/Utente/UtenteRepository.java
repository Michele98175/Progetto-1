package com.example.Utente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UtenteRepository extends JpaRepository<Utente,Long>{

	boolean existsByUsername(String username);
	

	
    @Query("SELECT u FROM Utente u LEFT JOIN FETCH u.spese WHERE u.username = :username")
	Optional<Utente> findByUsernameConSpese(@Param("username")String username);
	
}
