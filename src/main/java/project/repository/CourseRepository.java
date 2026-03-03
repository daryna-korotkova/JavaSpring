package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Client;
import project.model.Course;
import project.model.Order;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {}