package backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.Course;
import backend.model.Enrollment;
import backend.model.User;
import backend.repository.CourseRepository;
import backend.repository.CourseUnitRepository;
import backend.repository.EnrollmentRepository;
import backend.repository.UserRepository;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin("http://localhost:3000")
public class EnrollmentController {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseUnitRepository courseUnitRepository;
    
    // Enroll in a course
    @PostMapping
    public Enrollment enrollInCourse(@RequestBody Map<String, Long> enrollmentRequest) {
        Long userId = enrollmentRequest.get("userId");
        Long courseId = enrollmentRequest.get("courseId");
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        // Check if already enrolled
        if (enrollmentRepository.findByUserAndCourse(user, course).isPresent()) {
            throw new BadRequestException("You are already enrolled in this course");
        }
        
        Enrollment enrollment = new Enrollment(user, course);
        return enrollmentRepository.save(enrollment);
    }
    
    // Get enrollments for a user
    @GetMapping("/user/{userId}")
    public List<Enrollment> getUserEnrollments(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return enrollmentRepository.findByUser(user);
    }
    
    // Get completed enrollments for a user
    @GetMapping("/user/{userId}/completed")
    public List<Enrollment> getCompletedEnrollments(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return enrollmentRepository.findByUserAndCompleted(user, true);
    }
    
    // Get enrollment details
    @GetMapping("/{enrollmentId}")
    public Enrollment getEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
    }
    
    // Update progress
    @PutMapping("/{enrollmentId}/progress")
    public Enrollment updateProgress(@PathVariable Long enrollmentId, @RequestBody Map<String, Integer> progressUpdate) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        
        int unitIndex = progressUpdate.get("unitIndex");
        
        // Get total units count
        int totalUnits = courseUnitRepository.countByCourse(enrollment.getCourse());
        
        // Update progress
        enrollment.updateProgress(unitIndex, totalUnits);
        
        return enrollmentRepository.save(enrollment);
    }
    
    // Get enrollment stats for a user
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserEnrollmentStats(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        int totalEnrollments = enrollmentRepository.countByUser(user);
        int completedEnrollments = enrollmentRepository.countByUserAndCompleted(user, true);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEnrollments", totalEnrollments);
        stats.put("completedEnrollments", completedEnrollments);
        stats.put("inProgressEnrollments", totalEnrollments - completedEnrollments);
        
        return ResponseEntity.ok(stats);
    }
}
