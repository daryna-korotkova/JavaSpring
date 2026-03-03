package project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clients")
@Data // Це створює методи setEmail, setFirstName тощо
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private LocalDate collaborationStartDate;

    @OneToMany(mappedBy = "client")
    private List<Order> orders;
}