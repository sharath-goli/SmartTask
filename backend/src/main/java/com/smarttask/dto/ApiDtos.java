package com.smarttask.dto;
import com.smarttask.entity.Task; import jakarta.validation.constraints.*; import java.time.*; import java.util.*;
public final class ApiDtos { private ApiDtos(){}
 public record RegisterRequest(@NotBlank @Size(max=80) String name,@Email @NotBlank @Size(max=190) String email,@NotBlank @Size(min=8,max=100) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record AuthResponse(String token,UserView user){} public record UserView(Long id,String name,String email){}
 public record TaskRequest(@NotBlank @Size(max=180) String title,@Size(max=3000) String description,Task.Status status,Task.Priority priority,LocalDate dueDate,@Min(1) @Max(1440) Integer estimateMinutes,@NotNull Boolean important,@NotNull Boolean urgent,@Size(max=500) String tags,Task.Recurrence recurrence,Long parentId){}
 public record TaskView(Long id,String title,String description,Task.Status status,Task.Priority priority,LocalDate dueDate,Integer estimateMinutes,Boolean important,Boolean urgent,String quadrant,List<String> tags,Task.Recurrence recurrence,Long parentId,Instant createdAt,Instant completedAt){
  public static TaskView of(Task t){String q=Boolean.TRUE.equals(t.getImportant())?(Boolean.TRUE.equals(t.getUrgent())?"DO_FIRST":"SCHEDULE"):(Boolean.TRUE.equals(t.getUrgent())?"DELEGATE":"REDUCE");return new TaskView(t.getId(),t.getTitle(),t.getDescription(),t.getStatus(),t.getPriority(),t.getDueDate(),t.getEstimateMinutes(),t.getImportant(),t.getUrgent(),q,Arrays.stream(Optional.ofNullable(t.getTags()).orElse("").split(",")).map(String::trim).filter(s->!s.isBlank()).toList(),t.getRecurrence(),t.getParent()==null?null:t.getParent().getId(),t.getCreatedAt(),t.getCompletedAt());}
 }
 public record FocusRequest(@Min(1) @Max(600) int durationMinutes,Long taskId,@NotNull Instant startedAt,@NotNull Instant endedAt){}
 public record FocusView(Long id,Integer durationMinutes,Instant startedAt,Instant endedAt){}
}
