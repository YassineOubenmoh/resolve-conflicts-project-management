package ma.inwi.msproject.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ma.inwi.msproject.dto.DocumentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "ms-document", url = "http://localhost:8081")
public interface ProjectRestClient {

    @CircuitBreaker(name = "documentServiceCB", fallbackMethod = "fallbackUploadFile")
    @PostMapping(value = "/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DocumentDto> uploadFile(@RequestPart("file") MultipartFile file);

    default ResponseEntity<DocumentDto> fallbackUploadFile(MultipartFile file, Throwable t) {
        DocumentDto fallbackDto = new DocumentDto();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackDto);
    }

    @CircuitBreaker(name = "documentServiceCB", fallbackMethod = "fallbackUpdateFile")
    @PutMapping(value = "/files/update/{id}")
    ResponseEntity<DocumentDto> updateFile(@PathVariable("id") Long id, @RequestBody DocumentDto documentDto);

    default ResponseEntity<DocumentDto> fallbackUpdateFile(Long id, DocumentDto documentDto, Throwable t) {
        DocumentDto fallbackDto = new DocumentDto();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackDto);
    }

    @CircuitBreaker(name = "documentServiceCB", fallbackMethod = "fallbackDownloadFile")
    @GetMapping("/files/download/{fileName}")
    ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName);

    default ResponseEntity<byte[]> fallbackDownloadFile(String fileName, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new byte[0]);
    }

    @CircuitBreaker(name = "documentServiceCB", fallbackMethod = "fallbackGetFileSize")
    @GetMapping("/files/size/{fileName}")
    ResponseEntity<String> getFileSize(@PathVariable("fileName") String fileName);

    default ResponseEntity<String> fallbackGetFileSize(String fileName, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Unavailable");
    }

    @CircuitBreaker(name = "documentServiceCB", fallbackMethod = "fallbackGetFileLastModified")
    @GetMapping("/files/lastmodified/{fileName}")
    ResponseEntity<String> getFileLastModified(@PathVariable("fileName") String fileName);

    default ResponseEntity<String> fallbackGetFileLastModified(String fileName, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Unavailable");
    }
}

