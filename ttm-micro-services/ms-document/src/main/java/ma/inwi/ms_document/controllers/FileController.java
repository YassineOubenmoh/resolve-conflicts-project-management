package ma.inwi.ms_document.controllers;

import ma.inwi.ms_document.dtos.DocumentDto;
import ma.inwi.ms_document.entities.Document;
import ma.inwi.ms_document.enums.GateType;
import ma.inwi.ms_document.exceptions.DocumentNotFoundException;
import ma.inwi.ms_document.exceptions.UnauthorizedExtensionException;
import ma.inwi.ms_document.mappers.DocumentMapper;
import ma.inwi.ms_document.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    FileService fileService;
    private final DocumentMapper documentMapper;

    public FileController(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    /*
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String response = fileService.uploadFile(file);
            return ResponseEntity.ok(response);
        }
        catch (UnauthorizedExtensionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading the file: " + e.getMessage());
        }
    }

     */



    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            DocumentDto response = fileService.uploadFile(file);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (UnauthorizedExtensionException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<DocumentDto> updateDocument(@PathVariable("id") Long id, @RequestBody DocumentDto documentDto) {
        return new ResponseEntity<>(fileService.updateDocument(id, documentDto), HttpStatus.OK);
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName){
        try (InputStream fileStream = fileService.downloadFile(fileName)) {
            if (fileStream == null) {
                return ResponseEntity.status(404).body(null); // File not found
            }

            byte[] content = fileStream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(content);
        } catch (Exception e) {
            System.err.println("Error occurred while downloading the file: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }



    @GetMapping("/size/{fileName}")
    public ResponseEntity<String> getFileSize(@PathVariable("fileName") String fileName) {
        try {
            String readableSize = fileService.getReadableFileSize(fileName);
            return ResponseEntity.ok(readableSize);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving file size: " + e.getMessage());
        }
    }


    @GetMapping("/lastmodified/{fileName}")
    public ResponseEntity<String> getFileLastModified(@PathVariable("fileName") String fileName) {
        try {
            String lastModified = fileService.getLastModifiedTime(fileName);
            return ResponseEntity.ok(lastModified);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving last modified time: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<Set<DocumentDto>> getAllDocuments(){
        return new ResponseEntity<>(fileService.getAllDocuments(), HttpStatus.OK);
    }


    @GetMapping("/filter")
    public ResponseEntity<List<DocumentDto>> getFilteredDocuments(
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "authorName", required = false) String authorName,
            @RequestParam(value = "gateLabel", required = false) GateType gateLabel)
            {

        List<DocumentDto> documents = fileService.filterDocuments(department, authorName, gateLabel).stream()
                .map(documentMapper::documentToDocumentDto)
                .toList();

        return ResponseEntity.ok(documents);
    }



    @GetMapping("/actions-project/{projectId}")
    public ResponseEntity<Set<DocumentDto>> getDocumentsByProjectId(@PathVariable("projectId") Long projectId) {
        Set<DocumentDto> documents = fileService.getDocumentsByProjectId(projectId);
        return ResponseEntity.ok(documents);
    }


    @GetMapping("/actions-requiredaction/{requiredActionId}")
    public ResponseEntity<Set<DocumentDto>> getDocumentsByRequiredActionId(@PathVariable("requiredActionId") Long requiredActionId) {
        try {
            Set<DocumentDto> documents = fileService.getDocumentsByRequiredActionId(requiredActionId);
            return new ResponseEntity<>(documents, HttpStatus.OK);
        } catch (DocumentNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/documents-by-author/{authorName}")
    public ResponseEntity<List<DocumentDto>> getDocumentsByAuthorUsername(
            @PathVariable("authorName") String authorName) {
        return new ResponseEntity<>(fileService.getDocumentsByAuthorUsername(authorName), HttpStatus.OK);
    }





}
