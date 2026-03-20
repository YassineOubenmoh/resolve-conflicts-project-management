package ma.inwi.ms_iam.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ma.inwi.ms_iam.dto.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-project", url = "http://localhost:8080/project")
public interface ProjectClient {


    @CircuitBreaker(name = "projectServiceCB", fallbackMethod = "fallbackGetProjectById")
    @GetMapping("/find/{id}")
    ResponseEntity<ProjectDto> getProjectById(@PathVariable("id") Long id);

    default ResponseEntity<ProjectDto> fallbackGetProjectById(Long id, Throwable t) {
        ProjectDto fallbackDto = new ProjectDto();
        fallbackDto.setId(id);
        fallbackDto.setTitle("Unavailable - fallback");
        fallbackDto.setOwnerUsername("N/A");
        fallbackDto.setDescription("Fallback due to: " + t.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(fallbackDto);
    }




}

