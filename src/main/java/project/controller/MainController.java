package project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              Model model) {
        try {
            Client client = userService.login(email, password);
            model.addAttribute("client", client);
            model.addAttribute("courses", courseRepository.findAll());
            return "courses";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 Model model) {
        try {
            Client client = userService.register(email, password, firstName, lastName);
            model.addAttribute("client", client);
            model.addAttribute("courses", courseRepository.findAll());
            return "courses";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/cabinet")
    public String showCabinet(@RequestParam String email, Model model) {
        Client client = userService.findByEmail(email);
        if (client == null) return "redirect:/";

        model.addAttribute("client", client);
        model.addAttribute("myOrders", client.getOrders());
        return "cabinet";
    }

    @PostMapping("/purchase")
    @ResponseBody
    public ResponseEntity<String> purchaseCourse(@RequestParam String email, @RequestParam Long courseId) {
        try {
            userService.purchaseCourse(email, courseId);
            return ResponseEntity.ok("The course has been successfully purchased!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/courses")
    public String showCourses(@RequestParam String email, Model model) {
        Client client = userService.findByEmail(email);
        if (client == null) return "redirect:/";

        model.addAttribute("client", client);
        model.addAttribute("courses", courseRepository.findAll());
        return "courses";
    }

}
