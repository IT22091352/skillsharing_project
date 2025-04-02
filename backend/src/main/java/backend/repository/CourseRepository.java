package backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.model.Course;
import backend.model.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByAuthor(User author);
    List<Course> findByTitleContainingIgnoreCase(String title);
}
