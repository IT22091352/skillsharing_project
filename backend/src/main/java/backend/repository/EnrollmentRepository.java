package backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.model.Course;
import backend.model.Enrollment;
import backend.model.User;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    List<Enrollment> findByUserAndCompleted(User user, boolean completed);
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    int countByUser(User user);
    int countByUserAndCompleted(User user, boolean completed);
}
