package com.webapp.javawebapp.service;

import com.webapp.javawebapp.exception.ResourceNotFoundException;
import com.webapp.javawebapp.model.Activity;
import com.webapp.javawebapp.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    private Activity corsoJava;

    @BeforeEach
    void setUp() {
        corsoJava = new Activity();
        corsoJava.setId(10L);
        corsoJava.setNome("Corso Java Plus");
        corsoJava.setDescrizione("Descrizione Corso Java");
    }

    @Test
    void trovaTutti_ReturnsList() {
        when(activityRepository.findAll()).thenReturn(Arrays.asList(corsoJava));

        List<Activity> result = activityService.trovaTutti();

        assertEquals(1, result.size());
        assertEquals("Corso Java Plus", result.get(0).getNome());
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    void trovaPerId_Success() {
        when(activityRepository.findById(10L)).thenReturn(Optional.of(corsoJava));

        Activity result = activityService.trovaPerId(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(activityRepository, times(1)).findById(10L);
    }

    @Test
    void trovaPerId_NotFound_ThrowsException() {
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> activityService.trovaPerId(99L));
        verify(activityRepository, times(1)).findById(99L);
    }
}
