package ma.inwi.msproject.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.msproject.enums.GateType;

import java.util.Set;

@Entity
@Table(name = "gate")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class
Gate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private GateType gateType;

    @JsonManagedReference("trackingGate_gate")
    @OneToMany(mappedBy = "gate", cascade = CascadeType.ALL)
    private Set<TrackingGate> trackingGates;

    @Column(nullable = false)
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gate gate = (Gate) o;
        return id != null && id.equals(gate.id);
    }

    @Override
    public int hashCode() {
        return 31 + (id != null ? id.hashCode() : 0);
    }

}

