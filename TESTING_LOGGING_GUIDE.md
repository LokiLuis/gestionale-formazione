# Guida al Testing e al Debugging del Progetto

Questa guida fornisce le istruzioni dettagliate per soddisfare e valutare i requisiti di consegna relativi ai Test Unitari, ai Test degli Endpoint con Postman e alla gestione del Logging e degli Errori.

---

## 1. Test Unitari (JUnit & Mockito)

Sono stati implementati i test unitari per garantire il corretto funzionamento della logica di business principale (il livello `Service`), isolandola dal database reale tramite **Mockito**.

### Dove trovare i Test:
- `src/test/java/com/webapp/javawebapp/service/UserServiceTest.java` (Testa la ricerca, la validazione email duplicate e le iscrizioni).
- `src/test/java/com/webapp/javawebapp/service/ActivityServiceTest.java` (Testa il recupero dati sulle attività).

### Come eseguire i Test:
Puoi eseguire i test in due modi:
1. **Tramite IDE:** Apri i file di test su IntelliJ IDEA o Eclipse e premi il tasto "Play" verde (`Run Tests`).
2. **Tramite Terminale (Maven):** Apri un terminale nella cartella principale del progetto ed esegui:
   ```bash
   mvn test
   ```
   *(oppure `.\mvnw.cmd test` se non hai Maven configurato sulle variabili d'ambiente di Windows).*

---

## 2. Test Endpoint (Postman)

Il progetto utilizza Spring MVC con Thymeleaf (restituisce pagine HTML invece di risposte JSON pure), ma è comunque possibile testare i flussi HTTP usando **Postman**.

### Come importare ed usare la Collection
1. Apri Postman e clicca su **Import**.
2. Trascina all'interno il file **`postman_collection.json`** situato nella cartella principale di questo progetto.
3. La collection contiene 3 cartelle:
   - **Pubblico:** Chiamate `GET` libere (es: Homepage, Registrazione).
   - **Autenticazione:** Chiamata `POST` verso `/login` simulando l'invio del form (username: `admin`, password: `admin123`).
   - **Endpoint Protetti:** Chiamate `GET` (es. Lista Utenti, Gestione Corsi) che funzionano *solo se* hai prima eseguito correttamente la chiamata di Login. Postman in automatico salva il cookie `JSESSIONID` per le chiamate successive.

---

## 3. Logging e Gestione Errori (Exception Handling)

### Logging (SLF4J + Logback)
Spring Boot integra nativamente SLF4J. Nel progetto, i log vengono stampati in console automaticamente durante l'avvio e le operazioni di Hibernate (query SQL). 
Per visionare i log in ambiente **Docker**, usa il comando:
```bash
docker compose logs -f
```

### Gestione Centralizzata degli Errori
Per evitare che l'utente veda la spaventosa pagina bianca di errore generica di Tomcat (il famoso *Whitelabel Error Page*), il progetto implementa una gestione elegante e centralizzata tramite la classe `GlobalExceptionHandler`.

**Dove si trova:** `src/main/java/com/webapp/javawebapp/config/GlobalExceptionHandler.java`

**Come funziona:**
Utilizza l'annotazione `@ControllerAdvice`. Intercetta qualsiasi eccezione "volante" scaturita dai Controller e la reindirizza verso un template grafico curato (`error.html`).

Alcuni esempi di errori intercettati:
- **`ResourceNotFoundException`:** Se cerchi un ID inesistente (Lancia un Dettaglio Errore "404").
- **`DuplicateResourceException`:** Se provi a registrare uno username già esistente nel database.
- **`NoHandlerFoundException`:** Se l'utente digita un URL completamente inventato (es: `/pagina-che-non-esiste`).
- **`AccessDeniedException`:** Intercetta gli errori di Spring Security ("403"), quando ad esempio un corsista base tenta graficamente di forzare via URL l'ingresso in un pannello dedicato esclusivamente ai Formatori o agli Admin.
