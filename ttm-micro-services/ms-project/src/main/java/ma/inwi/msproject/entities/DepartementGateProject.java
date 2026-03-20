package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartementGateProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("departementGateProject_gateProject")
    @ManyToOne
    @JoinColumn(name = "gateProject_id")
    private GateProject gateProject;

    @JsonBackReference("departementGateProject_departement")
    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @JsonManagedReference("requiredAction-departementGateProject")
    @OneToMany(mappedBy = "departementGateProject", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RequiredAction> requiredActions;

    @Column(nullable = false)
    private boolean deleted = false;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DepartementGateProject that = (DepartementGateProject) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}

