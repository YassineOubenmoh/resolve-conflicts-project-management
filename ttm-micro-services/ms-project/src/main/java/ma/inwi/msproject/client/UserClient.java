package ma.inwi.msproject.client;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ma.inwi.msproject.dto.UserDto;
import ma.inwi.msproject.dto.UserDtoRs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@FeignClient(name = "ms-iam", url = "http://localhost:8087/api")
public interface UserClient {

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetUsersByDepartments")
    @PostMapping("/internal/users/by-departments")
    List<UserDto> getUsersByDepartments(@RequestBody List<String> departments);

    default List<UserDto> fallbackGetUsersByDepartments(List<String> departments, Throwable t) {
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetUsersOfAllDepartments")
    @PostMapping("/internal/users-all-deps")
    List<UserDto> getUsersOfAllDepartments(@RequestBody List<String> departments);

    default List<UserDto> fallbackGetUsersOfAllDepartments(List<String> departments, Throwable t) {
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackAffectProjectToInterlocutor")
    @PostMapping("internal/affectProject")
    String affectProjectToInterlocutor(@RequestParam String username, @RequestParam Long projectId);

    default String fallbackAffectProjectToInterlocutor(String username, Long projectId, Throwable t) {
        return "Fallback: Unable to assign project at this time.";
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetUserByUsername")
    @GetMapping("internal/users/by-username/{username}")
    UserDto getUserByUsername(@PathVariable("username") String username);

    default UserDto fallbackGetUserByUsername(String username, Throwable t) {
        return new UserDto();
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetInterlocutorsByDepartment")
    @GetMapping("internal/interlocutors-dep")
    Set<UserDtoRs> getInterlocutorsByDepartment(@RequestParam("department") String department);

    default Set<UserDtoRs> fallbackGetInterlocutorsByDepartment(String department, Throwable t) {
        return Collections.emptySet();
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetUsersByDepartment")
    @GetMapping("internal/users-by-department/{department}")
    ResponseEntity<List<UserDto>> getUsersByDepartment(@PathVariable("department") String department);

    default ResponseEntity<List<UserDto>> fallbackGetUsersByDepartment(String department, Throwable t) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetProjectsReservedForSpoc")
    @GetMapping("internal/reserved-for-spoc/{department}")
    ResponseEntity<List<Long>> getProjectsReservedForSpoc(@PathVariable("department") String department);

    default ResponseEntity<List<Long>> fallbackGetProjectsReservedForSpoc(String department, Throwable t) {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "fallbackGetProjectsOfInterlocutor")
    @GetMapping("internal/interlocutor-projects/{username}")
    ResponseEntity<List<Long>> getProjectsOfInterlocutor(@PathVariable("username") String username);

    default ResponseEntity<List<Long>> fallbackGetProjectsOfInterlocutor(String username, Throwable t) {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
