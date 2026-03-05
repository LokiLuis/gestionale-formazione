package com.webapp.javawebapp.controller;

import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.service.ActivityService;
import com.webapp.javawebapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

/**
 * Controller centrale per gestire tutte le logiche riguardanti i Corsi
 * (Activity).
 * Crea interazioni per Admin, Formatori e Utenti base a seconda dell'URL
 * mappato.
 */
@Controller
public class CorsoController {

    private final ActivityService activityService;
    private final UserService userService;

    public CorsoController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    // =============================================
    // LISTA CORSI GENERALE (Visibile a tutti gli iscritti al portale)
    // =============================================

    /**
     * Principal: Rappresenta l'Utente attualmente Loggato (è un oggetto di Spring
     * Security).
     * 
     * @RequestParam(required = false): Prende il testo dalla barra di ricerca URL
     *                        es. ?ricerca=java
     */
    @GetMapping("/corsi")
    public String visualizzaCorsi(Model model, Principal principal,
            @RequestParam(required = false) String ricerca) {
        List<Activity> corsi = activityService.cercaPerNome(ricerca);
        model.addAttribute("listaCorsi", corsi);
        model.addAttribute("ricerca", ricerca); // Rimandiamo la ricerca all'HTML per mantenere il testo nella barra

        // Passa la lista degli ID dei corsi a cui l'utente loggato è iscritto.
        // Serve all'HTML per capire se deve mostrare il bottone rosso "Disiscriviti" o
        // verde "Iscriviti".
        if (principal != null) {
            User utente = userService.trovaPerUsername(principal.getName());
            model.addAttribute("corsiIscritto", activityService.trovaIdCorsiIscritto(utente));
        }

        return "corsi";
    }

    // =============================================
    // CRUD CORSI (Solo ADMIN lavora in queste rotte)
    // =============================================

    @GetMapping("/corsi/nuovo")
    public String nuovoCorso(Model model) {
        model.addAttribute("activity", new Activity());
        // Forniamo la lista dei formatori per riempire la <select> nel form
        model.addAttribute("listaFormatori", userService.trovaFormatori());
        return "form-corso";
    }

    @GetMapping("/corsi/modifica/{id}")
    public String modificaCorso(@PathVariable Long id, Model model) {
        Activity activity = activityService.trovaPerId(id);
        model.addAttribute("activity", activity);
        model.addAttribute("listaFormatori", userService.trovaFormatori());
        return "form-corso";
    }

    /**
     * RedirectAttributes: Serve a mostrare i messaggi (verdi/rossi) DOPO un
     * redirect.
     * Dato che il return è "redirect:/corsi", un Model normale si perderebbe per
     * strada.
     */
    @PostMapping("/corsi/salva")
    public String salvaCorso(@ModelAttribute Activity activity,
            @RequestParam(required = false) Long formatoreId,
            RedirectAttributes redirectAttributes) {

        activityService.salvaCorso(activity, formatoreId);
        redirectAttributes.addFlashAttribute("successMessage", "Corso salvato con successo!");
        return "redirect:/corsi";
    }

