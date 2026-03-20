package ma.inwi.msproject.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConfigRequiredAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long departementId;
    private Long gateId;

    @ElementCollection
    @CollectionTable(name = "configured_requiredActions", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "requiredActions")
    private Set<String> requiredActions;

    private boolean deleted = false;
}
