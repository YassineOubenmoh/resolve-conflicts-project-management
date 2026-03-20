package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.TrackingType;

import java.util.Objects;
import java.util.Set;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tracking")
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private TrackingType trackingType;

    @JsonManagedReference("tracking_trackingGate")
    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL)
    private Set<TrackingGate> trackingGates;

    @Column(nullable = false)
    private boolean deleted = false;
    /*
    @JsonBackReference("project_tracking")
    @OneToOne(mappedBy = "tracking")
    private Project project;

     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tracking tracking = (Tracking) o;
        return Objects.equals(id, tracking.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
