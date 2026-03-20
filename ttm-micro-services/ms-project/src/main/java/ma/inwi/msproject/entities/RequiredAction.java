package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "required_action")
public class RequiredAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requiredAction;

    @Column(nullable = false)
    private boolean deleted = false;

    @JsonBackReference("requiredAction-departementGateProject")
    @ManyToOne
    @JoinColumn(name = "departementGateProject_id")
    private DepartementGateProject departementGateProject;

    @JsonManagedReference("action_requiredAction")
    @OneToMany(mappedBy = "requiredAction", cascade = CascadeType.ALL)
    private Set<Action> actions;


    @Override
    public int hashCode() {
        return Objects.hash(id); // only use ID or simple fields
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequiredAction that = (RequiredAction) o;
        return Objects.equals(id, that.id);
    }

}
