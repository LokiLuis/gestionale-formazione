package com.webapp.javawebapp.dto;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) per l'entità Activity.
 * I DTO servono per passare dati tra il backend e il frontend (o viceversa)
 * senza esporre le vere Entità del database (che contengono logica, relazioni e
 * password).
 * Questo DTO NON contiene la lista di utenti iscritti — evita loop infiniti
 * (User -> Activity -> User) e manda meno dati in rete.
 */
public class ActivityDTO {

    private Long id;
    private String nome;
    private String descrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;

    /**
     * Costruttore vuoto.
     * È OBBLIGATORIO per Jackson (la libreria che converte Java in JSON e
     * viceversa).
     * Senza questo, Spring non riuscirebbe a leggere le richieste dal frontend.
     */
    public ActivityDTO() {
    }

    // --- Getters e Setters ---
    // (Invece di Lombok @Data, qui sono espliciti per scopo didattico)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }
}
