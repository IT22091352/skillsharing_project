package backend.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enrollments")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = false)
    private Course course;
    
    @Column(nullable = false)
    private LocalDateTime enrollmentDate;
    
    @Column(nullable = false)
    private int lastCompletedUnit = 0;
    
    @Column(nullable = false)
    private boolean completed = false;
    
    @OneToOne(mappedBy = "enrollment", fetch = FetchType.LAZY)
    private Certificate certificate;
    
    // Default constructor
    public Enrollment() {
        this.enrollmentDate = LocalDateTime.now();
    }
    
    // Parameterized constructor
    public Enrollment(User user, Course course) {
        this.user = user;
        this.course = course;
        this.enrollmentDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public int getLastCompletedUnit() {
        return lastCompletedUnit;
    }
    
    public void setLastCompletedUnit(int lastCompletedUnit) {
        this.lastCompletedUnit = lastCompletedUnit;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public Certificate getCertificate() {
        return certificate;
    }
    
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
    
    // Helper method to update progress
    public void updateProgress(int unitIndex, int totalUnits) {
        if (unitIndex > lastCompletedUnit) {
            this.lastCompletedUnit = unitIndex;
        }
        
        if (lastCompletedUnit >= totalUnits - 1) {
            this.completed = true;
        }
    }
}
