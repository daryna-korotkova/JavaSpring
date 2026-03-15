package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {}