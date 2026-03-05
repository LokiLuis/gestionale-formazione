package com.webapp.javawebapp.repository;

import com.webapp.javawebapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository per i Ruoli (es. ROLE_ADMIN, ROLE_UTENTE).
 * Estende JpaRepository permettendo interazioni automatiche col database.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Trova un ruolo in base al suo nome (es. "ROLE_ADMIN").
     * Ritorna un Optional<Role> invece che Role diretto, perché il ruolo potrebbe
     * non esistere nel DB (in questo modo si evita la NullPointerException
     * strutturalmente).
     * SQL generato: SELECT * FROM roles WHERE nome = ?
     * 
     * @param nome Il nome testuale del ruolo
     * @return Optional contenente il ruolo (se trovato) o vuoto
     */
    Optional<Role> findByNome(String nome);
}