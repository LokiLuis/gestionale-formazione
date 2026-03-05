package com.webapp.javawebapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entità Activity che rappresenta un Corso di Formazione.
 * Mappata sulla tabella 'activities' del database Postgres.
 */
@Entity
@Data
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Chiave primaria auto-incrementata
    private Long id;

    private String nome;
    private String descrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;

    /**
     * RELAZIONE MOLTI-A-UNO: Molti corsi possono avere lo stesso Formatore.
     * 
     * @ManyToOne: Specifica la molteplicità rispetto all'entità 'User'.
     *             FetchType.EAGER carica l'utente subito.
     * @JoinColumn: Specifica il nome della colonna nel database che fa da Foreign
     *              Key.
     * @JsonIgnoreProperties: Quando convertiamo in JSON (es. per un'API REST), si
     *                        ferma per non causare loop ricorsivi infiniti
     * @ToString.Exclude / @EqualsAndHashCode.Exclude: Evita che Lombok finisca in
     *                   un loop infinito (User.toString chiama Activity.toString
     *                   che chiama User.toString...)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formatore_id")
    @JsonIgnoreProperties({ "activities", "password" })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User formatore;

    /**
     * RELAZIONE MOLTI-A-MOLTI INVERSA: Molti corsi hanno molti utenti iscritti.
     * 
     * @ManyToMany(mappedBy = "activities"): mappedBy dice a Hibernate "Io non
     *                      domino questa relazione, la gestisce il campo
     *                      'activities' nella classe 'User'!".
     *                      In questo modo eviti la creazione di tabelle duplicate
     *                      per caso.
     * @JsonIgnore: Non serializzare la lista degli utenti per ovvi motivi di
     *              sicurezza/peso.
     */
    @ManyToMany(mappedBy = "activities")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> users = new ArrayList<>();

    /**
     * METODO CALLBACK CICLO DI VITA JPA.
     * 
     * @PreRemove indica a Hibernate di lanciare questo metodo UN ATTIMO PRIMA di
     *            fare "DELETE FROM activities" sul DB.
     *            Serve a prevenire l'Eccezione di Chiave Esterna (Foreign Key
     *            Constraint Violation) disaccoppiando
     *            gli utenti dal corso prima di cancellarlo definitivamente.
     */
    @PreRemove
    private void preRemove() {
        for (User user : users) {
            user.getActivities().remove(this); // Rimuove questo corso dalla lista corsi di ogni utente
        }
    }
}