package project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import project.model.Client;
import project.model.Course;
import project.model.Order;
import project.model.CurrencyResponsePrivatBank;
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
@Transactional
public class UserService {

    private final ClientRepository clientRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email).orElse(null);
    }


    public Client register(String email, String password, String firstName, String lastName) {
        if (clientRepository.findByEmail(email.trim()).isPresent()) {
            throw new RuntimeException("A user with such an email address already exists!");
        }

        Client newClient = new Client();
        newClient.setEmail(email.trim());
        newClient.setPassword(password.trim());
        newClient.setFirstName(firstName);
        newClient.setLastName(lastName);
        newClient.setCollaborationStartDate(LocalDate.now());

        return clientRepository.save(newClient);
    }


    public Client login(String email, String password) {
        return clientRepository.findByEmail(email.trim())
                .filter(c -> c.getPassword().equals(password.trim()))
                .orElseThrow(() -> new RuntimeException("Incorrect email address or password"));
    }


    public Double getUsdRate() {
        String url = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
        try {
            CurrencyResponsePrivatBank[] response = restTemplate.getForObject(url, CurrencyResponsePrivatBank[].class);
            if (response != null) {
                parseCurrencyLog(response);
                return Arrays.stream(response)
                        .filter(c -> "USD".equals(c.getCcy()))
                        .findFirst()
                        .map(CurrencyResponsePrivatBank::getSale)
                        .orElse(44.50);
            }
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
        }
        return 44.50;
    }


    @Transactional
    public void purchaseCourse(String email, Long courseId) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean alreadyOwned = client.getOrders().stream()
                .anyMatch(o -> o.getCourse().getId().equals(courseId));
        if (alreadyOwned) {
            throw new RuntimeException("Ви вже придбали цей курс!");
        }

        Double rate = getUsdRate();
        Double priceInUah = course.getPriceUsd() * rate;

        if (client.getBalance() < priceInUah) {
            double missingAmount = priceInUah - client.getBalance();

            throw new RuntimeException(
                    "На балансі недостатньо коштів.<br>" +
                            "Ваш баланс: " + String.format("%.2f", client.getBalance()) + " грн.<br>" +
                            "Не вистачає: " + String.format("%.2f", missingAmount) + " грн."
            );
        }

        client.setBalance(client.getBalance() - priceInUah);

        Order order = new Order();
        order.setClient(client);
        order.setCourse(course);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
        clientRepository.save(client);
    }


    public List<String> parseDisciplines() {
        List<Course> allCourses = courseRepository.findAll();
        System.out.println("DEBUG: Starting the local category parser...");

        List<String> disciplines = allCourses.stream()
                .map(Course::getDisciplineName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("The parser detected the following spheres: " + disciplines);
        return disciplines;
    }


    public void parseCurrencyLog(CurrencyResponsePrivatBank[] responses) {
        System.out.println("=== Parsing data from an external API ===");
        for (CurrencyResponsePrivatBank res : responses) {
            System.out.println("Currency: " + res.getCcy() + " | For sale: " + res.getSale());
        }
    }
}