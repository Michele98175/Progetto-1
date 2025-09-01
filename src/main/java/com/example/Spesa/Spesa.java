package com.example.Spesa;

import java.time.LocalDate;

import org.springframework.data.annotation.Version;

import com.example.Utente.Utente;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Spesa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)/*GENERA VALORE E LO INCREMENTA (ID)*/
	private Long id;
	private String metodoPagamento;
	private double importo;
	private String categoria;
	private LocalDate data;
	
	@ManyToOne(fetch = FetchType.LAZY)//puoi anche mettere optional = false garantisce anche a livello di database che una Spesa non possa esistere senza utente.
	@JsonBackReference
	@JoinColumn(name = "utente_id")
	private Utente utente;
	
	@Version
	private Long versione; // Hibernate gestisce automaticamente la versione
	
	public enum MetodoPagamento {
	    CONTANTI,
	    CARTA
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMetodoPagamento() {
		return metodoPagamento;
	}
	public void setMetodoPagamento(String metodoPagamento) {
		this.metodoPagamento = metodoPagamento;
	}
	public double getImporto() {
		return importo;
	}
	public void setImporto(double importo) {
		this.importo = importo;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public LocalDate getData() {
		return data;
	}
	public void setData(LocalDate data) {
		this.data = data;
	}
	public Utente getUtente() {
		return utente;
	}
	public void setUtente(Utente utente) {
		this.utente = utente;
	}
}
