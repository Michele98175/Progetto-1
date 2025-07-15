package com.example.Spesa;

import java.time.LocalDate;

import org.hibernate.LazyInitializationException;

public class SpesaDTO {
	    private Long id;
	    private String descrizione;
	    private double importo;
	    private String categoria;
	    private LocalDate data;
	    private String usernameUtente;
	       
		public SpesaDTO(Long id, String descrizione, double importo, String categoria, LocalDate data,
				String usernameUtente) {
			this.id = id;
			this.descrizione = descrizione;
			this.importo = importo;
			this.categoria = categoria;
			this.data = data;
			this.usernameUtente = usernameUtente;
		}
		/*
	    public static SpesaDTO fromSpesa(Spesa spesa) {
	        return new SpesaDTO(
	            spesa.getId(),
	            spesa.getDescrizione(),
	            spesa.getImporto(),
	            spesa.getCategoria(),
	            spesa.getData(),
	            spesa.getUtente() != null ? spesa.getUtente().getUsername() : null
	        );
	    }
		*/
		
		public SpesaDTO() {
		}

		public static SpesaDTO fromSpesa(Spesa spesa) {
		    SpesaDTO dto = new SpesaDTO();
		    dto.setId(spesa.getId());
		    dto.setDescrizione(spesa.getDescrizione());
		    dto.setImporto(spesa.getImporto());
		    dto.setCategoria(spesa.getCategoria());
		    dto.setData(spesa.getData());
		    
		    try {
		        dto.setUsernameUtente(spesa.getUtente().getUsername());
		    } catch (LazyInitializationException e) {
		        dto.setUsernameUtente("N/A"); // O gestisci in altro modo
		    }
		    
		    return dto;
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getDescrizione() {
			return descrizione;
		}
		public void setDescrizione(String descrizione) {
			this.descrizione = descrizione;
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
		public String getUsernameUtente() {
			return usernameUtente;
		}
		public void setUsernameUtente(String usernameUtente) {
			this.usernameUtente = usernameUtente;
		}
		
	    
	    
}
