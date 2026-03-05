package com.webapp.javawebapp.dto;

import jakarta.validation.constraints.*;

/**
 * Questo DTO raccoglie i dati inseriti nel "Pannello di Amministrazione"
 * quando un Admin va a Creare o Modificare un utente (form 'form-utente.html').
 * È quasi identico a RegistrazioneDTO, ma ha dei campi aggiuntivi esclusivi
 * dell'admin come l'assegnazione del Ruolo e della Posizione Lavorativa.
 */
public class UtenteFormDTO {

    /**
     * IL TRUCCO DELL'ID:
     * Se è nullo significa che l'Admin sta "creando" un nuovo utente.
     * Se è presente (es. 5L) significa che l'Admin sta "modificando" un utente
     * esistente.
     */
    private Long id;

    /**
     * Nota l'assenza di @NotBlank su Username e Password.
     * Questo perché quando l'admin modifica l'utente, questi due campi potrebbero
     * non essere stati inviati (normalmente lo username non si modifica e la
     * password ha un form a parte).
     * Manteniamo solo la regola sulla dimensione se decidiamo di alterarli in
     * futuro.
     */
    @Size(min = 3, max = 50, message = "Lo username deve essere tra 3 e 50 caratteri")
    private String username;

    @Size(min = 4, message = "La password deve essere di almeno 4 caratteri")
    private String password;

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
    private String cognome;

    @Email(message = "Formato email non valido")
    private String email;

    @Pattern(regexp = "^(\\+?[0-9\\s\\-]{6,20})?$", message = "Formato telefono non valido")
    private String telefono;

    @Pattern(regexp = "^([A-Za-z0-9]{16})?$", message = "Il codice fiscale deve essere di 16 caratteri alfanumerici")
    private String codiceFiscale;

    // Campi accessibili direttamente solo dall'Admin

    private String posizioneLavorativa;

    /**
     * L'ID numerico del ruolo preso da una <select> HTML
     * nel backend andrà recuperato e tradotto nell'oggetto 'Role' vero.
     */
    private Long roleId;

    // --- Getter e Setter ---
    // (Spring MVC inietterà i dati in questo DTO tramite i Setter alla submit del
    // form)

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
