package com.webapp.javawebapp.config;

import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.model.Role;
import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.repository.ActivityRepository;
import com.webapp.javawebapp.repository.RoleRepository;
import com.webapp.javawebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Configuration dice a Spring Boot che questa classe contiene metodi che
 *                creano "Bean".
 *                Un Bean è un oggetto gestito direttamente da Spring.
 *                Questa classe in particolare serve a popolare il database con
 *                dati iniziali al primissimo avvio
 *                dell'Applicazione (come i ruoli base e un utente Admin).
 */
@Configuration
public class DataInitializer {

    /**
     * @BeanCommandLineRunner: Questo metodo viene eseguito in automatico da Spring
     *                         Boot
     *                         ESATTAMENTE UNA VOLTA subito dopo l'avvio del server.
     *                         Parametri: Spring inietta automaticamente i
     *                         Repository necessari qui.
     */
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
            RoleRepository roleRepository,
            ActivityRepository activityRepository,
            PasswordEncoder passwordEncoder) { // PasswordEncoder serve per crittografare la psw dell'Admin
        return args -> {

            // ========================================
            // 1. CREAZIONE DEI 3 RUOLI (Admin, Formatore, Utente base)
            // ========================================
            // findByNome(...).orElseGet(...) significa: "Cercalo nel database. Se non c'è,
            // crealo e salvalo".
            Role adminRole = roleRepository.findByNome("ROLE_ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setNome("ROLE_ADMIN");
                return roleRepository.save(r);
            });

            Role formatoreRole = roleRepository.findByNome("ROLE_FORMATORE").orElseGet(() -> {
                Role r = new Role();
                r.setNome("ROLE_FORMATORE");
                return roleRepository.save(r);
            });

            Role utenteRole = roleRepository.findByNome("ROLE_UTENTE").orElseGet(() -> {
                Role r = new Role();
                r.setNome("ROLE_UTENTE");
                return roleRepository.save(r);
            });

            System.out.println("🔑 Ruoli base assicurati nel database: ROLE_ADMIN, ROLE_FORMATORE, ROLE_UTENTE");

            // ========================================
            // 2. CREAZIONE ATTIVITÀ DI PROVA (Se il DB è vuoto)
            // ========================================
            // count() == 0 impedisce di creare cloni a ogni riavvio dell'app.
            if (activityRepository.count() == 0) {
                Activity corsoJava = new Activity();
                corsoJava.setNome("Corso Full Stack Java");
                corsoJava.setDescrizione("Impara Spring Boot da zero");
                corsoJava.setDataInizio(java.time.LocalDate.now());
                corsoJava.setDataFine(java.time.LocalDate.now().plusMonths(3));
                activityRepository.save(corsoJava);

                Activity corsoReact = new Activity();
                corsoReact.setNome("Corso React Frontend");
                corsoReact.setDescrizione("Sviluppo interfacce moderne con React");
                corsoReact.setDataInizio(java.time.LocalDate.now());
                corsoReact.setDataFine(java.time.LocalDate.now().plusMonths(2));
                activityRepository.save(corsoReact);

                System.out.println("📚 Corsi di prova iniziali creati.");
            }

            // ========================================
            // 3. CREAZIONE UTENTE SUPER ADMIN INIZIALE
            // ========================================
            // Crea un super utente solo se non esiste ancora uno chiamato 'admin'
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                // MAI salvare password in chiaro. Usiamo l'Encoder fornito da SecurityConfig!
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNome("Super");
                admin.setCognome("Admin");
                admin.setRole(adminRole); // Assegna "ROLE_ADMIN" visto sopra
                admin.setEmail("admin@gestionale.it");
                admin.setPosizioneLavorativa("Amministratore di Sistema");

                // Lo iscrive di default al primissimo corso creato
                Activity corso = activityRepository.findAll().get(0);
                admin.getActivities().add(corso);

                userRepository.save(admin);
                System.out.println("✅ ADMIN creato automaticamente (User: admin / Password: admin123)");
            }

        };
    }
}