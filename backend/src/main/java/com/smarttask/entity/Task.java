package com.smarttask.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name="tasks", indexes={@Index(name="idx_task_user_due", columnList="user_id,dueDate")})
public class Task {
    public enum Status { TODO, IN_PROGRESS, DONE }
    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Recurrence { NONE, DAILY, WEEKLY, MONTHLY }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id", nullable=false) private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="parent_id") private Task parent;
    @Column(nullable=false, length=180) private String title;
    @Column(length=3000) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status=Status.TODO;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Priority priority=Priority.MEDIUM;
    private LocalDate dueDate;
    private Integer estimateMinutes;
    private Boolean important=false;
    private Boolean urgent=false;
    @Column(length=500) private String tags="";
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Recurrence recurrence=Recurrence.NONE;
    @Column(nullable=false) private Instant createdAt=Instant.now();
    private Instant completedAt;
    protected Task() {}
    public Task(User user,String title,String description,Priority priority,LocalDate dueDate,Integer estimateMinutes,Boolean important,Boolean urgent,String tags,Recurrence recurrence,Task parent){this.user=user;this.title=title;this.description=description;this.priority=priority;this.dueDate=dueDate;this.estimateMinutes=estimateMinutes;this.important=important;this.urgent=urgent;this.tags=tags==null?"":tags;this.recurrence=recurrence;this.parent=parent;}
    public void update(String title,String description,Status status,Priority priority,LocalDate dueDate,Integer estimateMinutes,Boolean important,Boolean urgent,String tags,Recurrence recurrence,Task parent){this.title=title;this.description=description;this.status=status;this.priority=priority;this.dueDate=dueDate;this.estimateMinutes=estimateMinutes;this.important=important;this.urgent=urgent;this.tags=tags==null?"":tags;this.recurrence=recurrence;this.parent=parent;this.completedAt=status==Status.DONE?(completedAt==null?Instant.now():completedAt):null;}
    public void setStatus(Status status){this.status=status;this.completedAt=status==Status.DONE?Instant.now():null;}
    public Long getId(){return id;} public User getUser(){return user;} public Task getParent(){return parent;} public String getTitle(){return title;} public String getDescription(){return description;} public Status getStatus(){return status;} public Priority getPriority(){return priority;} public LocalDate getDueDate(){return dueDate;} public Integer getEstimateMinutes(){return estimateMinutes;} public Boolean getImportant(){return important;} public Boolean getUrgent(){return urgent;} public String getTags(){return tags;} public Recurrence getRecurrence(){return recurrence;} public Instant getCreatedAt(){return createdAt;} public Instant getCompletedAt(){return completedAt;}
}