    @PostMapping("/corsi/elimina/{id}")
    public String eliminaCorso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        activityService.eliminaCorso(id);
        redirectAttributes.addFlashAttribute("successMessage", "Corso eliminato con successo!");
        return "redirect:/corsi";
    }

    // =============================================
    // DETTAGLIO CORSO E LISTA ISCRITTI (ADMIN e FORMATORE DEL CORSO)
    // =============================================

    @GetMapping("/corsi/{id}/iscritti")
    public String dettaglioCorso(@PathVariable Long id, Model model, Principal principal) {
        Activity corso = activityService.trovaPerId(id);

        // Controllo di Sicurezza Extra Applicativo:
        // Anche se Spring Security permette l'accesso ai ruoli FORMATORE E ADMIN,
        // Dobbiamo verificare che QUEL formatore insegni proprio in QUESTO corso
        // specifico.
        User utenteLoggato = userService.trovaPerUsername(principal.getName());
        boolean isAdmin = utenteLoggato.getRole() != null && "ROLE_ADMIN".equals(utenteLoggato.getRole().getNome());

        // È lui il formatore del corso?
        boolean isFormatoreDelCorso = corso.getFormatore() != null
                && corso.getFormatore().getId().equals(utenteLoggato.getId());

        if (!isAdmin && !isFormatoreDelCorso) {
            // Seleziona un'eccezione nativa di Spring Security per mostrare l'error.html
            // 403-AccessDenied
            throw new org.springframework.security.access.AccessDeniedException(
                    "Non sei il formatore di questo corso.");
        }

        List<User> iscritti = corso.getUsers();

        model.addAttribute("corso", corso);
        model.addAttribute("iscritti", iscritti);
        model.addAttribute("isAdmin", isAdmin); // Serve all'HTML per nascondere l'icona "cestino" ai formatori

        // Se sei Admin, ti preparo la lista degli utenti non iscritti, per potergli
        // fare Iscrizione di Massa
        if (isAdmin) {
            model.addAttribute("utentiDisponibili", activityService.trovaUtentiDisponibili(id));
        } else {
            model.addAttribute("utentiDisponibili", Collections.emptyList());
        }

        return "dettaglio-corso";
    }

    // =============================================
    // GESTIONE MASSIVA ISCRITTI (Solo ADMIN)
    // =============================================

    @PostMapping("/corsi/{cid}/iscritti/aggiungi")
    public String iscriviMassivoAlCorso(@PathVariable("cid") Long cid,
            @RequestParam(name = "userIds", required = false) List<Long> userIds,
            RedirectAttributes redirectAttributes) {

        if (userIds != null && !userIds.isEmpty()) {
            activityService.iscriviMassivo(cid, userIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    userIds.size() + " utente/i iscritto/i con successo!");
        }
        return "redirect:/corsi/" + cid + "/iscritti"; // Torna alla pagina del corso appena modificato
    }

    @PostMapping("/corsi/{cid}/iscritti/rimuovi/{uid}")
    public String rimuoviIscrittoDaCorso(@PathVariable("cid") Long cid,
            @PathVariable("uid") Long uid,
            RedirectAttributes redirectAttributes) {

        User utente = userService.trovaPerid(uid);
        Activity corso = activityService.trovaPerId(cid);

        userService.rimuoviDaCorso(utente, corso);
        redirectAttributes.addFlashAttribute("successMessage", "Utente rimosso dal corso!");
        return "redirect:/corsi/" + cid + "/iscritti";
    }

    // =============================================
    // ISCRIZIONE / DISISCRIZIONE PERSONALE INDIVIDUALE
    // =============================================

    @PostMapping("/corsi/iscrivi/{id}")
    public String iscrivitiAlCorso(@PathVariable Long id, Principal principal,
            RedirectAttributes redirectAttributes) {
        User utente = userService.trovaPerUsername(principal.getName());
        Activity corso = activityService.trovaPerId(id);

        userService.iscriviACorso(utente, corso);
        redirectAttributes.addFlashAttribute("successMessage", "Iscrizione effettuata con successo!");
        return "redirect:/corsi";
    }

    @PostMapping("/corsi/disiscriviti/{id}")
    public String disiscrivitiDalCorso(@PathVariable Long id, Principal principal,
            RedirectAttributes redirectAttributes) {
        User utente = userService.trovaPerUsername(principal.getName());
        Activity corso = activityService.trovaPerId(id);

        userService.rimuoviDaCorso(utente, corso);
        redirectAttributes.addFlashAttribute("successMessage", "Iscrizione annullata!");
        return "redirect:/corsi";
    }

    // =============================================
    // DASHBOARD PERSONALI
    // =============================================

    /**
     * Vetrina per il Formatore: Mostra solo i Corsi che gli sono stati assegnati.
     */
    @GetMapping("/formatore/corsi")
    public String iMieiCorsiFormatore(Model model, Principal principal,
            @RequestParam(required = false) String ricerca) {
        User formatore = userService.trovaPerUsername(principal.getName());
        List<Activity> mieCorsi = activityService.cercaCorsiFormatore(formatore.getId(), ricerca);

        model.addAttribute("listaCorsi", mieCorsi);
        model.addAttribute("ricerca", ricerca);
        return "formatore-corsi";
    }

    /**
     * Vetrina per l'Utente Standard: Mostra a quali Corsi è iscritto come allievo.
     */
    @GetMapping("/attivita/mie")
    public String leMieAttivita(Model model, Principal principal,
            @RequestParam(required = false) String ricerca) {
        User utente = userService.trovaPerUsername(principal.getName());
        List<Activity> mieAttivita = activityService.cercaAttivitaUtente(utente, ricerca);

        model.addAttribute("listaCorsi", mieAttivita);
        model.addAttribute("ricerca", ricerca);
        return "mie-attivita";
    }
}
