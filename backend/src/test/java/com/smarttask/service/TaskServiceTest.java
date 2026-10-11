package com.smarttask.service;

import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    @Test void lookupIsScopedToTheAuthenticatedOwner() {
        TaskRepository tasks = mock(TaskRepository.class);
        TaskService service = new TaskService(tasks, mock(UserRepository.class));
        when(tasks.findByIdAndUserId(42L, 7L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.status(7L, 42L,
                com.smarttask.entity.Task.Status.DONE));
        verify(tasks).findByIdAndUserId(42L, 7L);
    }
}
