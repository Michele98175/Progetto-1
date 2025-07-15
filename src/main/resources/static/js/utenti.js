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
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    //DOPO IL LOGIN
    document.getElementById('login-form').style.display = 'none';//NASCONDO FORM LOGIN
    document.getElementById('logout-form').style.display = 'block';//COMPARE BOTTONE LOGOUT
    document.getElementById("aggiunta-spesa").style.display = "block";
	document.getElementById("lista-spese").style.display = "block";
	document.getElementById("filtro-form").style.display = "block";
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
    document.getElementById("admin-spese").style.display = "block";
	document.getElementById("lista-utenti").style.display = "block"; 
	document.getElementById("utente-f-div").style.display = "block";
 // <-- MOSTRA LA LISTA UTENTI
	trovaTuttiUtenti();
	//trovaTutteSpese();
    }else {
    document.getElementById("admin-spese").style.display = "none";
	document.getElementById("lista-utenti").style.display = "none";
	document.getElementById("utente-f-div").style.display = "none";

	caricaSpese(0);
    }
	localStorage.setItem("ruoli", JSON.stringify(ruoli));

	totaleSpese();
    
    })
    .catch(errore => {alert("Errore 2 login: "+errore.message); });
	 }
	 
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
				   <button class="btn btn-elimina" onclick="eliminaUtente(${utente.id})">Elimina</button>
				   </td>`
				   ;
	 		   
	 		  tbody.appendChild(riga);
	 	  });
	 	  })
	 	  .catch(err => alert("Errore durante la richiesta degli utenti: " + err.message));
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
	   	  document.getElementById("admin-spese").style.display = "none";
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
		      document.getElementById("admin-spese").style.display = "block";
		      document.getElementById("lista-utenti").style.display = "block";
		      document.getElementById("utente-f-div").style.display = "block";
		      trovaTuttiUtenti();
		    }

		    totaleSpese();
		  }
		};
	     
