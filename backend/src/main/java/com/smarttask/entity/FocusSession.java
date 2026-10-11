package com.smarttask.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="focus_sessions")
public class FocusSession {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="task_id") private Task task;
    @Column(nullable=false) private Integer durationMinutes;
    @Column(nullable=false) private Instant startedAt;
    @Column(nullable=false) private Instant endedAt;
    protected FocusSession(){}
    public FocusSession(User user,Task task,int duration,Instant start,Instant end){this.user=user;this.task=task;this.durationMinutes=duration;this.startedAt=start;this.endedAt=end;}
    public Long getId(){return id;} public Integer getDurationMinutes(){return durationMinutes;} public Instant getStartedAt(){return startedAt;} public Instant getEndedAt(){return endedAt;}
}
