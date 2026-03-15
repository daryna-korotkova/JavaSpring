package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.model.CourseModel;
import project.model.Course;
import project.repository.CourseRepository;
import project.service.UserService;
import project.service.HtmlParserService;


@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseRestController {

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final HtmlParserService htmlParserService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseModel> getCourseDetails(@PathVariable Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Double rate = userService.getUsdRate();

        String wikiInfo = htmlParserService.searchInWeb(course.getCourseName());

        CourseModel dto = new CourseModel();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setDisciplineName(course.getDisciplineName());
        dto.setDescription(course.getDescription());
        dto.setPriceUsd(course.getPriceUsd());
        dto.setPriceUah(course.getPriceUsd() * rate);
        dto.setWikiInfo(wikiInfo);

        return ResponseEntity.ok(dto);
    }
}