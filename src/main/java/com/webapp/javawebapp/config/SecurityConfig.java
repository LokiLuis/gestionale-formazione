package com.webapp.javawebapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * La classe "Guardiano" dell'applicazione. (Spring Security)
 * Decide CHI può fare COSA, e previene accessi non autorizzati.
 * 
 * @EnableWebSecurity trasformerà in automatico la nostra logica nel filtro di
 *                    sicurezza di Spring.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Il 'SecurityFilterChain' è come il "Buttafuori" della discoteca.
     * Guarda chi sei, e decide in quale sala puoi entrare.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Risorse pubbliche (La strada, chiunque può camminarci, NON serve il login)
                        // Permettiamo CSS, JavaScript, Login e Registrazione.
                        .requestMatchers("/login", "/registrazione", "/css/**", "/js/**").permitAll()

                        // 2. Area ADMIN (Stanza VIP, solo gli Admin)
                        // 'hasRole' controlla nel DB se la tua lista ruoli contiene "ROLE_ADMIN".
                        .requestMatchers("/utenti/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. Area FORMAZIONE
                        // Chi crea/modifica/elimina i corsi? -> SOLO ADMIN
                        .requestMatchers("/corsi/nuovo", "/corsi/modifica/**", "/corsi/salva", "/corsi/elimina/**")
                        .hasRole("ADMIN")
                        // Chi può VEDERE la lista degli iscritti di un corso nel dettaglio? -> ADMIN o
                        // FORMATORE
                        .requestMatchers("/corsi/*/iscritti").hasAnyRole("ADMIN", "FORMATORE")
                        // Chi può AGGIUNGERE iscritti dentro un corso? -> SOLO ADMIN (Non abbiamo
                        // autorizzato formatore)
                        .requestMatchers("/corsi/*/iscritti/**").hasRole("ADMIN")
                        // Dashboard Formatore -> Accessibile al FORMATORE e all'ADMIN in delega
                        .requestMatchers("/formatore/**").hasAnyRole("ADMIN", "FORMATORE")
                        .requestMatchers("/attivita/nuova", "/attivita/salva").hasRole("ADMIN")

                        // 4. Tutto il resto (La pista da ballo centrale)
                        // Per tutte le altre pagine non mappate sopra... BASTA ESSERE DENTRO IL LOCALE
                        // (Aver fatto login)
                        .anyRequest().authenticated())

                // Configurazione del Form di Login integrato di Spring Security
                .formLogin(form -> form
                        .loginPage("/login") // "La pagina grafica del login si trova al nostro url /login"
                        .defaultSuccessUrl("/", true) // "Dove ti mando dopo che entri con successo? Sulla Home ( / )"
                        .permitAll()) // Il form stesso lo può vedere chiunque

                // Configurazione Logout
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    /**
     * L'encoder password scelto da noi.
     * BCrypt è un algoritmo di hashing fortissimo attualmente standard (con tecnica
     * 'salt' incorporata).
     * Ogni volta che salveremo una psw o faremo login, Spring UserDetailsService lo
     * chiamerà automaticamente!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}