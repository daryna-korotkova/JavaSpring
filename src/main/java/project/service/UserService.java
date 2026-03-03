package project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import project.model.Client;
import project.dto.CurrencyResponse;
import project.repository.ClientRepository;
import java.time.LocalDate;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // Об'єкт для запитів

    public Client loginOrRegister(String email, String firstName, String lastName) {
        return clientRepository.findByEmail(email)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setEmail(email);
                    newClient.setFirstName(firstName);
                    newClient.setLastName(lastName);
                    newClient.setCollaborationStartDate(LocalDate.now());
                    return clientRepository.save(newClient);
                });
    }

    public Double getUsdRate() {
        // URL API ПриватБанку
        String url = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";

        try {
            // Робимо запит і отримуємо масив об'єктів CurrencyResponse
            CurrencyResponse[] response = restTemplate.getForObject(url, CurrencyResponse[].class);

            if (response != null) {
                // Шукаємо в масиві саме USD
                return Arrays.stream(response)
                        .filter(c -> "USD".equals(c.getCcy()))
                        .findFirst()
                        .map(CurrencyResponse::getSale) // Беремо курс продажу
                        .orElse(41.50); // Якщо не знайшли USD, повертаємо запасний варіант
            }
        } catch (Exception e) {
            System.out.println("Помилка при запиті до API: " + e.getMessage());
        }
        return 41.50; // Запасний курс на випадок відсутності інтернету
    }
}