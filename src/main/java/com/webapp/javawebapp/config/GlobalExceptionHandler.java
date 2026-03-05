package com.webapp.javawebapp.config;

import com.webapp.javawebapp.exception.DuplicateResourceException;
import com.webapp.javawebapp.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

/**
 * Gestore GLOBALE degli errori dell'applicazione.
 * 
 * @ControllerAdvice: Un "Intercettore Globale". Qualsiasi eccezione "buchi" i
 *                    tuoi Controller,
 *                    atterra qui invece di mostrare la brutta schermata bianca
 *                    (Whitelabel Error Page) creata da Spring.
 *                    Da qui mandiamo l'utente alla nostra bella pagina
 *                    'error.html'.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Il Logger serve per scrivere l'errore tecnico nella console di IntelliJ (o
    // nel file di log)
    // per noi sviluppatori, mentre all'utente mostriamo un messaggio carino a
    // schermo.
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =============================================
    // 1. RISORSA NON TROVATA (Errore Custom)
    // =============================================
    /**
     * @ExceptionHandler dice a Spring: "Se vedi una ResourceNotFoundException in
     *                   giro per l'app, esegui QUESTO metodo!"
     *                   L'errore restituito al browser (ResponseStatus) sarà un 404
     *                   (Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("⚠️ Risorsa non trovata: {}", ex.getMessage());

        // Riempiamo il model per la pagina error.html
        model.addAttribute("codiceErrore", "404");
        model.addAttribute("titoloErrore", "Risorsa Non Trovata");
        model.addAttribute("messaggioErrore", "L'elemento che stai cercando non esiste o è stato eliminato.");
        model.addAttribute("dettaglio", ex.getMessage());

        return "error"; // Ritorna il file src/main/resources/templates/error.html
    }

    // =============================================
    // 1b. RISORSA NON TROVATA (Eccezioni Java Generiche)
    // =============================================
    /**
     * Cattura errori nativi di Java (es. quando l'ID di Optional.get() non esiste)
     * e li tratta fluidamente come un 404.
     */
    @ExceptionHandler({ NoSuchElementException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception ex, Model model) {
        log.warn("⚠️ Risorsa non trovata o parametro non valido: {}", ex.getMessage());

        model.addAttribute("codiceErrore", "404");
        model.addAttribute("titoloErrore", "Risorsa Non Trovata");
        model.addAttribute("messaggioErrore", "L'elemento che stai cercando non esiste o è stato eliminato.");
        model.addAttribute("dettaglio", ex.getMessage());

        return "error";
    }

    // =============================================
    // 2. RISORSA DUPLICATA (Errore Custom)
    // =============================================
    /**
     * Cattura il nostro DuplicateResourceException lanciato nei Service (es.
     * Username già in uso).
     * Invia un 409 (Conflict).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicate(DuplicateResourceException ex, Model model) {
        log.warn("⚠️ Risorsa duplicata: {}", ex.getMessage());

        model.addAttribute("codiceErrore", "409");
        model.addAttribute("titoloErrore", "Risorsa Duplicata");
        model.addAttribute("messaggioErrore", ex.getMessage());
        model.addAttribute("dettaglio", "Modifica i dati e riprova.");

        return "error";
    }

    // =============================================
    // 3. ACCESSO NEGATO (Security Exception)
    // =============================================
    /**
     * Errore 403 (Forbidden) lanciato in automatico da Spring Security quando
     * es. un Utente base prova ad aprire l'URL /utenti/nuovo (riservato agli
     * Admin).
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.warn("🔒 Accesso negato: {}", ex.getMessage());

        model.addAttribute("codiceErrore", "403");
        model.addAttribute("titoloErrore", "Accesso Negato");
        model.addAttribute("messaggioErrore", "Non hai i permessi necessari per accedere a questa pagina.");
        model.addAttribute("dettaglio", "Contatta un amministratore se ritieni sia un errore.");

        return "error";
    }

    // =============================================
    // 4. ERRORE GENERICO (Fallback per tutto il resto)
    // =============================================
    /**
     * Fallback assoluto: Qualsiasi altra Eccezione Java (Exception.class) finisce
     * qui dentro.
     * È il classico 500 Internal Server Error (es. Database offline,
     * NullPointerException)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        log.error("🔴 Errore imprevisto del server: ", ex); // Scrive TUTTO il tracciato rosso nella console!

        model.addAttribute("codiceErrore", "500");
        model.addAttribute("titoloErrore", "Errore del Server");
        model.addAttribute("messaggioErrore", "Si è verificato un errore imprevisto. Riprova più tardi.");
        model.addAttribute("dettaglio", ex.getMessage());

        return "error";
    }
}
