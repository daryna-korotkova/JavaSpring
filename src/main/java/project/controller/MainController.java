package project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import project.service.UserService;
import project.repository.CourseRepository;
import project.model.Client;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    private final CourseRepository courseRepository;

    @GetMapping("/")
    public String loginPage() {
        System.out.println("Hello world!");
        return "login"; // сторінка входу
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam(required = false) String firstName,
                              @RequestParam(required = false) String lastName,
                              Model model) {
        Client client = userService.loginOrRegister(email, firstName, lastName);
        model.addAttribute("client", client);
        model.addAttribute("courses", courseRepository.findAll());
        return "courses"; // перехід до списку курсів
    }
}
