package com.webapp.javawebapp.exception;

/**
 * Eccezione personalizzata lanciata quando si tenta di creare una risorsa
 * duplicata.
 * In Spring Boot, creare una classe che estende RuntimeException ci permette di
 * lanciare questo errore ovunque nel codice (es. nel Service) senza essere
 * obbligati
 * a dichiararlo nei metodi con la keyword 'throws'.
 * Questa eccezione verrà poi "catturata" dal nostro GlobalExceptionHandler.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Costruttore che accetta un messaggio di errore personalizzato.
     * 
     * @param messaggio Il testo che spiegherà cosa è andato storto (es. "Username
     *                  già in uso").
     */
    public DuplicateResourceException(String messaggio) {
        super(messaggio); // Passa il messaggio alla superclasse (RuntimeException) per memorizzarlo
    }
}
