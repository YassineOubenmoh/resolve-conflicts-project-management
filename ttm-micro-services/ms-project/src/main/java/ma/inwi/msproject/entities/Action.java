package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.ValidationStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "action")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String actionLabel;

    @Column(unique = true)
    private String responseToActionLabel;

    @ElementCollection
    @CollectionTable(name = "action_comments", joinColumns = @JoinColumn(name = "action"))
    private Set<String> comments;

    private String actionCreatedBy;
    private String impactSenderEmail;

    private String actionDocument;
    private String responseDocument;

    private ValidationStatus validationStatus;
    private String justificationStatus;

    private String validatedBy;
    private String responseEmailSender;


    @JsonBackReference("action_requiredAction")
    @ManyToOne
    @JoinColumn(name = "requiredAction_id")
    private RequiredAction requiredAction;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastModifiedAt;

    @Column(nullable = false)
    private boolean deleted = false;

}
