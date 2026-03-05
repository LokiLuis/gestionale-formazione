package com.webapp.javawebapp.controller;

import com.webapp.javawebapp.dto.UtenteFormDTO;
import com.webapp.javawebapp.exception.DuplicateResourceException;
import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.service.ActivityService;
import com.webapp.javawebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller per la sezione di Amministrazione Utenti.
 * (Queste rotte sono tutte protette da '/utenti/** = hasRole("ADMIN")' nel
 * SecurityConfig).
 */
@Controller
public class UtenteController {

    private final UserService userService;
    private final ActivityService activityService;

    public UtenteController(UserService userService, ActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    // =============================================
    // LISTA UTENTI (Con sistema di Filtri)
    // =============================================

    @GetMapping("/utenti")
    public String visualizzaUtenti(Model model,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Long activityId) {
        List<User> utenti;

        // Semplice albero decisionale per i MOCK di Filtri (Dropdown in
        // anagrafiche-utenti.html)
        if (roleId != null) {
            utenti = userService.filtraPerRuolo(roleId);
        } else if (activityId != null) {
            // Serve a filtrare al volo es. "Fammi vedere chi è iscritto al Corso Java"
            utenti = userService.filtraPerAttivita(activityId);
        } else {
            utenti = userService.trovaTutti();
        }

        model.addAttribute("listaUtenti", utenti);
        // Passiamo queste due liste per popolare le opzioni <option> nei menu a tendina
        model.addAttribute("listaRuoli", userService.trovaTuttiRuoli());
        model.addAttribute("listaAttivita", activityService.trovaTutti());

        // Rimandiamo indietro l'id selezionato per fare in modo che la <select>
        // mantenga in memoria la scelta dell'admin
        model.addAttribute("roleIdSelezionato", roleId);
        model.addAttribute("activityIdSelezionato", activityId);

        return "anagrafiche-utenti";
    }

    // =============================================
    // FORM NUOVO ED EDIT UTENTE
    // =============================================

    @GetMapping("/utenti/nuovo")
    public String nuovoUtente(Model model) {
        // Passiamo il DTO vuoto per la creazione
        model.addAttribute("utenteForm", new UtenteFormDTO());
        model.addAttribute("listaRuoli", userService.trovaTuttiRuoli());
        return "form-utente";
    }

    @GetMapping("/utenti/modifica/{id}")
    public String modificaUtente(@PathVariable Long id, Model model) {
        User user = userService.trovaPerid(id);

        // Qui c'è una logica importante:
        // L'Admin preme "Modifica" e noi dobbiamo prendere il VERO UTENTE dal DataBase,
        // ma non possiamo darglielo in pasto ad HTML. Dobbiamo travasare l'Utente
        // dentro il nostro UtenteFormDTO (una sorta di "vestito su misura" per il form
        // grafico).
        UtenteFormDTO dto = new UtenteFormDTO();
        dto.setId(user.getId());
        dto.setNome(user.getNome());
        dto.setCognome(user.getCognome());
        dto.setEmail(user.getEmail());
        dto.setTelefono(user.getTelefono());
        dto.setCodiceFiscale(user.getCodiceFiscale());
        dto.setPosizioneLavorativa(user.getPosizioneLavorativa());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
        }

        // Adesso mandiamo il DTO pieno al frontend
        model.addAttribute("utenteForm", dto);
        model.addAttribute("listaRuoli", userService.trovaTuttiRuoli());

        return "form-utente";
    }

    // =============================================
    // SALVATAGGIO / CREAZIONE (Con DTO + @Valid)
    // =============================================

    @PostMapping("/utenti/salva")
    public String salvaUtente(@Valid @ModelAttribute("utenteForm") UtenteFormDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Se Spring Validation trova un errore (es. email falsa, cognome lasciato
        // vuoto) ...
        if (result.hasErrors()) {
            // ... ricarichiamo la pagina ripopolando i Ruoli, e gli errori appariranno
            // sotto i campi rossi!
            model.addAttribute("listaRuoli", userService.trovaTuttiRuoli());
            return "form-utente";
        }

        try {
            // MAGIC TRICK: Se l'id è nel DTO (non null), era una modifica, altrimenti era
            // una creazione!
            if (dto.getId() != null) {
                userService.aggiornaUtente(dto);
                redirectAttributes.addFlashAttribute("successMessage", "Utente modificato con successo!");
            } else {
                userService.creaUtente(dto);
                redirectAttributes.addFlashAttribute("successMessage", "Utente creato con successo!");
            }
        } catch (DuplicateResourceException ex) {
            // L'Exception lanciata in UserService viene catturata qui
            result.rejectValue("username", "duplicate", ex.getMessage());
            model.addAttribute("listaRuoli", userService.trovaTuttiRuoli());
            return "form-utente";
        }

        return "redirect:/utenti";
    }

    // =============================================
    // ELIMINAZIONE UTENTE
    // =============================================

    @PostMapping("/utenti/elimina/{id}")
    public String eliminaUtente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.eliminaUtente(id);
        redirectAttributes.addFlashAttribute("successMessage", "Utente eliminato con successo!");
        return "redirect:/utenti";
    }

    // =============================================
    // ADMIN: GESTIONE ISCRIZIONI DI QUESTO SINGOLO UTENTE
    // =============================================

    @GetMapping("/utenti/{id}/corsi")
    public String gestioneIscrizioniUtente(@PathVariable Long id, Model model) {
        User utente = userService.trovaPerid(id);
        List<Activity> tuttiCorsi = activityService.trovaTutti();
        List<Activity> corsiIscritti = utente.getActivities();

        // Anche qui: Prendo "tutti i corsi", li filtro escludendo quelli a cui è "già
        // iscritto",
        // e ottengo i "corsi disponibili".
        List<Activity> corsiDisponibili = tuttiCorsi.stream()
                .filter(c -> !corsiIscritti.contains(c))
                .collect(Collectors.toList());

        model.addAttribute("utente", utente);
        model.addAttribute("corsiIscritti", corsiIscritti);
        model.addAttribute("corsiDisponibili", corsiDisponibili);
        return "gestione-iscrizioni";
    }

    @PostMapping("/utenti/{uid}/corsi/iscrivi/{cid}")
    public String adminIscriviUtente(@PathVariable("uid") Long uid,
            @PathVariable("cid") Long cid,
            RedirectAttributes redirectAttributes) {
        User utente = userService.trovaPerid(uid);
        Activity corso = activityService.trovaPerId(cid);

        userService.iscriviACorso(utente, corso);
        redirectAttributes.addFlashAttribute("successMessage", "Utente iscritto al corso!");
        return "redirect:/utenti/" + uid + "/corsi";
    }

    @PostMapping("/utenti/{uid}/corsi/rimuovi/{cid}")
    public String adminRimuoviUtente(@PathVariable("uid") Long uid,
            @PathVariable("cid") Long cid,
            RedirectAttributes redirectAttributes) {
        User utente = userService.trovaPerid(uid);
        Activity corso = activityService.trovaPerId(cid);

        userService.rimuoviDaCorso(utente, corso);
        redirectAttributes.addFlashAttribute("successMessage", "Iscrizione rimossa!");
        return "redirect:/utenti/" + uid + "/corsi";
    }
}
