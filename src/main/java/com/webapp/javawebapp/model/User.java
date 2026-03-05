package com.webapp.javawebapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * L'entità più complessa: l'Utente.
 * Mappata sulla tabella 'users'.
 */
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementante (Identity è perfetto per PostgreSQL e
                                                        // MySQL)
    private Long id;

    /**
     * @Column(nullable = false, unique = true): Dice al database di creare una
     *                  costrizione (CONSTRAINT).
     *                  Nessun utente potrà essere salvato senza username, né con
     *                  uno username già esistente.
     */
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // È obbligatoria. Nel DB ci andrà l'hash crittografato, NON la password in
                             // chiaro.

    private String nome;
    private String cognome;

    // Email, Telefono e CF devono essere unici nel DB per questioni anagrafiche.
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String telefono;

    @Column(unique = true)
    private String codiceFiscale;

    private String posizioneLavorativa;

    /**
     * CALLBACKS JPA (@PrePersist e @PreUpdate).
     * Vengono eseguiti un attimo prima di INSERT o UPDATE.
     * Converte le stringhe vuote "" rimesse dai form HTML in null ("vuoto
     * assoluto").
     * Perché? Perché PostgreSQL considera le stringhe vuote come "valide" per il
     * vincolo UNIQUE,
     * quindi due utenti con telefono vuoto "" farebbero crasciare il DB. Due utenti
     * con telefono "null" no!
     */
    @PrePersist
    @PreUpdate
    private void normalizzaCampi() {
        if (codiceFiscale != null && codiceFiscale.isBlank()) {
            codiceFiscale = null;
        }
        if (email != null && email.isBlank()) {
            email = null;
        }
        if (telefono != null && telefono.isBlank()) {
            telefono = null;
        }
    }

    /**
     * RELAZIONE MOLTI-A-UNO. L'utente ha 1 solo Ruolo (es. ADMIN). Un ruolo ha N
     * Utenti.
     * La colonna nel DB 'users' si chiamerà 'role_id'.
     */
    @ManyToOne(fetch = FetchType.EAGER) // Carica sempre il Role assieme allo User
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * RELAZIONE MOLTI-A-MOLTI DOMINANTE. Un utente fa N Corsi. Un corso ha N
     * Utenti.
     * 'JoinTable' sta dicendo ad Hibernate di creare lui stesso una 3° tabella
     * fisica SQL chiamata 'user_activities'
     * con 2 chiavi esterne (user_id e activity_id).
     * 
     * @JsonIgnoreProperties ignora la serializzazione del campo 'users' dentro le
     *                       singole Activity (per evitare loop)
     * @ToString.Exclude / @EqualsAndHashCode.Exclude fermano i loop ricorsivi della
     *                   libreria Lombok.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_activities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "activity_id"))
    @JsonIgnoreProperties("users")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Activity> activities = new ArrayList<>();
}