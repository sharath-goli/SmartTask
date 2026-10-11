package com.smarttask.repository;
import com.smarttask.entity.Task; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TaskRepository extends JpaRepository<Task,Long>{ List<Task> findByUserIdOrderByCreatedAtDesc(Long userId); Optional<Task> findByIdAndUserId(Long id,Long userId); }
