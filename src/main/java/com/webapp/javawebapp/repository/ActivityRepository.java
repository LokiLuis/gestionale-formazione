package com.webapp.javawebapp.repository;

import com.webapp.javawebapp.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Questo è il Repository per l'entità Activity (I Corsi).
 * In Spring Data JPA, basta estendere l'interfaccia JpaRepository per avere
 * IN AUTOMATICO tutti i metodi CRUD standard (save, findById, findAll,
 * deleteById)
 * senza dover scrivere nemmeno una riga di SQL.
 * Tipi generici: <Activity (L'entità), Long (Il tipo della sua Primary Key -
 * ID)>
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Esempio di "Derived Query Method" (Query derivata dal nome del metodo).
     * Spring legge il nome del metodo e genera la query SQL dietro le quinte:
     * SELECT * FROM activities WHERE UPPER(nome) LIKE UPPER('%nome%')
     */
    List<Activity> findByNomeContainingIgnoreCase(String nome);

    /**
     * Trova tutti i corsi assegnati a uno specifico formatore tramite il suo ID.
     * La sintassi usa "FormatoreId" che naviga la relazione: Activity -> formatore
     * -> id.
     * SQL generato: SELECT * FROM activities WHERE formatore_id = ?
     */
    List<Activity> findByFormatoreId(Long formatoreId);
}