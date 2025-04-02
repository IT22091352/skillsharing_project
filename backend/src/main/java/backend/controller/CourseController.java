package backend.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.exception.BadRequestException;
import backend.exception.ResourceNotFoundException;
import backend.model.Course;
import backend.model.CourseUnit;
import backend.model.User;
import backend.repository.CourseRepository;
import backend.repository.CourseUnitRepository;
import backend.repository.UserRepository;
import backend.service.FileStorageService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin("http://localhost:3000")
public class CourseController {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseUnitRepository courseUnitRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;
    
    // Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    // Create a new course with file upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Course createCourse(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("authorId") Long authorId,
            @RequestParam("units") String unitsJson,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) throws IOException {
        
        // Find the author
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
        
        // Create course
        Course course = new Course(title, description, author);
        course.setCategory(category);
        
        // Handle PDF file if present
        if (pdfFile != null && !pdfFile.isEmpty()) {
            // Store the file and get its URL
            String fileUrl = fileStorageService.storePdfFile(pdfFile);
            course.setPdfFileName(pdfFile.getOriginalFilename());
            course.setPdfFileUrl(fileUrl);
        }
        
        // Extract and add units
        List<Map<String, String>> units = objectMapper.readValue(unitsJson, new TypeReference<List<Map<String, String>>>() {});
        
        if (units != null) {
            int orderIndex = 0;
            for (Map<String, String> unitData : units) {
                String unitTitle = unitData.get("title");
                String unitContent = unitData.get("content");
                
                if (unitTitle != null && unitContent != null) {
                    CourseUnit unit = new CourseUnit(unitTitle, unitContent, orderIndex++);
                    course.addUnit(unit);
                }
            }
        }
        
        return courseRepository.save(course);
    }
    
    // Get course by ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return ResponseEntity.ok(course);
    }
    
    // Update course with file
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("requestedByUserId") Long requestedByUserId,
            @RequestParam("units") String unitsJson,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestParam(value = "replacePdf", required = false, defaultValue = "false") Boolean replacePdf) throws IOException {
            
        // Find the course
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        // Update course details
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        
        // Handle PDF file updates
        if (pdfFile != null && !pdfFile.isEmpty()) {
            // Store the new file and update course
            String fileUrl = fileStorageService.storePdfFile(pdfFile);
            course.setPdfFileName(pdfFile.getOriginalFilename());
            course.setPdfFileUrl(fileUrl);
        } else if (Boolean.TRUE.equals(replacePdf)) {
            // If replacePdf is true but no file is provided, remove the existing PDF
            course.setPdfFileName(null);
            course.setPdfFileUrl(null);
        }
        
        // Handle units updates - first clear existing units
        courseUnitRepository.deleteAll(course.getUnits());
        course.getUnits().clear();
        
        // Add updated units
        List<Map<String, String>> units = objectMapper.readValue(unitsJson, new TypeReference<List<Map<String, String>>>() {});
        
        if (units != null) {
            int orderIndex = 0;
            for (Map<String, String> unitData : units) {
                String unitTitle = unitData.get("title");
                String unitContent = unitData.get("content");
                
                if (unitTitle != null && unitContent != null) {
                    CourseUnit unit = new CourseUnit(unitTitle, unitContent, orderIndex++);
                    course.addUnit(unit);
                }
            }
        }
        
        Course updatedCourse = courseRepository.save(course);
        return ResponseEntity.ok(updatedCourse);
    }
    
    // Delete course
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id, @RequestParam Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        // Verify that the requester is the author
        if (!course.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("Only the course author can delete this course");
        }
        
        courseRepository.delete(course);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        
        return ResponseEntity.ok(response);
    }
    
    // Get courses by author
    @GetMapping("/author/{authorId}")
    public List<Course> getCoursesByAuthor(@PathVariable Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
        
        return courseRepository.findByAuthor(author);
    }
    
    // Search courses by title
    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam String query) {
        return courseRepository.findByTitleContainingIgnoreCase(query);
    }
    
    // Get units for a course
    @GetMapping("/{courseId}/units")
    public List<CourseUnit> getCourseUnits(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        return courseUnitRepository.findByCourseOrderByOrderIndexAsc(course);
    }
    
    // Get specific unit in a course
    @GetMapping("/{courseId}/units/{unitIndex}")
    public CourseUnit getCourseUnit(@PathVariable Long courseId, @PathVariable int unitIndex) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        List<CourseUnit> units = courseUnitRepository.findByCourseOrderByOrderIndexAsc(course);
        
        if (unitIndex < 0 || unitIndex >= units.size()) {
            throw new ResourceNotFoundException("Unit not found with index: " + unitIndex);
        }
        
        return units.get(unitIndex);
    }
}
