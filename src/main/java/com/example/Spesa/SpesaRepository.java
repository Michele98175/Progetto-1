package com.example.Spesa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import com.example.Utente.Utente;

@Repository
public interface SpesaRepository extends JpaRepository<Spesa, Long>{ 

	//SE VUOI DARE IL NOME CHE VUOI AL METODO DEVI USARE ANNOTAZIONE QUERY 
	//ALTRIMENTI DEVI USARE NOME SPECIFICO (findBy)PER FAR CAPIRE A SPRING DATA LA QUERY DA ESEGUIRE
	

    @Query("SELECT s FROM Spesa s JOIN FETCH s.utente WHERE s.utente = :utente")
    List<Spesa> findByUtente(@Param("utente") Utente utente);
	
	@Query("SELECT s FROM Spesa s JOIN FETCH s.utente WHERE s.categoria = :categoria")
	List<Spesa> findByCategoria(@Param("categoria") String categoria);

	@Query("SELECT s FROM Spesa s JOIN FETCH s.utente WHERE s.data BETWEEN :a AND :b")
	List<Spesa> findByData(@Param("a") LocalDate a, @Param("b") LocalDate b);

	@Query("SELECT s FROM Spesa s JOIN FETCH s.utente WHERE s.categoria = :categoria AND s.data BETWEEN :a AND :b")
	List<Spesa> findByCategoriaEDataBetween(@Param("categoria") String categoria, @Param("a") LocalDate a, @Param("b") LocalDate b);
	
	@Query("SELECT s FROM Spesa s  JOIN FETCH s.utente")
	List<Spesa> findAllConUtente();
	
	List<Spesa> findByUtenteUsername(String username);
	
	Optional<Spesa> findByIdAndUtenteUsername(Long id,String username);
	

	    List<Spesa> findByUtenteUsernameAndCategoria(String username, String categoria);

	    List<Spesa> findByUtenteUsernameAndDataBetween(String username, LocalDate a, LocalDate b);

	    List<Spesa> findByUtenteUsernameAndCategoriaAndDataBetween(String username, String categoria, LocalDate a, LocalDate b);

	@Query("SELECT new com.example.Spesa.SpesaDTO(s.id, s.descrizione,s.importo,s.categoria, s.data, u.username) FROM Spesa s JOIN s.utente u")
	List<SpesaDTO> findAllSpeseDTO();

	@Query("SELECT s FROM Spesa s WHERE s.utente = :utente "
		     + "AND (:categoria IS NULL OR s.categoria = :categoria) "
		     + "AND (:a IS NULL OR :b IS NULL OR s.data BETWEEN :a AND :b)")
		List<Spesa> filtra(@Param("utente") Utente utente,
		                   @Param("categoria") String categoria,
		                   @Param("a") LocalDate a,
                           @Param("b") LocalDate b);

    @Query("SELECT COALESCE(SUM(s.importo), 0) FROM Spesa s WHERE s.utente.username = :username")
    Double findTotalByUtente(@Param("username") String username);

    @Query("SELECT COALESCE(SUM(s.importo), 0) FROM Spesa s")
    Double findTotalSpese();
    
    @Query(value = "SELECT s FROM Spesa s JOIN FETCH s.utente WHERE s.utente = :utente",
    	   countQuery = "SELECT count(s) FROM Spesa s WHERE s.utente = :utente")
    Page<Spesa> findByUtentePag(Utente utente, Pageable pageable);

    @Query(value = "SELECT s FROM Spesa s JOIN s.utente u",
    	       countQuery = "SELECT count(s) FROM Spesa s")
    	Page<Spesa> findByAdminPag(Pageable pageable);

}