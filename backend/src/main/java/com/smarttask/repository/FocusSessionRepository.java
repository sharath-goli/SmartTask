package com.smarttask.repository;
import com.smarttask.entity.FocusSession; import org.springframework.data.jpa.repository.JpaRepository; import java.time.Instant; import java.util.List;
public interface FocusSessionRepository extends JpaRepository<FocusSession,Long>{ List<FocusSession> findByUserIdAndStartedAtAfterOrderByStartedAtAsc(Long userId,Instant after); }
