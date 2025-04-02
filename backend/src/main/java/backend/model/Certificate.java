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
@Table(name = "certificates")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Certificate {
    
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
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    @JsonIdentityReference(alwaysAsId = true)
    private Enrollment enrollment;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;
    
    @Column(name = "certificate_number", nullable = false, unique = true)
    private String certificateNumber;
    
    // Default constructor
    public Certificate() {
        this.issueDate = LocalDateTime.now();
    }
    
    // Parameterized constructor
    public Certificate(User user, Course course, Enrollment enrollment, String certificateNumber) {
        this.user = user;
        this.course = course;
        this.enrollment = enrollment;
        this.certificateNumber = certificateNumber;
        this.issueDate = LocalDateTime.now();
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
    
    public Enrollment getEnrollment() {
        return enrollment;
    }
    
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
    
    public LocalDateTime getIssueDate() {
        return issueDate;
    }
    
    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }
    
    public String getCertificateNumber() {
        return certificateNumber;
    }
    
    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }
}
