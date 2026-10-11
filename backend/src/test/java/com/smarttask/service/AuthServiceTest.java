package com.smarttask.service;

import com.smarttask.dto.ApiDtos.RegisterRequest;
import com.smarttask.entity.User;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Test void registrationStoresOnlyAnEncodedPassword() {
        UserRepository users = mock(UserRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        AuthService service = new AuthService(users, encoder, mock(JwtService.class));
        when(users.findByEmail("ari@example.com")).thenReturn(Optional.empty());
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(new RegisterRequest("Ari", " Ari@Example.com ", "password-123"));

        var captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        assertNotEquals("password-123", captor.getValue().getPasswordHash());
        assertTrue(encoder.matches("password-123", captor.getValue().getPasswordHash()));
        assertEquals("ari@example.com", captor.getValue().getEmail());
    }
}
