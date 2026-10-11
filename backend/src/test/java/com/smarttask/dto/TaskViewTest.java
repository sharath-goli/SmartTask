package com.smarttask.dto;

import com.smarttask.entity.Task;
import com.smarttask.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskViewTest {
    @Test void classifiesAllFourQuadrantsFromImportanceAndUrgency() {
        User owner = new User("Ari", "ari@example.com", "hash");
        assertEquals("DO_FIRST", view(owner, true, true).quadrant());
        assertEquals("SCHEDULE", view(owner, true, false).quadrant());
        assertEquals("DELEGATE", view(owner, false, true).quadrant());
        assertEquals("REDUCE", view(owner, false, false).quadrant());
    }

    private ApiDtos.TaskView view(User owner, boolean important, boolean urgent) {
        return ApiDtos.TaskView.of(new Task(owner, "Task", null, Task.Priority.MEDIUM,
                null, null, important, urgent, "", Task.Recurrence.NONE, null));
    }
}
