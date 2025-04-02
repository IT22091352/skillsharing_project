package backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.Certificate;
import backend.model.Course;
import backend.model.Enrollment;
import backend.model.User;
import backend.repository.CertificateRepository;
import backend.repository.CourseRepository;
import backend.repository.EnrollmentRepository;
import backend.repository.UserRepository;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin("http://localhost:3000")
public class CertificateController {
    
    @Autowired
    private CertificateRepository certificateRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    // Generate a certificate
    @PostMapping
    public Certificate generateCertificate(@RequestBody Map<String, Long> certificateRequest) {
        Long userId = certificateRequest.get("userId");
        Long courseId = certificateRequest.get("courseId");
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        // Check if user has completed the course
        Enrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for this user and course"));
        
        if (!enrollment.isCompleted()) {
            throw new BadRequestException("You have not completed this course yet");
        }
        
        // Check if certificate already exists
        if (certificateRepository.findByUserAndCourse(user, course).isPresent()) {
            throw new BadRequestException("Certificate already exists for this course");
        }
        
        // Generate certificate number
        String certificateNumber = UUID.randomUUID().toString();
        
        Certificate certificate = new Certificate(user, course, enrollment, certificateNumber);
        return certificateRepository.save(certificate);
    }
    
    // Get certificates for a user
    @GetMapping("/user/{userId}")
    public List<Certificate> getUserCertificates(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return certificateRepository.findByUser(user);
    }
    
    // Get certificate by ID
    @GetMapping("/{id}")
    public Certificate getCertificate(@PathVariable Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + id));
    }
    
    // Verify certificate
    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<Certificate> verifyCertificate(@PathVariable String certificateNumber) {
        Certificate certificate = certificateRepository.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with number: " + certificateNumber));
        
        return ResponseEntity.ok(certificate);
    }
}
