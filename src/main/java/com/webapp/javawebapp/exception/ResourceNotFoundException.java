package com.webapp.javawebapp.exception;

/**
 * Eccezione personalizzata lanciata quando cerchiamo nel Database una ri
 * orsa
 * (es. un Utente, un Corso o un Ruolo) ma questa non esiste.
 * Estendere RuntimeException è la best practice in Spring per gli errori di runtime.
 * L'eccezione verrà intercettata dal GlobalExceptionHandler che mostrerà la pagina error.html.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Costruttore base che accetta un messaggio generico.
     * @param messaggio Es. "Utente non trovato nel database"
     */
    public ResourceNotFoundException(String messaggio) {
        super(messaggio);
    }

    /**
     * Costruttore di utilità specifico per la ricerca tramite ID.
     * Semplifica la sintassi quando cerchiamo qualcosa per Primary Key.
     * @param risorsa Il nome dell'entità (es. "Utente", "Corso")
     * @param id L'identificativo che non è stato trovato (es. 5L)
     */
    public ResourceNotFoundException(String risorsa, Long id) {
        super(risorsa + " non trovato/a con ID: " + id); // Concatena e crea: "Utente non trovato/a con ID: 5"
    }
}
