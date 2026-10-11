package com.smarttask.service;

import com.smarttask.entity.Task;
import com.smarttask.entity.User;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRecurrenceTest {
    @Test void completingWeeklyTaskCreatesNextOccurrence() {
        User owner = new User("Ari", "ari@example.com", "hash");
        LocalDate due = LocalDate.of(2026, 10, 10);
        Task task = new Task(owner, "Weekly review", "", Task.Priority.MEDIUM, due,
                30, true, false, "planning", Task.Recurrence.WEEKLY, null);
        TaskRepository tasks = mock(TaskRepository.class);
        when(tasks.findByIdAndUserId(4L, 9L)).thenReturn(Optional.of(task));
        TaskService service = new TaskService(tasks, mock(UserRepository.class));

        service.status(9L, 4L, Task.Status.DONE);

        var captor = org.mockito.ArgumentCaptor.forClass(Task.class);
        verify(tasks).save(captor.capture());
        assertEquals(due.plusWeeks(1), captor.getValue().getDueDate());
        assertEquals(Task.Status.TODO, captor.getValue().getStatus());
    }
}
