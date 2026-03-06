# 🚀 Gestionale Anagrafiche e Attività Formative - Guida Completa

Benvenuto! Questo progetto permette di gestire anagrafiche utenti e attività formative. Segui questa guida per avviare l'applicazione in pochi minuti.

[![Testo Alternativo Video](https://img.youtube.com/vi/ipWxiLdLngQ/0.jpg)](https://www.youtube.com/watch?v=ipWxiLdLngQ)

---

## 🛠️ Prerequisiti
Assicurati di avere **Docker Desktop** (o Docker Engine) installato e avviato sul tuo computer.

---

## 🏃 Avvio Rapido (Modalità Docker)

Il progetto è già configurato per essere eseguito interamente su Docker, includendo sia il database PostgreSQL che l'applicazione Spring Boot.

1. **Apri il terminale** nella cartella principale del progetto.
2. **Esegui il comando**:
   ```bash
   docker-compose up -d --build
   ```
   *(Nota: Se usi una versione recente di Docker, il comando è `docker compose up -d --build`).*
3. **Attendi il caricamento**: Docker scaricherà le immagini e compilerà il codice (circa 30-60 secondi al primo avvio).
4. **Accedi all'app**: Apri il browser su 👉 **[http://localhost:8081](http://localhost:8081)**

---

## 🔐 Credenziali di Accesso

Accedi con l'utente amministratore creato automaticamente:
- **Email/Username**: `admin`
- **Password**: `admin123`

---

## ⚙️ Dettagli Tecnici Docker

- **Database**: PostgreSQL è esposto sulla porta locale `5443`.
- **Configurazione**: I parametri di connessione sono definiti nel file `.env`.
- **Persistenza**: I dati non vanno persi allo spegnimento grazie ai volumi Docker.
- **Log**: Per controllare cosa succede nell'app: `docker-compose logs -f`.

---

## 📂 Documentazione & Schema Database
Per dettagli sull'architettura, la divisione dei moduli e lo schema del database (Legenda PK/FK), consulta:
👉 **[Relazione Tecnica di Progetto](RELAZIONE_TECNICA.md)**

---

## 🛑 Spegnimento
Per fermare e rimuovere i container:
```bash
docker-compose down
```
