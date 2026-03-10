package project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import project.model.Client;
import project.model.Course;
import project.model.Order;
import project.dto.CurrencyResponse;
import project.repository.ClientRepository;
import project.repository.CourseRepository;
import project.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Гарантує цілісність даних при роботі з БД
public class UserService {

    private final ClientRepository clientRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Знайти клієнта за email для відображення в Кабінеті або Курсах
     */
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email).orElse(null);
    }

    /**
     * Реєстрація нового користувача
     */
    public Client register(String email, String password, String firstName, String lastName) {
        if (clientRepository.findByEmail(email.trim()).isPresent()) {
            throw new RuntimeException("Користувач з такою поштою вже існує!");
        }

        Client newClient = new Client();
        newClient.setEmail(email.trim());
        newClient.setPassword(password.trim());
        newClient.setFirstName(firstName);
        newClient.setLastName(lastName);
        newClient.setCollaborationStartDate(LocalDate.now());

        return clientRepository.save(newClient);
    }

    /**
     * Авторизація користувача
     */
    public Client login(String email, String password) {
        return clientRepository.findByEmail(email.trim())
                .filter(c -> c.getPassword().equals(password.trim()))
                .orElseThrow(() -> new RuntimeException("Невірна пошта або пароль"));
    }

    /**
     * Request API: Отримання курсу валют від ПриватБанку
     */
    public Double getUsdRate() {
        String url = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
        try {
            CurrencyResponse[] response = restTemplate.getForObject(url, CurrencyResponse[].class);
            if (response != null) {
                parseCurrencyLog(response); // Виклик парсера для логів
                return Arrays.stream(response)
                        .filter(c -> "USD".equals(c.getCcy()))
                        .findFirst()
                        .map(CurrencyResponse::getSale)
                        .orElse(41.50);
            }
        } catch (Exception e) {
            System.err.println("Помилка API: " + e.getMessage());
        }
        return 41.50;
    }

    /**
     * Купівля курсу: перевірка на дублікати та збереження в ORDERS
     */
    @Transactional
    public void purchaseCourse(String email, Long courseId) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Клієнта не знайдено"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Курс не знайдено"));

        // Перевірка, чи курс вже є у списку замовлень клієнта
        boolean alreadyOwned = client.getOrders().stream()
                .anyMatch(o -> o.getCourse().getId().equals(courseId));

        if (alreadyOwned) {
            throw new RuntimeException("Ви вже придбали цей курс! Перейдіть до кабінету.");
        }

        Order order = new Order();
        order.setClient(client);
        order.setCourse(course);
        order.setCreatedAt(LocalDateTime.now()); // Фіксація точного часу покупки

        // Зберігаємо замовлення (ID генерується автоматично базою)
        orderRepository.save(order);
    }

    /**
     * Парсер: модуль для аналізу та збору унікальних сфер навчання з бази
     */
    public List<String> parseDisciplines() {
        List<Course> allCourses = courseRepository.findAll();
        System.out.println("DEBUG: Запуск локального парсера категорій...");

        List<String> disciplines = allCourses.stream()
                .map(Course::getDisciplineName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("Парсер виявив сфери: " + disciplines);
        return disciplines;
    }

    /**
     * Лог-парсер для даних з зовнішнього API
     */
    public void parseCurrencyLog(CurrencyResponse[] responses) {
        System.out.println("=== Парсинг даних зовнішнього API ===");
        for (CurrencyResponse res : responses) {
            System.out.println("Валюта: " + res.getCcy() + " | Продаж: " + res.getSale());
        }
    }
}