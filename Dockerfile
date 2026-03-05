# ============================================================
# DOCKERFILE — Multi-stage Build per Spring Boot
# ============================================================
# Questo file insegna a Docker come costruire e far girare
# la nostra applicazione Java. Usa una tecnica chiamata
# "Multi-stage Build" (costruzione a più fasi) per produrre
# un'immagine finale leggera e sicura.
# ============================================================

# ==================== FASE 1: BUILD ====================
# Partiamo da un'immagine che ha sia Maven che Java 17.
# Il suo unico scopo è compilare il codice e produrre il .jar
FROM maven:3.9-eclipse-temurin-17 AS build

# Cartella di lavoro dentro al container (come fare "cd /app")
WORKDIR /app

# Copiamo PRIMA solo il pom.xml (il file delle dipendenze).
# Perché? Perché Docker usa una cache intelligente: se il pom.xml
# non è cambiato dall'ultima build, Docker RICICLA le dipendenze
# già scaricate, risparmiando minuti preziosi.
COPY pom.xml .

# Scarica tutte le dipendenze (senza compilare il codice).
# -B = modalità "batch" (niente output interattivo)
# -q = modalità silenziosa
RUN mvn dependency:go-offline -B -q

# Ora copiamo il codice sorgente vero e proprio
COPY src ./src

# Compiliamo e creiamo il file .jar finale.
# -DskipTests = salta i test unitari durante la build Docker
#               (i test li eseguiamo separatamente in CI/CD)
RUN mvn clean package -DskipTests -B -q

# ==================== FASE 2: RUN ====================
# Ora prendiamo un'immagine molto più leggera (solo Java, senza Maven)
# e ci copiamo dentro SOLO il .jar prodotto nella fase precedente.
# Risultato: immagine finale ~300MB invece di ~800MB!
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamo il .jar dalla fase "build" all'immagine finale
COPY --from=build /app/target/*.jar app.jar

# Porta su cui l'applicazione Spring Boot ascolta
EXPOSE 8081

# Comando che Docker eseguirà quando il container parte
ENTRYPOINT ["java", "-jar", "app.jar"]
