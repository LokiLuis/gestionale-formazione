package com.webapp.javawebapp.dto;

import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DtoMapper è una classe "Utility" per convertire le Entità vere (dal DB) nei
 * DTO "sicuri" per il frontend.
 * Contiene solo metodi statici (puoi chiamare DtoMapper.toActivityDTO() senza
 * fare un 'new DtoMapper()').
 * Perché si usa? Per centralizzare la logica di conversione ed evitare di
 * riscriverla ovunque nei service.
 */
public class DtoMapper {

    /**
     * Mappa un oggetto Activity (dal DB) in un ActivityDTO (per il Frontend).
     * Copia i campi essenziali uno ad uno.
     */
    public static ActivityDTO toActivityDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setNome(activity.getNome());
        dto.setDescrizione(activity.getDescrizione());
        dto.setDataInizio(activity.getDataInizio());
        dto.setDataFine(activity.getDataFine());
        return dto; // L'attività non ha la lista di utenti collegati, è ferma qui.
    }

    /**
     * Mappa un oggetto User in un UserDTO.
     * SICUREZZA: Nota come NON copiamo la Password nel DTO. Così la password
     * hashata non uscirà mai dal server backend.
     */
    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        // NO password! Il campo password in UserDTO non esiste proprio.

        dto.setNome(user.getNome());
        dto.setCognome(user.getCognome());
        dto.setEmail(user.getEmail());
        dto.setTelefono(user.getTelefono());
        dto.setCodiceFiscale(user.getCodiceFiscale());
        dto.setPosizioneLavorativa(user.getPosizioneLavorativa());

        // Gestione Relazione a Uno (Role): Estraiamo solo il nome String (es.
        // "ROLE_ADMIN")
        if (user.getRole() != null) {
            dto.setRuolo(user.getRole().getNome());
        }

        // Gestione Relazione Molti a Molti (Activities):
        // Usa la sintassi Stream di Java 8 per prendere la lista di Entity,
        // applicare il metodo toActivityDTO ad ognuna (.map), e ricreare una lista
        // (collect).
        if (user.getActivities() != null) {
            List<ActivityDTO> activityDTOs = user.getActivities().stream()
                    .map(DtoMapper::toActivityDTO)
                    .collect(Collectors.toList());
            dto.setActivities(activityDTOs); // L'utente avrà la sua lista di corsi (ma in versione DTO)
        }

        return dto;
    }
}
