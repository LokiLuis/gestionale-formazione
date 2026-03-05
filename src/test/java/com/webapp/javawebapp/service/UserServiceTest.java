package com.webapp.javawebapp.service;

import com.webapp.javawebapp.dto.RegistrazioneDTO;
import com.webapp.javawebapp.exception.DuplicateResourceException;
import com.webapp.javawebapp.exception.ResourceNotFoundException;
import com.webapp.javawebapp.model.User;
import com.webapp.javawebapp.repository.RoleRepository;
import com.webapp.javawebapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setNome("Mario");
        adminUser.setCognome("Rossi");
    }

    @Test
    void trovaPerid_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        User result = userService.trovaPerid(1L);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void trovaPerid_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.trovaPerid(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void registraUtente_Success() {
        RegistrazioneDTO dto = new RegistrazioneDTO();
        dto.setUsername("nuovo");
        dto.setPassword("pass");
        dto.setNome("Test");
        dto.setCognome("User");

        when(userRepository.existsByUsername("nuovo")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        User result = userService.registraUtente(dto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("nuovo", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registraUtente_DuplicateUsername_ThrowsException() {
        RegistrazioneDTO dto = new RegistrazioneDTO();
        dto.setUsername("admin");

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.registraUtente(dto));
        verify(userRepository, never()).save(any(User.class));
    }
}
