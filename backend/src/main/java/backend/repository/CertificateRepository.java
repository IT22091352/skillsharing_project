package backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.model.Certificate;
import backend.model.Course;
import backend.model.User;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByUser(User user);
    Optional<Certificate> findByUserAndCourse(User user, Course course);
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
}
