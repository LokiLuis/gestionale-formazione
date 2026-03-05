package com.webapp.javawebapp.service;

import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * Questa classe è il cuore del LOGIN di Spring Security.
 * Spring di default non sa in quale database salviamo i nostri utenti.
 * Implementando UserDetailsService gli insegniamo noi COME andare a prendere un
 * utente (dal DB JPA).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Spring Security chiama questo metodo (in automatico!) ogni volta che qualcuno
     * prova a fare login.
     * Gli passa lo username digitato nel form HTML.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Cerchiamo l'utente nel NOSTRO database tramite username.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        // 2. Prendiamo il suo ruolo (es. "ROLE_ADMIN") e lo impacchettiamo nella classe
        // 'Authority' che piace a Spring.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getNome());

        // 3. Creiamo l'oggetto org.springframework.security.core.userdetails.User a
        // partire dai nostri dati.
        // Spring prenderà in consegna questo User e comparerà DA SOLO la psw hashata
        // (non dobbiamo farlo noi).
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Passiamo la psw hashata dal database, Spring sa usare BCrypt per
                                    // confrontarla.
                Collections.singletonList(authority) // Lista dei ruoli (noi ne abbiamo uno a testa)
        );
    }
}