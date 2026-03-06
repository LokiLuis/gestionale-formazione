# Relazione Tecnica di Progetto
## Applicazione Web per la Gestione di Anagrafiche e Attività Formative

---

## Indice

1. [Obiettivo del Progetto](#1-obiettivo-del-progetto)
2. [Architettura e Tecnologie Scelte](#2-architettura-e-tecnologie-scelte)
3. [Moduli Sviluppati e Divisione del Lavoro](#3-moduli-sviluppati-e-divisione-del-lavoro)
4. [Modalità di Lavoro e Organizzazione del Team](#4-modalità-di-lavoro-e-organizzazione-del-team)
5. [Schema del Database (Modello E/R)](#5-schema-del-database-modello-er)
6. [Istruzioni di Avvio (Deployment)](#6-istruzioni-di-avvio-deployment)
7. [Note Finali e Raccomandazioni](#7-note-finali-e-raccomandazioni)



---


(Cliccare sull' immagine per poter guardare il risultato del progetto su youtube.)
[![Testo Alternativo Video](https://img.youtube.com/vi/ipWxiLdLngQ/0.jpg)](https://www.youtube.com/watch?v=ipWxiLdLngQ)


## 1. Obiettivo del Progetto

Il presente documento descrive l'attività di sviluppo svolta dal team per la realizzazione di una **applicazione web per la gestione di anagrafiche per utenti in formazione e posizioni lavorative**.

Il sistema consente di:

- Gestire l'anagrafica completa degli utenti, comprensiva di dati identificativi (nome, cognome, codice fiscale), recapiti (email, telefono) e posizione lavorativa, con ruoli differenziati (`ADMIN`, `FORMATORE`, `USER`).
- Creare, modificare ed eliminare attività formative (corsi), con associazione di un formatore responsabile.
- Gestire le iscrizioni degli utenti ai corsi tramite una relazione Molti-a-Molti.
- Proteggere tutte le aree applicative tramite un sistema di autenticazione basato su ruoli, garantendo che ogni profilo acceda esclusivamente alle funzionalità di propria competenza.




---

## 2. Architettura e Tecnologie Scelte

Il team ha adottato uno stack tecnologico **Java-based**, moderno e consolidato in ambito enterprise, che garantisce robustezza, manutenibilità.

### 2.1 Stack Tecnologico

| Layer          | Tecnologia                          | Versione  | Motivazione                                                                 |
|----------------|-------------------------------------|-----------|-----------------------------------------------------------------------------|
| **Backend**    | Java + Spring Boot                  | 3.5.10    | Framework enterprise standard, auto-configurazione, ampia comunità          |
| **Frontend**   | Thymeleaf + HTML/CSS                | 3.x       | Template engine server-side nativo di Spring, nessun frontend framework separato |
| **Database**   | PostgreSQL                          | Latest    | RDBMS open-source affidabile, supporto completo a vincoli e relazioni complesse |
| **ORM**        | Spring Data JPA + Hibernate         | —         | Astrazione del livello di persistenza, generazione automatica delle query    |
| **Sicurezza**  | Spring Security 6                   | 6.x       | Modulo di sicurezza standard Spring, RBAC nativo, gestione sessioni          |
| **Build Tool** | Apache Maven                        | —         | Gestione dipendenze e build lifecycle standardizzati                         |
| **Utility**    | Lombok                              | —         | Riduzione del boilerplate Java (getter, setter, costruttori)                 |
| **Container**  | Docker + Docker Compose             | —         | Portabilità dell'ambiente, deployment riproducibile                          |

### 2.2 Pattern Architetturale

L'applicazione adotta il pattern **MVC (Model-View-Controller)** implementato nativamente da Spring Boot:

- **Model** — Entità JPA (`User`, `Activity`, `Role`) mappate sulle tabelle PostgreSQL.
- **View** — Template Thymeleaf lato server, con integrazione dell'extension `thymeleaf-extras-springsecurity6` per il controllo dei ruoli direttamente nelle pagine HTML (`sec:authorize`).
- **Controller** — Controller Spring MVC che orchestrano le richieste HTTP, delegano la logica ai Service e restituiscono le View.

È stato inoltre applicato il pattern a **strati (Layered Architecture)**:

```
Controller  →  Service  →  Repository  →  Database
    ↕               ↕
   DTO           Entity/Model
```

L'uso di **DTO (Data Transfer Object)** separa il modello di dominio interno dall'oggetto esposto alle view, prevenendo l'esposizione accidentale di dati sensibili (es. hash della password).




---

### 2.3 Sicurezza

La sicurezza è gestita interamente da **Spring Security 6** con le seguenti misure:

- **Autenticazione Form-based**: pagina di login personalizzata con gestione della sessione HTTP.
- **Autorizzazione basata su Ruoli (RBAC)**: la classe `SecurityConfig` definisce le regole di accesso per ogni rotta dell'applicazione in modo centralizzato e dichiarativo.
- **Hashing delle password con BCrypt**: nessuna password viene salvata in chiaro nel database. L'algoritmo BCrypt con salt incorporato protegge dagli attacchi a dizionario e rainbow table.
- **Protezione delle rotte per ruolo**:
  - `/utenti/**`, `/admin/**` → solo `ROLE_ADMIN`
  - `/corsi/nuovo`, `/corsi/modifica/**` → solo `ROLE_ADMIN`
  - `/corsi/*/iscritti` → `ROLE_ADMIN` o `ROLE_FORMATORE`
  - `/formatore/**` → `ROLE_ADMIN` o `ROLE_FORMATORE`
  - Qualsiasi altra rotta → utente autenticato

### 2.4 Testing, Logging e Gestione Errori

Il progetto adotta diverse pratiche per garantire affidabilità, stabilità e facilità di risoluzione dei problemi:

- **Test Unitari (JUnit 5 & Mockito):** La logica di business principale, situata nei livelli service (`UserService`, `ActivityService`), è testata tramite JUnit. I test isolano le dipendenze esterne (come il database) sfruttando Mockito, garantendo ad esempio che regole come l'univocità delle email o l'assegnazione dei ruoli funzionino correttamente.
- **Testing degli Endpoint (Postman):** Assieme al codice sorgente viene fornita una collection Postman (`postman_collection.json`) pronta all'uso, utile per saggiare in modo programmatico le procedure di login form-based e le risposte delle rotte protette.
- **Gestione Errori Centralizzata (Global Exception Handling):** Le eccezioni applicative (come `404 Not Found`, tentativi di inserire dati duplicati con `DuplicateResourceException`, o accesso negato `403 Forbidden`) non generano logiche sparse. Sono invece catturate globalmente dalla classe `GlobalExceptionHandler` (annotata con `@ControllerAdvice`), che reindirizza l'utente a una vista gradevole (`error.html`) evitando l'esposizione di stack trace malevoli o interfacce di default (Whitelabel Error).
- **Logging (SLF4J):** Spring Boot gestisce i log applicativi che sono stati configurati per aiutare a tracciare passaggi critici ed eventuali eccezioni in console, rendendo immediato il debug.



---

## 3. Moduli Sviluppati e Divisione del Lavoro

Il progetto è stato suddiviso in **3 moduli verticali** indipendenti, uno per sviluppatore, per minimizzare i conflitti sul codice e massimizzare la produttività parallela.

---

### Modulo A — Security & Autenticazione
**Sviluppatore assegnato:** Luis Dragos Istrate

Questo modulo costituisce la **fondamenta trasversale** dell'intera applicazione. Nessun altro modulo è accessibile senza che questo sia funzionante.

**Componenti realizzati:**

- `SecurityConfig.java` — Configurazione centralizzata di Spring Security: definizione delle rotte pubbliche, protette per ruolo e autenticate. Configurazione del form di login e logout.
- `CustomUserDetailsService.java` — Implementazione di `UserDetailsService` per integrare il sistema di autenticazione Spring con il database applicativo.
- `DataInitializer.java` — Componente di inizializzazione che popola il database con i ruoli di sistema (`ROLE_ADMIN`, `ROLE_FORMATORE`, `ROLE_USER`) e un utente amministratore di default al primo avvio.
- `AuthController.java` — Gestione delle rotte `/login` e `/registrazione`.
- Template: `login.html`, `registrazione.html`.
- Modello: `Role.java` (entità ruolo con relazione `ManyToOne` verso `User`: ogni utente ha esattamente un ruolo assegnato).

**Funzionalità consegnate:**
- [x] Login sicuro con hashing BCrypt
- [x] Logout
- [x] Registrazione nuovo utente
- [x] Protezione differenziata delle rotte per ruolo
- [x] Visualizzazione condizionale degli elementi UI in base al ruolo (`sec:authorize`)



---

### Modulo B — Anagrafica Utenti
**Sviluppatore assegnato:** Gaetano Rocchetti

Questo modulo gestisce il ciclo di vita completo degli utenti nel sistema (operazioni CRUD), accessibile esclusivamente agli amministratori.

**Componenti realizzati:**

- `User.java` — Entità JPA principale con i seguenti campi:
  - Identificativi: `id` (PK auto-generata), `username` (univoco, obbligatorio)
  - Anagrafici: `nome`, `cognome`, `codiceFiscale` (univoco)
  - Recapiti: `email` (univoca), `telefono` (univoco)
  - Professionale: `posizioneLavorativa`
  - Sicurezza: `password` (salvata come hash BCrypt, mai in chiaro)
  - Relazioni: `role` (ManyToOne → un ruolo per utente), `activities` (ManyToMany → corsi frequentati)

  I campi opzionali (`codiceFiscale`, `email`, `telefono`) implementano una logica di normalizzazione tramite callback JPA `@PrePersist`/`@PreUpdate` che converte le stringhe vuote in `null`, prevenendo violazioni del vincolo `UNIQUE` su PostgreSQL.
- `UserRepository.java` — Interfaccia Spring Data JPA per le query sul database utenti.
- `UserService.java` — Logica di business: creazione utente con hashing automatico della password, aggiornamento, eliminazione, ricerca per email.
- `UtenteController.java` — Controller per la gestione delle rotte `/utenti/**`.
- `UserDTO.java`, `UtenteFormDTO.java`, `DtoMapper.java` — Oggetti di trasferimento dati per disaccoppiare il modello interno dalla view.
- Template: `anagrafiche-utenti.html`, `form-utente.html`.

**Funzionalità consegnate:**
- [x] Lista completa degli utenti (solo ADMIN)
- [x] Creazione nuovo utente con assegnazione ruolo
- [x] Modifica dati utente
- [x] Eliminazione utente
- [x] Validazione dati con Bean Validation (`@NotBlank`, `@Email`)
- [x] Gestione eccezioni centralizzata (`DuplicateResourceException` per email, telefono, codice fiscale duplicati; `ResourceNotFoundException` per risorse non trovate; `GlobalExceptionHandler` centralizzato)



---

### Modulo C — Attività Formative (Corsi)
**Sviluppatore assegnato:** Claudio Monaco Carmelo

Questo modulo gestisce le attività formative, la loro assegnazione ai formatori e le iscrizioni degli utenti.

**Componenti realizzati:**

- `Activity.java` — Entità JPA con campi: `id`, `nome`, `descrizione`, `dataInizio`, `dataFine`, relazione `ManyToOne` verso il formatore (`User`) e relazione `ManyToMany` (inversa) verso gli utenti iscritti.
- `ActivityRepository.java` — Interfaccia Spring Data JPA per le query sulle attività.
- `ActivityService.java` — Logica di business: CRUD corsi, gestione iscrizioni, callback `@PreRemove` per la pulizia delle relazioni prima dell'eliminazione.
- `CorsoController.java` — Controller per le rotte `/corsi/**`, `/formatore/**`, `/attivita/**`.
- `ActivityDTO.java` — DTO per l'esposizione sicura dei dati del corso.
- Template: `corsi.html`, `form-corso.html`, `dettaglio-corso.html`, `gestione-iscrizioni.html`, `formatore-corsi.html`, `mie-attivita.html`.

**Funzionalità consegnate:**
- [x] Lista e dettaglio corsi
- [x] Creazione e modifica corso con assegnazione formatore (solo ADMIN)
- [x] Eliminazione corso con pulizia automatica delle relazioni (solo ADMIN)
- [x] Gestione iscrizioni utenti al corso (solo ADMIN)
- [x] Dashboard formatore: visualizzazione dei propri corsi e dei relativi iscritti
- [x] Vista "Le mie attività" per l'utente standard



---

## 4. Modalità di Lavoro e Organizzazione del Team

### 4.1 Metodologia

Il team ha adottato una metodologia di lavoro **ispirata all'Agile**, adattata al contesto di un progetto a tempistiche ristrette. L'obiettivo era massimizzare la produttività individuale riducendo al minimo le dipendenze bloccanti tra sviluppatori.

### 4.2 Fasi del Progetto

| Fase | Giorni | Attività |
|------|--------|----------|
| **Fase 1 — Design Congiunto** | Giorno 1–2 | Analisi dei requisiti, progettazione dello Schema E/R, definizione delle API interne e delle interfacce tra moduli, setup dell'ambiente di sviluppo condiviso |
| **Fase 2 — Sviluppo Parallelo** | Giorno 3–8 | Sviluppo indipendente dei 3 moduli verticali assegnati |
| **Fase 3 — Integrazione e Testing** | Giorno 9–10 | Sessioni di integrazione congiunta, test funzionali end-to-end, correzione bug di integrazione, packaging finale |

### 4.3 Coordinamento

- **Daily meeting**: ogni mattina, il team si allineava sullo stato di avanzamento di ciascun modulo, segnalando eventuali blocchi e coordinando le dipendenze (es. il Modulo B e C dipendono dalla struttura dell'entità `User` definita nel Modulo A).
- **Contratto di interfaccia preliminare**: prima di procedere allo sviluppo parallelo, il team ha definito e condiviso la struttura delle entità JPA (`User`, `Role`, `Activity`) e le loro relazioni. Questo ha consentito a ciascuno sviluppatore di lavorare in isolamento senza sorprese durante l'integrazione.
- **Gestione dei conflitti minimizzata**: la divisione verticale per modulo ha garantito che ogni sviluppatore operasse su package Java e template Thymeleaf distinti, rendendo i merge quasi privi di conflitti.

### 4.4 Strumenti di Supporto

- **IDE**: IntelliJ IDEA.



---

## 5. Schema del Database (Modello Entity-Relationship)

Di seguito la rappresentazione semplificata delle entità e delle loro relazioni:

```
┌──────────────────────┐      ┌──────────────────┐      ┌───────────────────┐
│        users         │      │  user_activities  │     │    activities     │
├──────────────────────┤      │  (tabella ponte) │      ├───────────────────┤
│ id           (PK)    │◄─────┤ user_id     (FK) │      │ id          (PK)  │
│ username     UNIQUE  │      │ activity_id (FK) ├─────►│ nome              │
│ password             │      └──────────────────┘      │ descrizione       │
│ nome                 │                                 │ data_inizio       │
│ cognome              │◄────────────────────────────────│ data_fine         │
│ email        UNIQUE  │   ManyToOne (formatore_id FK)   │ formatore_id (FK) │
│ telefono     UNIQUE  │                                 └───────────────────┘
│ codice_fiscale UNIQUE│
│ posizione_lavorativa │
│ role_id      (FK)────┼──────┐  ManyToOne (un ruolo per utente)
└──────────────────────┘      │
                              ▼
                       ┌─────────────┐
                       │    roles    │
                       ├─────────────┤
                       │ id    (PK)  │
                       │ nome        │
                       └─────────────┘
```

**Relazioni principali:**
- `User` → `Role`: **ManyToOne** — ogni utente ha esattamente un ruolo assegnato (FK `role_id` nella tabella `users`). Un ruolo può essere assegnato a più utenti.
- `User` ↔ `Activity` (iscritti): **ManyToMany** — un utente può essere iscritto a più corsi, un corso può avere più utenti iscritti. Gestita dalla tabella ponte `user_activities`.
- `Activity` → `User` (formatore): **ManyToOne** — ogni corso ha un unico formatore responsabile (FK `formatore_id` nella tabella `activities`).

**Legenda tecnica:**
- **PK (Primary Key)**: Chiave Primaria. Identificativo unico e immutabile di ogni riga della tabella (es. l'ID).
- **FK (Foreign Key)**: Chiave Esterna. Rappresenta un legame verso un'altra tabella, indicando la Chiave Primaria del record associato.
- **
UK (Unique Key)**: Chiave Univoca. Garantisce che tutti i valori in una determinata colonna siano univoci e non si ripetano mai (es. email o username).


---

## 6. Istruzioni di Avvio (Deployment)

Il progetto supporta due modalità di avvio. Per la procedura più veloce e consigliata tramite **Docker**, consultare la guida rapida nel file:
👉 **[README.md](README.md)**

---

---

### Opzione A — Avvio con Docker (Consigliata)

Questa modalità avvia automaticamente sia il database PostgreSQL sia l'applicazione, senza alcuna configurazione manuale del database.

**Prerequisiti:** Docker Desktop installato e avviato.

```bash
# 1. Posizionarsi nella cartella radice del progetto
cd /percorso/del/progetto

# 2. Costruire l'immagine ed avviare tutti i servizi
docker-compose up --build
```

L'applicazione sarà disponibile all'indirizzo: **http://localhost:8081**


**Credenziali di accesso di default** (create dal `DataInitializer` al primo avvio):

| Ruolo   | Username | Email                 | Password |
|---------|----------|-----------------------|----------|
| `ADMIN` | `admin`  | admin@gestionale.it   | admin123 |


Per fermare i servizi:
```bash
docker-compose down
```










---

### Opzione B — Avvio in Locale (senza Docker)

**Prerequisiti:**
- Java 17+ installato (`java -version`)
- Apache Maven installato (`mvn -version`)
- Un'istanza PostgreSQL in esecuzione sulla propria macchina

**Step 1 — Configurare il Database**

Creare un database PostgreSQL vuoto:
```sql
CREATE DATABASE gestione_formazione;
```

**Step 2 — Configurare le credenziali**

Aprire il file `src/main/resources/application.properties` e verificare o modificare le seguenti proprietà in base alla propria installazione locale:

```properties
# URL del database (porta 5443 di default nel progetto, modificare se necessario)
spring.datasource.url=jdbc:postgresql://localhost:5443/gestione_formazione

# Credenziali PostgreSQL locali
spring.datasource.username=postgres
spring.datasource.password=root
```

> **Nota:** Hibernate creerà automaticamente le tabelle al primo avvio grazie alla configurazione `ddl-auto=update`. Non è necessario eseguire script SQL manualmente.

**Step 3 — Avviare l'applicazione**

```bash
# Dalla cartella radice del progetto (dove si trova il file pom.xml)
mvn spring-boot:run
```

L'applicazione sarà disponibile all'indirizzo: **http://localhost:8081**

**Credenziali di accesso di default** (create dal `DataInitializer` al primo avvio):

| Ruolo   | Username | Email                 | Password |
|---------|----------|-----------------------|----------|
| `ADMIN` | `admin`  | admin@gestionale.it   | admin123 |

---

## 7. Note Finali e Raccomandazioni

Il sistema è stato completato nei tempi stabiliti e copre tutti i requisiti funzionali definiti in fase di analisi. Di seguito alcune raccomandazioni per eventuali sviluppi futuri o per il passaggio in produzione:

- **Variabili d'ambiente**: in un ambiente di produzione, rimuovere i valori di fallback da `application.properties` e gestire tutte le credenziali tramite variabili d'ambiente o un secret manager dedicato.
- **HTTPS**: abilitare la comunicazione cifrata TLS/SSL prima di qualsiasi rilascio in produzione.

---

*Documento redatto dal Team di Sviluppo — Marzo 2026*
