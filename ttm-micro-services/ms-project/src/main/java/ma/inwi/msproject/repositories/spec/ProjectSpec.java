package ma.inwi.msproject.repositories.spec;

import ma.inwi.msproject.entities.Project;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpec {

    public static Specification<Project> hasProjectType(String projectType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("projectType"), "%" + projectType + "%");
    }

    public static Specification<Project> hasMarketType(String marketType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("marketType"), "%" + marketType + "%");
    }


}
