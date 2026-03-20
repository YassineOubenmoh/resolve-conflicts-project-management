package ma.inwi.ms_document.repositories.spec;

import ma.inwi.ms_document.entities.Document;
import ma.inwi.ms_document.enums.GateType;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpec {

    public static Specification<Document> hasDepartement(String department) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("department"), "%" + department + "%");
    }

    public static Specification<Document> hasAuthorName(String authorName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("authorName"), "%" + authorName + "%");
    }

    public static Specification<Document> hasGateLabel(GateType gateLabel) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("gateLabel"), gateLabel);
    }

}
