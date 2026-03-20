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

@Entity
@Table(name = "tracking_gate")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingGate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("trackingGate_gate")
    @ManyToOne
    @JoinColumn(name = "gate_id")
    private Gate gate;

    @JsonBackReference("tracking_trackingGate")
    @ManyToOne
    @JoinColumn(name = "tracking_id")
    private Tracking tracking;

    @JsonBackReference("gate-before")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "gate_before_id", referencedColumnName = "id", nullable = true)
    private Gate gateBefore;

    @JsonBackReference("gate-after")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "gate_after_id", referencedColumnName = "id", nullable = true)
    private Gate gateAfter;

    @JsonManagedReference("trackingGate_gateProject")
    @OneToMany(mappedBy = "trackingGate", cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE})
    private Set<GateProject> gateProjects;

    @Column(nullable = false)
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingGate that = (TrackingGate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
