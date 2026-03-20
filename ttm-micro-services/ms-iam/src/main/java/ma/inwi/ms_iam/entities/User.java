package ma.inwi.ms_iam.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    private String username;
    private String department;

    private String email;

    @ElementCollection
    @CollectionTable(name = "user_project", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "project_ids")
    private List<Long> projectsId;


    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    private List<String> roles;



}
