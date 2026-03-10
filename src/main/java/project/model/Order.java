package project.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor // Додано для коректної роботи Hibernate
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Стратегія для автоматичного лічильника в H2
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Завантажуємо клієнта разом із замовленням
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER) // Завантажуємо курс разом із замовленням
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}