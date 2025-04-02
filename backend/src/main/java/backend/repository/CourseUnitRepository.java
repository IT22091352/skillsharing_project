package backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.model.Course;
import backend.model.CourseUnit;

@Repository
public interface CourseUnitRepository extends JpaRepository<CourseUnit, Long> {
    List<CourseUnit> findByCourseOrderByOrderIndexAsc(Course course);
    int countByCourse(Course course);
}
