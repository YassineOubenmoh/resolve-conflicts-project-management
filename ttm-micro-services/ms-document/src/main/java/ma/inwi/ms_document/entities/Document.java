package ma.inwi.ms_document.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.ms_document.enums.GateType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actionLabel;

    private Long projectId;
    private Long requiredActionId;

    private String documentLabel;
    private String typeDocument;
    private String department;
    private String authorName;
    private GateType gateLabel;
    private String dateUpload;
    private String size;
    private boolean deleted = false;

}
