package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.dto.CourseDTO;
import project.model.Course;
import project.repository.CourseRepository;
import project.service.UserService;
import project.service.HtmlParserService; // Не забудьте імпортувати парсер

import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseRestController {

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final HtmlParserService htmlParserService; // Впроваджуємо сервіс парсингу

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseDetails(@PathVariable Long id) {
        // Знаходимо курс в базі
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Курс не знайдено"));

        // Отримуємо актуальний курс валют через API ПриватБанку
        Double rate = userService.getUsdRate();

        // ВИКОРИСТАННЯ ПАРСЕРА: Отримуємо технічну довідку з Вікіпедії
        // Парсер бере назву курсу (наприклад, "Java") і шукає статтю
        String wikiInfo = htmlParserService.searchInWeb(course.getCourseName());

        // Заповнюємо DTO (Data Transfer Object)
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setDisciplineName(course.getDisciplineName());
        dto.setDescription(course.getDescription());
        dto.setPriceUsd(course.getPriceUsd());
        dto.setPriceUah(course.getPriceUsd() * rate);
        dto.setWikiInfo(wikiInfo); // Передаємо розпарсений текст

        return ResponseEntity.ok(dto);
    }
}