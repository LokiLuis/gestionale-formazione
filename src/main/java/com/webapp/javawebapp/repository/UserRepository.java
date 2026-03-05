package com.webapp.javawebapp.repository;

import com.webapp.javawebapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository per gestire l'entità User nel Database.
 * Spring Data JPA creerà automaticamente la classe concreta a runtime.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trova un utente dal suo username esatto.
     * Restituisce un Optional utile per gestire il caso in cui l'utente non sia
     * trovato.
     */
    Optional<User> findByUsername(String username);

    /**
     * Trova tutti gli utenti che hanno uno specifico ID di ruolo.
     * Naviga la relazione: User -> role -> id
     * SQL generato: SELECT * FROM users WHERE role_id = ?
     */
    List<User> findByRoleId(Long roleId);

    /**
     * Trova tutti gli utenti filtrandoli per NOME del ruolo (es. "ROLE_FORMATORE").
     * Naviga la relazione: User -> role -> nome
     * Ottimo per trovare al volo tutti i formatori o tutti gli admin.
     */
    List<User> findByRoleNome(String nomeRuolo);

    /**
     * Trova tutti gli utenti iscritti a una determinata attività formativa.
     * Data la relazione ManyToMany, "activities" è la lista di corsi dell'utente.
     * Spring usa la tabella ponte 'user_activities' per risolvere questa query.
     */
    List<User> findByActivitiesId(Long activityId);

    /**
     * Controlla velocemente se uno username è già registrato a sistema.
     * Più efficiente di caricare l'intero record dal DB: fa una semplice conta
     * (COUNT/EXISTS).
     * 
     * @return true se lo username esiste già
     */
    boolean existsByUsername(String username);
}