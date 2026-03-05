package com.webapp.javawebapp.service;

import com.webapp.javawebapp.dto.RegistrazioneDTO;
import com.webapp.javawebapp.dto.UtenteFormDTO;
import com.webapp.javawebapp.exception.DuplicateResourceException;
import com.webapp.javawebapp.exception.ResourceNotFoundException;
import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.model.Role;
import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.repository.RoleRepository;
import com.webapp.javawebapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service per la logica di business degli Utenti.
 * Centralizza tutte le creazioni, aggiornamenti e l'encryption delle password.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Generato nel nostro SecurityConfig

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =============================================
    // LOOKUP (Metodi per cercare utenti)
    // =============================================

    public User trovaPerid(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", id));
    }

    /**
     * Molto utile dopo che l'utente effettua il login, Spring salva nella sessione
     * solo l'username.
     * Noi lo cerchiamo da questo service per accedere al suo ID o i suoi corsi
     * originari.
     */
    public User trovaPerUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato: " + username));
    }

    public List<User> trovaTutti() {
        return userRepository.findAll();
    }

    public List<User> filtraPerRuolo(Long roleId) {
        return userRepository.findByRoleId(roleId);
    }

    public List<User> filtraPerAttivita(Long activityId) {
        return userRepository.findByActivitiesId(activityId); // Ricarica gli utenti di quel corso
    }

    public List<User> trovaFormatori() {
        return userRepository.findByRoleNome("ROLE_FORMATORE"); // "ROLE_FORMATORE" cablato nel DataInitializer
    }

    public List<Role> trovaTuttiRuoli() {
        return roleRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // =============================================
    // REGISTRAZIONE PUBBLICA
    // =============================================

    /**
     * Chiamato dal Controller durante la registrazione pubblica.
     * Riceve un DTO compilato e "assembla" l'Entità vera e propria da mandare al
     * database.
     */
    @Transactional
    public User registraUtente(RegistrazioneDTO dto) {
        // Controllo business (Spring non può aiutarci con la validation su duplicati
        // nel DB, qui tocca farla noi)
        if (userRepository.existsByUsername(dto.getUsername())) {
            // Lancia ecc custom -> GlobalExceptionHandler la cattura -> error.html
            throw new DuplicateResourceException(
                    "Lo username '" + dto.getUsername() + "' è già in uso. Scegline un altro.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());

        // MAI IN CHIARO. Usiamo BCrypt per cifrarla prima che vada su PostgreSQL.
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setNome(dto.getNome());
        user.setCognome(dto.getCognome());
        user.setCodiceFiscale(dto.getCodiceFiscale());
        user.setTelefono(dto.getTelefono());
        user.setEmail(dto.getEmail());

        // La registrazione pubblica dà di base solo il ruolo UTENTE (livello più
        // basso).
        roleRepository.findByNome("ROLE_UTENTE").ifPresent(user::setRole);

        return userRepository.save(user); // Salva genererà l'Entity
    }

    // =============================================
    // CRUD UTENTI (LATO ADMIN)
    // =============================================

    /**
     * Quando l'admin compila il form "Nuovo utente". Questo usa UtenteFormDTO al
     * posto di RegistrazioneDTO.
     */
    @Transactional
    public User creaUtente(UtenteFormDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException(
                    "Lo username '" + dto.getUsername() + "' è già in uso. Scegline un altro.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Copia il resto dei campi tramite helper dedicato
        copiaAttributiDaDTO(user, dto);

        return userRepository.save(user);
    }

    /**
     * Quando l'admin modifica un utente già esistente (ID presente).
     * Non altera lo username ne tantomeno la password.
     */
    @Transactional
    public User aggiornaUtente(UtenteFormDTO dto) {
        User user = trovaPerid(dto.getId()); // Tira fuori dal DB l'utente vecchio
        copiaAttributiDaDTO(user, dto); // Aggiorna nome, cognome, ruolo...
        return userRepository.save(user); // JPA capisce che avendo un ID, questa è una modifica (UPDATE)
    }

    @Transactional
    public void eliminaUtente(Long id) {
        userRepository.deleteById(id);
    }

    // =============================================
    // GESTIONE ISCRIZIONI INDIVIDUALI (Pulsante "Iscriviti")
    // =============================================

    @Transactional
    public void iscriviACorso(User utente, Activity corso) {
        // Evitiamo che ci si iscriva 2 volte per sbaglio.
        if (!utente.getActivities().contains(corso)) {
            utente.getActivities().add(corso); // DOMINANTE. Basta aggiungere alla lista, JPA esegue la query della
                                               // Tabella Ponte.
            userRepository.save(utente);
        }
    }

    @Transactional
    public void rimuoviDaCorso(User utente, Activity corso) {
        utente.getActivities().remove(corso); // Rimuove dalla lista. JPA pulisce la riga ponte.
        userRepository.save(utente);
    }

    // =============================================
    // HELPER PRIVATI (Utilità interamente al Service)
    // =============================================

    /**
     * Metodo helper per non ripetere codice su Crea e Modifica Utente (lato Admin)
     */
    private void copiaAttributiDaDTO(User user, UtenteFormDTO dto) {
        user.setNome(dto.getNome());
        user.setCognome(dto.getCognome());
        user.setEmail(dto.getEmail());
        user.setTelefono(dto.getTelefono());
        user.setCodiceFiscale(dto.getCodiceFiscale());
        user.setPosizioneLavorativa(dto.getPosizioneLavorativa());

        if (dto.getRoleId() != null) {
            roleRepository.findById(dto.getRoleId()).ifPresent(user::setRole);
        }
    }
}
