/*
METODI: CARICASPESE,AGGIUNGI,ELIMINA,MODIFICA
SALVAMODIFICA,ANNULLAMODIFICA,VEDITUTTESPESE
*/
let paginaCorrente = 0;
let totalePagine = 0;
const righePerPagina = 10;
			

	    
  function caricaSpese(pagina=0){
	  const token = localStorage.getItem("token");
	  paginaCorrente = pagina; // aggiorna la variabile globale
	  fetch(`http://localhost:8080/api/spese/mie-paginato?page=${pagina}&size=${righePerPagina}`,{
		  method: "GET",
		  headers: { 'Authorization': 'Bearer ' + token,
			  "Content-Type": "application/json"}
	  })
	  .then(risposta => {
        if (!risposta.ok) {
         return risposta.text().then(text => { console.error("Errore backend:", text);
          throw new Error("Errore nel recupero delle spese:\n" + text);});
         }
        return risposta.json();
       })
	  .then(dati => {
		  const spese = dati.content;
		   totalePagine = dati.totalPages; 
		  const tbody = document.getElementById("spese-tbody");
		  const numeroPagina = document.getElementById("numero-pagina");
		  numeroPagina.innerText = `Pagina ${paginaCorrente + 1} di ${totalePagine}`;

		  if (!tbody) {
			  alert("Errore: tabella spese non trovata nel DOM.");
			  return;
			}
		  tbody.innerHTML = "";// Pulisce la tabella prima di riempirla
		  let totale = 0;

		  spese.forEach(spesa => {
			totale += spesa.importo;

			  const riga = document.createElement("tr");
			  riga.innerHTML =  `
			     <td>${spesa.usernameUtente || "-"}</td>
			     <td>${spesa.metodoPagamento || "-"}</td>
				 <td>${spesa.categoria || "-"}</td>
		         <td>${spesa.importo.toFixed(2)} €</td>
		         <td>${spesa.data}</td>
		       <td>
				<div style="display: flex; gap: 8px;">
				 <button class="btn btn-modifica" onclick="modificaSpesa(${spesa.id}, this)">
				   <span class="testo">MODIFICA</span>
				    <svg class="icona" xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
				    <path d="M12 20h9"></path>
				    <path d="M16.5 3.5a2.121 2.121 0 113 3L7 19l-4 1 1-4 12.5-12.5z"></path>
				    </svg>
				 </button>
				 
				 
				 <button class="btn btn-elimina" onclick="eliminaSpesa(${spesa.id})">
				   <span class="testo">ELIMINA</span>
				    <svg class="icona" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" >
				 	<polyline points="3 6 5 6 21 6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				 	<path d="M19 6l-1 14H6L5 6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				 	<path d="M10 11v6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				 	<path d="M14 11v6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				 	<path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				   </svg>
				  </button>
				 </div>
				</td> `;
          tbody.appendChild(riga);
		  });
		  document.getElementById("lista-spese").style.display = "block";
		  document.getElementById("totale-spese").innerText = `€${totale.toFixed(2)}`;

		        // Disabilita pulsanti se necessario
				const bottoni = document.querySelectorAll("#paginazione button");
                 if(bottoni.length >=2){
					const btnPrec = bottoni[0];
					const btnSucc = bottoni[1];
				 
				  btnPrec.disabled = paginaCorrente === 0;
				  btnSucc.disabled = paginaCorrente >= totalePagine - 1;
				}
		      })
	          .catch(errore => {
		      alert("Errore nel caricamento delle spese: " + errore.message);});
              } 
			  
			  function trovaTutteSpese(pagina=0){
			  	  const token = localStorage.getItem("token");
			  	  paginaCorrente=pagina;
			  	  
			  	  fetch(`http://localhost:8080/api/spese/tutte-paginato?page=${pagina}&size=${righePerPagina}`,{
			  		  method: "GET",
			  		  headers: {"Authorization": "Bearer " + token,
			  		      "Content-Type": "application/json"}
			  	  })
			  	  .then(risposta => {
			  		  if(risposta.ok){
			  			 return risposta.json();
			  		  }else{
			  	         return risposta.text().then(text => { throw new Error(text) });
			  		  }
			  	  })
			  	  .then(dati => {
			        // Mostra sezione lista spese
			        document.getElementById("lista-spese").style.display = "block";
			  	  const spese = dati.content;
			  	  totalePagine = dati.totalPages;
			  	  

			        const tbody = document.getElementById("spese-tbody");
			        if (!tbody) {
			      	  alert("Errore: tabella spese non trovata nel DOM.");
			      	  return;
			      	}
					
			        tbody.innerHTML = ""; // Svuota prima
                    totale = 0;
					
			        spese.forEach(spesa => {
			        totale += spesa.importo;
					
			        const riga = document.createElement("tr");
			        riga.innerHTML = `
			  		   <td>${spesa.usernameUtente|| "-"}</td>
			           <td>${spesa.metodoPagamento|| "-"}</td>
			           <td>${spesa.categoria|| "-"}</td>
			           <td>${spesa.importo.toFixed(2)} €</td>
			           <td>${spesa.data}</td>
			           <td>
						 <button class="btn btn-modifica" onclick="modificaSpesa(${spesa.id}, this)">
						  <span class="testo">MODIFICA</span>
						  <svg class="icona" xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
						  <path d="M12 20h9"></path>
						  <path d="M16.5 3.5a2.121 2.121 0 113 3L7 19l-4 1 1-4 12.5-12.5z"></path>
						  </svg>
					     </button>
										 					 
						<button class="btn btn-elimina" onclick="eliminaSpesa(${spesa.id})">
						 <span class="testo">ELIMINA</span>
					     <svg class="icona" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" >
					 	 <polyline points="3 6 5 6 21 6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
			         	 <path d="M19 6l-1 14H6L5 6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
					 	 <path d="M10 11v6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
					 	 <path d="M14 11v6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
					 	 <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
					     </svg>
					    </button>
			          </td>
			          `;
			          tbody.appendChild(riga);
			        });
			  	  // Disabilita pulsanti se necessario
			  	  				const bottoni = document.querySelectorAll("#paginazione button");
			  	                   if(bottoni.length >=2){
			  	  					const btnPrec = bottoni[0];
			  	  					const btnSucc = bottoni[1];
			  	  				 
			  	  				  btnPrec.disabled = paginaCorrente === 0;
			  	  				  btnSucc.disabled = paginaCorrente >= totalePagine - 1;
			  	  				}
			      })
			  	  .catch(err => alert("Errore durante la modifica: " + err.message));
			    }
			    	
				
  function aggiungiSpesa(){
	  
	  const token = localStorage.getItem('token');
	  const metodoPagamento= document.getElementById("metodoPagamento").value;
	  const importo = parseFloat(document.getElementById("importo").value);
	  const categoria = document.getElementById("categoria").value;
	  const data = document.getElementById("data").value;
	  
	  const nuovaSpesa = {
		    metodoPagamento: metodoPagamento,
			importo: importo,
			categoria: categoria,
			data: data
	  };
	  fetch("http://localhost:8080/api/spese",{
		  method: "POST",
		  headers: {
		      "Authorization": "Bearer " + token,
		      "Content-Type": "application/json" },
		  body: JSON.stringify(nuovaSpesa)
	  })
	  .then(risposta => {
		  if(risposta.ok){
			  alert("Spesa aggiunta con successo!");
			  //SVUOTO CAMPI 
			  document.getElementById("metodoPagamento").value = '';
			  document.getElementById("importo").value = '';
			  document.getElementById("categoria").value = '';
			  document.getElementById("data").value = '';
			  
			  // Recupero i ruoli dal localStorage (assumendo che siano salvati come array JSON)
			  totaleSpese();
		  } else {
		      throw new Error("Errore durante l'aggiunta della spesa");
		  }
	      }).catch(error => { alert("Errore: " + error.message);});
          }
  
    function eliminaSpesa(id){
	  const token = localStorage.getItem("token");
	  const payload = parseJwt(token);
	  const ruolo = (payload.role || (payload.roles && payload.roles[0]) || "").toUpperCase();

	  console.log("ID della spesa da eliminare:", id);
	  
	  let url = "";
	  if(ruolo.includes("ADMIN")) {url=`http://localhost:8080/api/spese/admin/${id}`;}
	  else if (ruolo.includes("USER")) {url = `http://localhost:8080/api/spese/utente/${id}`;
      } else {
      console.error("Ruolo non riconosciuto:", ruolo);
      return;
	  }
	  fetch(url,{
		  method: "DELETE",
		  headers: {'Authorization': 'Bearer ' + token,
			  "Content-Type": "application/json"}
	  })
		  .then(risposta => {
			  if (risposta.ok) {
				  alert("Spesa eliminata!!")  

				  totaleSpese();
			  } else {
				  alert('Errore eliminazione spesa: ' + risposta.status);
				  risposta.text().then(text => { throw new Error("Errore nel eliminare la spesa: " + text) });
			  }
	  }).catch(errore => {  alert("Errore eliminazione spesa: " + errore.message) });
      }
	  function dopoModifica() {
	    document.getElementById("modifica-spesa").style.display = "none";

	    // Mostra di nuovo tutto
	    document.getElementById("aggiunta-spesa").style.display = "block";
	    document.getElementById("lista-spese").style.display = "block";
	    document.getElementById("lista-utenti").style.display = "block";
}
  
  function modificaSpesa(id,bottone){
	  const riga = bottone.closest("tr");
	  const metodoPagamento = riga.children[1].textContent;
	  const categoria = riga.children[2].textContent;
	  const importo = parseFloat(riga.children[3].textContent.replace(" €", ""));
	  const data = riga.children[4].textContent;	  
	  
	  // Riempie i campi del form modifica
      document.getElementById("modifica-id").value = id;
      document.getElementById("modifica-metodoPagamento").value = metodoPagamento;
	  document.getElementById("modifica-categoria").value = categoria;
      document.getElementById("modifica-importo").value = importo;
      document.getElementById("modifica-data").value = data;
	  
      // Mostra il form modifica, nasconde aggiunta
      document.getElementById("modifica-spesa").style.display = "block";
      document.getElementById("aggiunta-spesa").style.display = "none";
	  document.getElementById("lista-spese").style.display = "none";
	  document.getElementById("lista-utenti").style.display = "none";
      
  
  }
  function salvaModifica() {
	  const token = localStorage.getItem("token");

	  const id = document.getElementById("modifica-id").value;
	  const metodoPagamento = document.getElementById("modifica-metodoPagamento").value;
	  const importo = parseFloat(document.getElementById("modifica-importo").value);
	  const categoria = document.getElementById("modifica-categoria").value;
	  const data = document.getElementById("modifica-data").value;

	  const spesaModificata = {
	    metodoPagamento: metodoPagamento,
	    importo: importo,
	    categoria: categoria,
	    data: data
	  };

	  fetch(`http://localhost:8080/api/spese/${id}`, {
	    method: "PUT",
	    headers: {
	      "Authorization": "Bearer " + token,
	      "Content-Type": "application/json"
	    },
	    body: JSON.stringify(spesaModificata)
	  })
	  .then(response => {
	    if (!response.ok) {
	      return response.text().then(text => { throw new Error(text) });
	    }
	    return response.json();
	  })
		  .then(spesaAggiornata => {
			  alert("Spesa modificata con successo!");

			 totaleSpese();
			 dopoModifica();
			  document.getElementById("modifica-spesa").style.display = "none";
			  document.getElementById("aggiunta-spesa").style.display = "block";
		  })
		  .catch(err => alert("Errore durante la modifica: " + err.message));
  }

  function annullaModifica() {
	  document.getElementById("modifica-spesa").style.display = "none";
	  dopoModifica();
	}
  
  
    function filtraSpesa(){
	  const token = localStorage.getItem("token");
	  const utente = document.getElementById("utente-f").value;
	  const categoria = document.getElementById("categoria-f").value;
	  const dataInizio = document.getElementById("dataInizio").value;
	  const dataFine = document.getElementById("dataFine").value;

	 
		  let params = [];
		  if (utente) params.push(`utente=${encodeURIComponent(utente)}`);
		  if (categoria) params.push(`categoria=${encodeURIComponent(categoria)}`);
		  if (dataInizio) params.push(`a=${encodeURIComponent(dataInizio)}`);
		  if (dataFine) params.push(`b=${encodeURIComponent(dataFine)}`);

		  const url = "http://localhost:8080/api/spese/filtro" + (params.length ? "?" + params.join("&") : "");
		  
	  fetch(url,{
		method: "GET",
		headers: {"Authorization": "Bearer " + token,
				  "Content-Type": "application/json"}
	  })
	  .then(risposta => {
		if(risposta.ok)
		   return risposta.json();
	    else 
		return risposta.text().then(text => { throw new Error(text) });
	  })
	  .then(spese => {
		const tbody = document.getElementById("filtro-tbody");
			  tbody.innerHTML ="";
			  
			  if(spese.length === 0){
				alert("Nessuna spesa trovata!! Ops Ops Ops")
				return;
			  }
			  
			  let totale = 0;
			  
			    spese.forEach(spesa => {
				const riga = document.createElement("tr");
				riga.innerHTML = 
				   `<td>${spesa.usernameUtente || "-"}</td>
				    <td>${spesa.metodoPagamento || "-"}</td>
				    <td>${spesa.categoria}</td>
				    <td>${spesa.importo.toFixed(2)}€</td>
				    <td>${spesa.data}</td>
				    `
			   tbody.appendChild(riga);
			   totale += spesa.importo;
			   });
			   const totaleElemento = document.getElementById("totale-spese-filtrate");
			       totaleElemento.textContent = `${totale.toFixed(2)}€`; 
	  })
	  .catch(err => alert("Errore nel filtrare le spese: " + err.message)); }
  
	  
	  
	  function totaleSpese(){
		const token = localStorage.getItem("token");

		fetch("http://localhost:8080/api/spese/totaleSpese",{
			method: "GET",
			headers: {
				"Authorization": "Bearer " + token,
				"Content-Type": "application/json"}
		})
		.then(risposta => {
			if(risposta.ok)
				return risposta.json();
			else 
				return risposta.text().then(text => { throw new Error(text) });
         })
		 .then(totale => {
			const totaleElement = document.getElementById("totale-spese");

			if (totaleElement) {
			            totaleElement.textContent = ` €${totale.toFixed(2)}`;
			        } else {
			            console.error("Elemento totale-spese non trovato");
			        }
			const ruoli = JSON.parse(localStorage.getItem("ruoli") || "[]");
					  if (ruoli.includes("ADMIN")) {
						  trovaTutteSpese();
					  } else {
						  caricaSpese();
					  }
		 })
		 .catch(err => alert("Errore durante calcolo del totale: " + err.message));
         }
		 
		 document.addEventListener("DOMContentLoaded", () => {
			const token = localStorage.getItem("token");
			if (isTokenValido(token)) {
					document.getElementById("lista-spese").style.display = "block";
					totaleSpese();
				} else {
					localStorage.removeItem("token");
					localStorage.removeItem("ruoli");
				}
			});
			

			function aggiornaNumeroPagina() {
			  const numeroPagina = document.getElementById("numero-pagina");
			  numeroPagina.innerHTML = `Pagina ${paginaCorrente + 1} di ${totalePagine}`;
			}
			
			function paginaPrecedente() {
			    const ruoli = JSON.parse(localStorage.getItem("ruoli") || "[]");
			    if (paginaCorrente > 0) {
			        if (ruoli.includes("ADMIN")) {
						paginaCorrente--;
			            trovaTutteSpese(paginaCorrente);
						aggiornaNumeroPagina()
			        } else {
						paginaCorrente--;
			            caricaSpese(paginaCorrente);
						aggiornaNumeroPagina()
			        }
			    }
			}

			function paginaSuccessiva() {
			    const ruoli = JSON.parse(localStorage.getItem("ruoli") || "[]");
			    if (paginaCorrente < totalePagine - 1) {
			        if (ruoli.includes("ADMIN")) {
						paginaCorrente++;
			            trovaTutteSpese(paginaCorrente);
						aggiornaNumeroPagina()
			        } else {
						paginaCorrente++;
			            caricaSpese(paginaCorrente);
						aggiornaNumeroPagina()
			        }
			    }
			}
			  
		/*	SE VUOI NASCONDERE O MOSTRARE ID ANNIDATI 
				function mostraSolo(id) {
			  const blocchi = ["aggiunta-spesa", "lista-spese", "lista-utenti", "modifica-spesa", "admin-spese"];
			  blocchi.forEach(b => document.getElementById(b).style.display = "none");
			  document.getElementById(id).style.display = "block";
			} */	
			
			
			

			      