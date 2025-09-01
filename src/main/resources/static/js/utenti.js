/*
METODI LOGIN,LOGOUT,TROVATUTTIUTENTI
*/
function login(){
	const username = document.getElementById("username").value;
	const password = document.getElementById("password").value;
	const utente = {
		      username: username,
		      password: password
		    };
	fetch('http://localhost:8080/auth/login', {
	      method: 'POST',
	      headers: { 'Content-Type': 'application/json' },
	      body: JSON.stringify(utente)
	    })
    .then(risposta => {
    	if(risposta.ok){
    	  return risposta.json();
    	}else{
          return risposta.text().then(text => alert('Errore 1 login: ' + text));
    	} })
        .then(data => {
        	if (!data || !data.token) {
        	    alert('Token non ricevuto! Login fallito.');
        	    return;
        	  }
    document.body.classList.add("logged-in");
			  
    localStorage.setItem('token', data.token);
    alert('Login effettuato con successo!');
	
	//DOPO LOGIN SVUOTA SEMPRE TABELLA DI RICERCA/FILTRI SPESE
	const tbodyFiltro = document.getElementById("filtro-tbody");
	if (tbodyFiltro) tbodyFiltro.innerHTML = "";
	
	const totaleFiltro = document.getElementById("totale-spese-filtrate");
	if (totaleFiltro) totaleFiltro.textContent = "€0.00";
	
	const filtroForm = document.getElementById("filtro-form");
	const inputs = filtroForm.querySelectorAll('input[type="text"], input[type="date"]');
	inputs.forEach(input => input.value = "");
	
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    //DOPO IL LOGIN
    document.getElementById('login-form').style.display = 'none';//NASCONDO FORM LOGIN
    document.getElementById('logout-form').style.display = 'block';//COMPARE BOTTONE LOGOUT
	document.getElementById("lista-spese").style.display = "block";
	document.getElementById("filtro-form").style.display = "block";
	document.getElementById("aggiunta-spesa").style.display = "block";
    document.getElementById('utente-loggato').textContent = username;
    //SOLO SE ADMIN(estraggo ruolo dal token JWT)
const payload = parseJwt(data.token);

 let ruoli = [];

// Nel payload JWT il ruolo è stringa singola, la metto in array per uniformità
   if (payload.role) {
    ruoli = [payload.role];
   } else if (payload.roles) {
    ruoli = payload.roles;
    }
   console.log('Ruoli:', ruoli);

    if (ruoli.includes("ADMIN")) {
    //document.getElementById("admin-spese").style.display = "block";
	document.getElementById("lista-utenti").style.display = "block"; 
	document.getElementById("utente-f-div").style.display = "block";
	document.getElementById("aggiunta-spesa").style.display = "none";
 // <-- MOSTRA LA LISTA UTENTI
	trovaTuttiUtenti();
	//trovaTutteSpese();
    }else {
	document.getElementById("aggiunta-spesa").style.display = "block";
    //document.getElementById("admin-spese").style.display = "none";
	document.getElementById("lista-utenti").style.display = "none";
	document.getElementById("utente-f-div").style.display = "none";

	caricaSpese(0);
    }
	localStorage.setItem("ruoli", JSON.stringify(ruoli));

	totaleSpese();
    
    })
    .catch(errore => {alert("Errore 2 login: "+errore.message); });
	 }
	 
	 
	 function logout() {
	 	   	  //RIMUOVE TOKEN SALVATO
	 	   	  localStorage.removeItem('token');
	 		  // Rimuovo la classe "logged-in" così lo sfondo torna normale
	 		  document.body.classList.remove("logged-in");
	 	   	  //MOSTRA LOGIN
	 	   	  document.getElementById('login-form').style.display = 'block';
	 	   	  //NASCONDE IL RESTO 
	 	   	  document.getElementById('logout-form').style.display = 'none';
	 	   	  document.getElementById('aggiunta-spesa').style.display = 'none';
	 	   	  document.getElementById('modifica-spesa').style.display = 'none';
	 	   	  document.getElementById('lista-spese').style.display = 'none';
	 	   	  document.getElementById("lista-utenti").style.display = "none";
	 	   	 // document.getElementById("admin-spese").style.display = "none";
	 	   	  document.getElementById("filtro-form").style.display = "none";
	 	   	  document.getElementById('utente-loggato').textContent = '';
	 	   	  // PULISCO CONTENUTO TABELLE
	 	   	  document.getElementById('utente-loggato').textContent = '';
	 	   	  document.getElementById('spese-tbody').innerHTML = '';
	 	   	  document.getElementById('utenti-tbody').innerHTML = '';
	 	   	}
	 		
	 		
	         //PERMETTE RICARICA PAGINA IN AUTOMATICO DOPO LOGIN  
	 		window.onload = function () {
	 		  const token = localStorage.getItem("token");
	 		  if (token) {
	 		    const payload = parseJwt(token);
	 		    const username = payload.sub;
	 		    const ruoli = [payload.role || (payload.roles || [])];

	 		    document.body.classList.add("logged-in");
	 		    document.getElementById("login-form").style.display = "none";
	 		    document.getElementById("logout-form").style.display = "block";
	 		    document.getElementById("aggiunta-spesa").style.display = "block";
	 		    document.getElementById("lista-spese").style.display = "block";
	 		    document.getElementById("filtro-form").style.display = "block";
	 		    document.getElementById("utente-loggato").textContent = username;

	 		    if (ruoli.includes("ADMIN")) {
	 		     // document.getElementById("admin-spese").style.display = "block";
	 		      document.getElementById("lista-utenti").style.display = "block";
	 		      document.getElementById("utente-f-div").style.display = "block";
	 		      trovaTuttiUtenti();
	 		    }

	 		    totaleSpese();
	 		  }
	 		};
	 
	 
	 
	 function eliminaUtente(id){
		const token = localStorage.getItem("token");
		
		fetch(`http://localhost:8080/api/utenti/elimina/${id}`,{
			method:"DELETE",
			headers:{'Authorization': 'Bearer ' + token,
				     "Content-Type": "application/json"}
		})
		.then(risposta =>{
			if(risposta.ok){
			  alert("Utente eliminato con successo.");
              trovaTuttiUtenti();
			  totaleSpese();
			} else {
			  return risposta.text().then(errore => {throw new Error ("Errore nel eliminare Utente!" + errore)});
			  }
		})
		.catch(errore => {errore.message});
	    }
	 
	 
	 function trovaTuttiUtenti(){
	  const token = localStorage.getItem("token");
	 	  
	 	  fetch("http://localhost:8080/api/utenti/tutti",{
	 		  method: "GET",
	 		  headers: {"Authorization": "Bearer " + token,
	 		            "Content-Type": "application/json"}
	 	  })
	 	  .then(risposta => {
	 		  if(risposta.ok) return risposta.json();
	 		  else return risposta.text().then(text => { throw new Error(text) });
	 	  })
	 	  .then(utenti => {
	 		  document.getElementById("lista-utenti").style.display = "block";
	 		  
	 		  const tbody = document.getElementById("utenti-tbody");
	 		  if (!tbody) { console.error("Elemento utenti-tbody non trovato");
	 		  		return;
	 		  	    }
	 		  tbody.innerHTML="";
	 		  
	 		  utenti.forEach(utente => {  
	 		  const ruoli = utente.ruolo;
	 		  const riga = document.createElement("tr");
	 		  riga.innerHTML = `
	 			   <td>${utente.id}</td>
	 		       <td>${utente.username}</td>
	 		       <td>${utente.email || "-"}</td>
	 		       <td>${ruoli}</td> 
				   <td>
				   <button class="btn btn-elimina" onclick="eliminaUtente(${utente.id})">
				     <span class="testo">ELIMINA</span>
				     <svg class="icona" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" >
				       <polyline points="3 6 5 6 21 6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				       <path d="M19 6l-1 14H6L5 6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				       <path d="M10 11v6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				       <path d="M14 11v6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				       <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
				     </svg>
				   </button>
				   </td>`
				   ;
	 		   
	 		  tbody.appendChild(riga);
	 	  });
	 	  })
	 	  .catch(err => alert("Errore durante la richiesta degli utenti: " + err.message));
	   }
	   
	  
	    function parseJwt(token) {
	     const base64Payload = token.split('.')[1];
	     const payload = atob(base64Payload);
	     return JSON.parse(payload);
	   }

	   function isTokenValido(token) {
	     	if (!token) return false;
	     	try {
	     		const payload = JSON.parse(atob(token.split('.')[1]));
	     		const now = Math.floor(Date.now() / 1000);
	     		return payload.exp && payload.exp > now;
	     	} catch (e) {
	     		return false;
	     	}
	     } 
	   
	     
