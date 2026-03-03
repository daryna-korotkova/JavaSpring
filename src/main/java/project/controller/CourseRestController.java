package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.dto.CourseDTO;
import project.model.Course;
import project.repository.CourseRepository;
import project.service.UserService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseRestController {
    private final CourseRepository courseRepository;
    private final UserService userService;

    @GetMapping("/{id}")
    public CourseDTO getCourseDetails(@PathVariable Long id) {
        Course course = courseRepository.findById(id).orElseThrow();
        Double rate = userService.getUsdRate(); // беремо курс з сервісу

        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setPriceUsd(course.getPriceUsd());
        dto.setPriceUah(course.getPriceUsd() * rate);
        return dto;
    }
}