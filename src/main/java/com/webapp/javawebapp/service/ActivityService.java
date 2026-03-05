package com.webapp.javawebapp.service;

import com.webapp.javawebapp.exception.ResourceNotFoundException;
import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.repository.ActivityRepository;
import com.webapp.javawebapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service per la logica di business dei Corsi / Attività formative.
 * 
 * @Service dice a Spring: "Crea una sola istanza di questa classe e tienila in
 *          memoria".
 *          È qui dentro che scriviamo le vere regole della nostra applicazione,
 *          mantenendo
 *          i Controller snelli e puliti.
 */
@Service
public class ActivityService {

    // Dependency Injection: Spring inietta automaticamente i repository dal
    // costruttore.
    // Usiamo sempre variabili 'final' per assicurarci che non cambino dopo l'avvio.
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository,
            UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    // =============================================
    // LOOKUP (Letture dal DB)
    // =============================================

    /**
     * Trova un corso per ID, se non c'è lancia subito la nostra Eccezione Custom.
     * È il pattern preferito in Spring: fail-fast (fermati subito se c'è un
     * errore).
     */
    public Activity trovaPerId(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corso", id));
    }

    public List<Activity> trovaTutti() {
        return activityRepository.findAll();
    }

    /**
     * Cerca i corsi il cui nome contiene una certa stringa.
     * Se la stringa è vuota, restituisce tutti i corsi.
     */
    public List<Activity> cercaPerNome(String ricerca) {
        if (ricerca != null && !ricerca.isBlank()) {
            return activityRepository.findByNomeContainingIgnoreCase(ricerca);
        }
        return activityRepository.findAll(); // Ritorna tutto se non cerchi nulla
    }

    public List<Activity> trovaCorsiFormatore(Long formatoreId) {
        return activityRepository.findByFormatoreId(formatoreId);
    }

    // =============================================
    // CRUD CORSI (Modifiche al DB)
    // =============================================

    /**
     * @Transactional: Se questa operazione fallisce a metà (es. salta la luce sul
     *                 server),
     *                 fa un "rollback" annullando tutto (non salva nulla a metà sul
     *                 database db).
     *                 Qui salviamo un corso e cerchiamo il Formatore associato
     *                 tramite l'ID passato.
     */
    @Transactional
    public Activity salvaCorso(Activity activity, Long formatoreId) {
        // Se l'admin ha selezionato un formatore dal menu a tendina...
        if (formatoreId != null) {
            // ...lo cerchiamo e se esiste lo impostiamo nel corso
            userRepository.findById(formatoreId).ifPresent(activity::setFormatore);
        } else {
            // Altrimenti il corso non ha nessun formatore assegnato.
            activity.setFormatore(null);
        }
        return activityRepository.save(activity);
    }

    @Transactional
    public void eliminaCorso(Long id) {
        activityRepository.deleteById(id);
    }

    // =============================================
    // ISCRIZIONI (La logica delle associazioni)
    // =============================================

    /**
     * Ritorna tutti gli utenti iscritti al corso.
     */
    public List<User> trovaIscrittiCorso(Long corsoId) {
        Activity corso = trovaPerId(corsoId);
        return corso.getUsers(); // JPA Navigability: naviga la relazione molti-a-molti in automatico!
    }

    /**
     * Iscrizione di massa: Iscrive N utenti contemporaneamente allo stesso corso.
     */
    @Transactional
    public void iscriviMassivo(Long corsoId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty())
            return; // Esci subito se non c'è nessuno da iscrivere

        Activity corso = trovaPerId(corsoId);

        // Per ogni ID utente arrivato dalla checkbox HTML...
        for (Long uid : userIds) {
            User utente = userRepository.findById(uid)
                    .orElseThrow(() -> new ResourceNotFoundException("Utente", uid));

            // ...se l'utente non è già iscritto a questo corso
            if (!utente.getActivities().contains(corso)) {
                utente.getActivities().add(corso); // Aggiungi il corso ALL'UTENTE (perché è lato dominante)
                userRepository.save(utente); // Salva l'utente per generare la riga nella tabella ponte
            }
        }
    }

    /**
     * Logica di business: L'admin vede solo gli utenti NON ancora iscritti
     * per evitare di mostrare checkbox di persone già presenti nel corso.
     */
    public List<User> trovaUtentiDisponibili(Long corsoId) {
        Activity corso = trovaPerId(corsoId);
        List<User> iscritti = corso.getUsers();
        List<User> tutti = userRepository.findAll();

        return tutti.stream()
                // Tieni solo quelli che NON sono presenti nella lista degli iscritti
                .filter(u -> !iscritti.contains(u))
                .collect(Collectors.toList());
    }

    /**
     * Un Formatore cerca solo tra i propri corsi assegnati.
     */
    public List<Activity> cercaCorsiFormatore(Long formatoreId, String ricerca) {
        List<Activity> corsi = activityRepository.findByFormatoreId(formatoreId); // 1. Prendi i suoi corsi

        if (ricerca != null && !ricerca.isBlank()) {
            // 2. Filtra la lista creata sopra usando Java Streams
            corsi = corsi.stream()
                    .filter(c -> c.getNome().toLowerCase().contains(ricerca.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return corsi;
    }

    /**
     * Filtra le "Mie Attività" (utente base) per nome.
     */
    public List<Activity> cercaAttivitaUtente(User utente, String ricerca) {
        List<Activity> attivita = utente.getActivities();
        if (ricerca != null && !ricerca.isBlank()) {
            attivita = attivita.stream()
                    .filter(a -> a.getNome().toLowerCase().contains(ricerca.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return attivita;
    }

    /**
     * Ritorna gli ID (tipicamente serve ai Checkbox HTML per sapere chi spuntare o
     * disabilitare).
     */
    public List<Long> trovaIdCorsiIscritto(User utente) {
        if (utente == null)
            return Collections.emptyList();

        return utente.getActivities().stream()
                .map(Activity::getId) // Trasforma una lista di Activity in una lista di Long (ID)
                .collect(Collectors.toList());
    }
}
