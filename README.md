# 🎓 Gestione Formazione — Full-Stack Java App

[![CI/CD Pipeline](https://github.com/LokiLuis/gestionale-formazione/actions/workflows/ci.yml/badge.svg)](https://github.com/LokiLuis/gestionale-formazione/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://www.docker.com/)
[![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-red?logo=prometheus)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-Dashboard-orange?logo=grafana)](https://grafana.com/)

> Applicazione web enterprise per la gestione di anagrafiche utenti e attività formative, con autenticazione basata su ruoli (RBAC), observability stack (Prometheus + Grafana) e CI/CD automatizzata.

---

## 🏗️ Architettura

```
┌─────────────┐     ┌──────────────────┐     ┌──────────────┐
│   Browser   │────▶│  Spring Boot     │────▶│  PostgreSQL  │
│             │◀────│  (porta 8081)    │◀────│  (porta 5443)│
└─────────────┘     └───────┬──────────┘     └──────────────┘
                            │
                    /actuator/prometheus
                            │
                    ┌───────▼──────────┐     ┌──────────────┐
                    │   Prometheus     │────▶│    Grafana    │
                    │   (porta 9090)   │     │  (porta 3000)│
                    └──────────────────┘     └──────────────┘
```

**CI/CD Pipeline** (GitHub Actions):
```
Push su main  →  Maven Build & Test  →  Docker Build  →  Push a GHCR
```

---

## 🛠️ Tech Stack

| Layer              | Tecnologia                     |
|--------------------|--------------------------------|
| **Backend**        | Java 17 + Spring Boot 3.5      |
| **Frontend**       | Thymeleaf (server-side)        |
| **Database**       | PostgreSQL 15                  |
| **Sicurezza**      | Spring Security 6 (RBAC)       |
| **Container**      | Docker + Docker Compose        |
| **CI/CD**          | GitHub Actions                 |
| **Monitoring**     | Prometheus + Grafana           |
| **Metriche**       | Micrometer + Actuator          |

---

## 🚀 Quick Start

### Prerequisiti
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installato e avviato

### 1. Clona il repository
```bash
git clone https://github.com/LokiLuis/gestionale-formazione.git
cd gestione-formazione
```

### 2. Configura le variabili d'ambiente
```bash
cp .env.example .env
# Modifica il file .env con le tue credenziali
```

### 3. Avvia tutto con un comando
```bash
docker-compose up --build
```

### 4. Accedi ai servizi

| Servizio        | URL                          | Credenziali           |
|-----------------|------------------------------|-----------------------|
| **App Web**     | http://localhost:8081         | admin@example.com / admin |
| **Prometheus**  | http://localhost:9090         | —                     |
| **Grafana**     | http://localhost:3000         | admin / admin         |

---

## 📊 Observability

L'applicazione espone metriche JVM e HTTP tramite **Spring Boot Actuator + Micrometer**, raccolte da **Prometheus** e visualizzate su **Grafana**.

### Metriche monitorate:
- 📈 **CPU Usage** — Utilizzo CPU dell'app e del sistema
- 💾 **JVM Memory** — Heap e Non-Heap (con limiti massimi)
- 🧵 **Threads** — Thread attivi della JVM
- 🌐 **HTTP Requests** — Rate, latenza e codici di risposta
- ♻️ **Garbage Collector** — Pause e attività del GC

### Endpoints:
- `/actuator/health` — Stato dell'applicazione e del database
- `/actuator/prometheus` — Metriche in formato Prometheus
- `/actuator/info` — Informazioni sull'applicazione

---

## 🔄 CI/CD Pipeline

La pipeline **GitHub Actions** automatizza il processo di build e deployment:

1. **Build & Test** — Compila il progetto con Maven ed esegue i test
2. **Docker Build** — Costruisce l'immagine Docker multi-stage
3. **Push a GHCR** — Pubblica l'immagine su GitHub Container Registry

La pipeline si attiva automaticamente ad ogni `push` sul branch `main`.

---

## 👥 Funzionalità per Ruolo

| Funzionalità                    | ADMIN | FORMATORE | USER |
|---------------------------------|:-----:|:---------:|:----:|
| Gestione utenti (CRUD)         | ✅    | ❌        | ❌   |
| Creazione/modifica corsi       | ✅    | ❌        | ❌   |
| Gestione iscrizioni            | ✅    | ❌        | ❌   |
| Vista iscritti al corso        | ✅    | ✅        | ❌   |
| Dashboard formatore            | ✅    | ✅        | ❌   |
| Le mie attività                | ✅    | ✅        | ✅   |

---

## 📁 Struttura del Progetto

```
├── .github/workflows/ci.yml     # CI/CD Pipeline (GitHub Actions)
├── grafana/                      # Configurazione Grafana
│   ├── dashboards/               # Dashboard JSON preconfigurate
│   └── provisioning/             # Auto-provisioning datasource
├── prometheus/                   # Configurazione Prometheus
│   └── prometheus.yml            # Target di scraping
├── src/main/java/com/webapp/
│   ├── config/                   # Security, DataInitializer, ExceptionHandler
│   ├── controller/               # AuthController, UtenteController, CorsoController
│   ├── dto/                      # Data Transfer Objects
│   ├── model/                    # Entità JPA (User, Role, Activity)
│   ├── repository/               # Spring Data JPA Repositories
│   └── service/                  # Business Logic Layer
├── Dockerfile                    # Multi-stage build (Maven → JRE)
├── docker-compose.yml            # Orchestrazione 4 servizi
└── pom.xml                       # Dipendenze Maven
```

---

## 📄 Documentazione

- [RELAZIONE_TECNICA.md](RELAZIONE_TECNICA.md) — Documentazione tecnica completa del progetto

---

## 📝 Licenza

Progetto sviluppato a scopo formativo — Marzo 2026
