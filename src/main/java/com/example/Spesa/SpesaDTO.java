package com.example.Spesa;

import java.time.LocalDate;

import org.hibernate.LazyInitializationException;

public class SpesaDTO {
	    private Long id;
	    private String metodoPagamento;
	    private double importo;
	    private String categoria;
	    private LocalDate data;
	    private String usernameUtente;
	       
		public SpesaDTO(Long id,String metodoPagamento, double importo, String categoria, LocalDate data,
				String usernameUtente) {
			this.id = id;
		    this.metodoPagamento = metodoPagamento;
			this.importo = importo;
			this.categoria = categoria;
			this.data = data;
			this.usernameUtente = usernameUtente;
		}			
		
		public SpesaDTO() {
		}

		public static SpesaDTO fromSpesa(Spesa spesa) {
		    SpesaDTO dto = new SpesaDTO();
		    dto.setId(spesa.getId());
		    dto.setMetodoPagamento(spesa.getMetodoPagamento());		   
		    dto.setImporto(spesa.getImporto());
		    dto.setCategoria(spesa.getCategoria());
		    dto.setData(spesa.getData());
		    
		    if (spesa.getUtente() != null) {
	            dto.setUsernameUtente(spesa.getUtente().getUsername());
	        } else {
	            dto.setUsernameUtente("N/A");
	        }
	        return dto;
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
		public String getUsernameUtente() {
			return usernameUtente;
		}
		public void setUsernameUtente(String usernameUtente) {
			this.usernameUtente = usernameUtente;
		}
		
	    
	    
}
