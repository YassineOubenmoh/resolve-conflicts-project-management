package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.DecisionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "gate_project")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GateProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("trackingGate_gateProject")
    @ManyToOne
    @JoinColumn(name = "trackingGate_id")
    private TrackingGate trackingGate;

    private boolean currentGate = false;

    @Column(nullable = true)
    private LocalDateTime passingDate;

    private boolean inProgress = true;

    //@Column(nullable = false)
    private boolean startingGate = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DecisionType decisionType;

    @ElementCollection
    @CollectionTable(name = "gate_information", joinColumns = @JoinColumn(name = "gate_id"))
    @Column(name = "info")
    private Set<String> information;

    @ElementCollection
    @CollectionTable(name = "gate_actions", joinColumns = @JoinColumn(name = "gate_id"))
    @Column(name = "action")
    private Set<String> actions;

    @ElementCollection
    @CollectionTable(name = "gate_decisions", joinColumns = @JoinColumn(name = "gate_id"))
    @Column(name = "decision")
    private Set<String> decisions;

    @JsonBackReference("project_gateProject")
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonManagedReference("departementGateProject_gateProject")
    @OneToMany(mappedBy = "gateProject", cascade = CascadeType.ALL)
    private Set<DepartementGateProject> departementGateProjects;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean projectCompleted = false;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GateProject that = (GateProject) o;
        return Objects.equals(trackingGate, that.trackingGate) &&
                Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackingGate, project);
    }
}
