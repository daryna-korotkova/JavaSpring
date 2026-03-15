package project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private LocalDate collaborationStartDate;

    @Column(nullable = false)
    private Double balance = 5000.0;

    @OneToMany(mappedBy = "client")
    private List<Order> orders;
}