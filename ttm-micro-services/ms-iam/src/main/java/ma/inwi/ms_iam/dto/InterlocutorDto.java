package ma.inwi.ms_iam.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterlocutorDto {
    private Long interlocutorSignalingId;
    private Long interlocutorRespondingId;
    private String interlocutorSignalingFirstName;
    private String interlocutorRespondingFirstName;
    private String interlocutorSignalingLastName;
    private String interlocutorRespondingLastName;
    private String projectName;
    private Long projectId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterlocutorDto)) return false;
        InterlocutorDto that = (InterlocutorDto) o;
        return Objects.equals(interlocutorSignalingId, that.interlocutorSignalingId) &&
                Objects.equals(interlocutorRespondingId, that.interlocutorRespondingId) &&
                Objects.equals(projectName, that.projectName); // uniqueness condition
    }

    @Override
    public int hashCode() {
        return Objects.hash(interlocutorSignalingId, interlocutorRespondingId, projectName);
    }
}
