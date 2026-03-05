package com.webapp.javawebapp.dto;

import jakarta.validation.constraints.*;

/**
 * DTO utilizzato ESCLUSIVAMENTE per raccogliere i dati dal form di
 * registrazione in 'registrazione.html'.
 * Questo pattern (Form DTO) separa la raccolta dei dati dalla struttura del
 * database.
 * Tramite Jakarta Validation API (le annotazioni come @NotBlank o @Size)
 * definiamo REGOLE DI VALIDAZIONE.
 * Spring controllerà che i dati le rispettino prima ancora di far partire il
 * nostro codice.
 */
public class RegistrazioneDTO {

    /**
     * @NotBlank: Il campo non può essere null, vuoto "" o composto da soli spazi
     *            bianchi " ".
     * @Size: Definisce lunghezza minima e/o massima di caratteri.
     *        message: Il testo che verrà mostrato automaticamente nel form se la
     *        validazione fallisce.
     */
    @NotBlank(message = "Lo username è obbligatorio")
    @Size(min = 3, max = 50, message = "Lo username deve essere tra 3 e 50 caratteri")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 4, message = "La password deve essere di almeno 4 caratteri")
    private String password; // In ingresso è in chiaro, poi la crittograferemo col PasswordEncoder nel
                             // Service.

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
    private String cognome;

    /**
     * @Pattern: Validazione basata su espressioni regolari (Regex).
     *           "^" Inizio stringa
     *           "([A-Za-z0-9]{16})" Esattamente 16 caratteri alfanumerici
     *           "?" Rende l'intero gruppo OPZIONALE. Quindi accetta CF completi
     *           oppure stringa vuota (perché non è obbligatorio).
     *           "$" Fine stringa
     */
    @Pattern(regexp = "^([A-Za-z0-9]{16})?$", message = "Il codice fiscale deve essere di 16 caratteri alfanumerici")
    private String codiceFiscale;

    @Pattern(regexp = "^(\\+?[0-9\\s\\-]{6,20})?$", message = "Formato telefono non valido")
    private String telefono;

    /**
     * @Email: Regola predefinita che controlla la presenza della '@' e un dominio
     *         valido (es. test@test.com).
     */
    @Email(message = "Formato email non valido")
    private String email;

    // --- Getter e Setter ---
    // (Spring MVC ha un disperato bisogno dei setter per "riempire" l'oggetto
    // quando l'utente invia il form HTML).

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

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
