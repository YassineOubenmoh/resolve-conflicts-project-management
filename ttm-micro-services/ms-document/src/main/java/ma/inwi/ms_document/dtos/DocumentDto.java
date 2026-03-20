package ma.inwi.ms_document.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.inwi.ms_document.enums.GateType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {
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
}
