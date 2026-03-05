package com.webapp.javawebapp.controller;

import com.webapp.javawebapp.dto.RegistrazioneDTO;
import com.webapp.javawebapp.exception.DuplicateResourceException;
import com.webapp.javawebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller dedicato esclusivamente all'Autenticazione.
 * Gestisce la dashboard principale (Home), la pagina di Login e il flusso di
 * Registrazione pubblica.
 * 
 * @Controller indica a Spring MVC che questa classe risponderà a richieste HTTP
 *             restituendo file HTML (Thymeleaf).
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // =============================================
    // DASHBOARD PRINCIPALE
    // =============================================

    /**
     * @GetMapping("/") intercetta le richieste dirette all'indirizzo base
     * (localhost:8080/).
     * Ritorna "home", che Spring MVC tradurrà in
     * "src/main/resources/templates/home.html".
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // =============================================
    // LOGIN
    // =============================================

    /**
     * Mostra la pagina di login personalizzata.
     * La gestione effettiva del POST dei dati di login è gestita magicamente da
     * Spring Security.
     * A noi basta fargli vedere l'HTML.
     */
    @GetMapping("/login")
    public String loginPagina() {
        return "login";
    }

    // =============================================
    // REGISTRAZIONE PUBBLICA
    // =============================================

    /**
     * Mostra il form vuoto.
     * Dobbiamo inserire un oggetto 'RegistrazioneDTO' vuoto nel Model,
     * altrimenti Thymeleaf (th:object) andrebbe in crash non trovandolo.
     */
    @GetMapping("/registrazione")
    public String paginaRegistrazione(Model model) {
        model.addAttribute("registrazioneDTO", new RegistrazioneDTO());
        return "registrazione"; // Ricarica registrazione.html
    }

    /**
     * Riceve i dati compilati dall'utente (metodo POST).
     * 
     * @Valid dice a Spring: "Controlla le regole (@NotBlank, @Email) nel DTO prima
     *        di entrare nel metodo!"
     *        BindingResult contiene l'esito della validazione. DEVE sempre trovarsi
     *        subito dopo il parametro @Valid.
     */
    @PostMapping("/registrazione")
    public String registraUtente(@Valid @ModelAttribute("registrazioneDTO") RegistrazioneDTO dto,
            BindingResult result,
            Model model) {

        // 1. Validazione Automatica (campi vuoti, password corte...)
        // Se c'è anche un solo errore, restituisce la stessa pagina HTML,
        // e Thymeleaf mostrerà i 'th:errors' in automatico colorandoli di rosso!
        if (result.hasErrors()) {
            return "registrazione";
        }

        // 2. Controllo Logica (es. Username già preso)
        try {
            userService.registraUtente(dto);
            // Se fa tutto, prepariamo il messaggio verde di successo e resettiamo il form
            // svuotandolo
            model.addAttribute("successMessage", "Registrazione completata! Ora puoi accedere con le tue credenziali.");
            model.addAttribute("registrazioneDTO", new RegistrazioneDTO());
        } catch (DuplicateResourceException ex) {
            // Se lo username era già preso, aggiungiamo un errore MANUALE al BindingResult
            // "Senti, associa al campo 'username' questo messaggio di errore e ricarica la
            // pagina"
            result.rejectValue("username", "duplicate", ex.getMessage());
            return "registrazione";
        }

        return "registrazione";
    }
}
