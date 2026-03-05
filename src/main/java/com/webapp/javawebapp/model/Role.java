package com.webapp.javawebapp.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entità Role che mappa la tabella 'roles' nel database.
 * 
 * @Entity: Dichiara che questa è una classe mappa su una tabella SQL.
 * @Data: Annotazione di Lombok che genera in automatico Getter, Setter,
 *        toString, equals e hashCode.
 * @Table: Specifica esplicitamente il nome della tabella SQL (roles invece del
 *         default Role).
 */
@Entity
@Data
@Table(name = "roles")
public class Role {

    /**
     * @Id: Indica che questo campo è la Primary Key.
     * @GeneratedValue(strategy = GenerationType.IDENTITY): Il valore viene
     *                          auto-incrementato dal database (es. 1, 2, 3...).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(unique = true, nullable = false): Aggiunge vincoli al database.
     *                In questo caso due ruoli non possono avere lo stesso nome
     *                (unique) e il nome non può essere NULL.
     */
    @Column(unique = true, nullable = false)
    private String nome; // Esempio: "ROLE_ADMIN", "ROLE_FORMATORE", "ROLE_UTENTE"
}