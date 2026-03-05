package com.webapp.javawebapp.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) per l'entità User.
 * Usato per mandare le informazioni dell'utente verso le pagine HTML (es. la
 * lista utenti)
 * SENZA inviare la password (nemmeno crittografata).
 * Rappresenta l'Utente in "sola lettura" per l'interfaccia grafica.
 */
public class UserDTO {

    private Long id;
    private String username;
    // Omettiamo VOLONTARIAMENTE il campo password. La sicurezza prima di tutto.

    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private String codiceFiscale;
    private String posizioneLavorativa;

    /**
     * Invece di inviare l'intero oggetto "Role" che contiene l'ID e il Nome,
     * "appiattiamo" (flattening) il DTO inviando solo la stringa che ci interessa
     * visualizzare (es. "ROLE_ADMIN").
     */
    private String ruolo;

    /**
     * La lista delle attività a cui è iscritto l'utente.
     * Si usa una List di ActivityDTO (non le vere Activity), così tronchiamo il
     * rischio
     * di loop ricorsivi (Utente -> Corso -> Lista Iscritti -> Utente...).
     */
    private List<ActivityDTO> activities;

    /**
     * Costruttore vuoto. Necessario per la libreria Jackson per costruire l'oggetto
     * in fase di deserializzazione dati REST/JSON.
     */
    public UserDTO() {
    }

    // --- Getters e Setters (generati ma esposti per scopi didattici) ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getPosizioneLavorativa() {
        return posizioneLavorativa;
    }

    public void setPosizioneLavorativa(String posizioneLavorativa) {
        this.posizioneLavorativa = posizioneLavorativa;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }
}
