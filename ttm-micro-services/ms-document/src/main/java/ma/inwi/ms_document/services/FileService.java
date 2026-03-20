package ma.inwi.ms_document.services;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import ma.inwi.ms_document.dtos.DocumentDto;
import ma.inwi.ms_document.entities.Document;
import ma.inwi.ms_document.enums.GateType;
import ma.inwi.ms_document.exceptions.DocumentNotFoundException;
import ma.inwi.ms_document.exceptions.UnauthorizedExtensionException;
import ma.inwi.ms_document.mappers.DocumentMapper;
import ma.inwi.ms_document.repositories.DocumentRepository;
import ma.inwi.ms_document.repositories.spec.DocumentSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);


    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Autowired
    public FileService(MinioClient minioClient,
                       DocumentRepository documentRepository,
                       DocumentMapper documentMapper) {
        this.minioClient = minioClient;
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    public String generateFileNameWithTimestamp(String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = originalFileName.replace(fileExtension, "_" + timestamp + fileExtension);
        return newFileName;
    }

    public DocumentDto uploadFile(MultipartFile file) throws Exception {
        String originalFileName = file.getOriginalFilename();

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        //String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toUpperCase();


        List<String> allowedExtensions = Arrays.asList(".pptx", ".docx", ".pdf", ".xlsx");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new UnauthorizedExtensionException("File type not allowed. Allowed types: PPTX, DOCX, PDF, XLSX");
        }

        String uniqueFileName = generateFileNameWithTimestamp(originalFileName);
        InputStream fileStream = file.getInputStream();

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(uniqueFileName)
                .stream(fileStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build();

        ObjectWriteResponse response = minioClient.putObject(putObjectArgs);

        DocumentDto documentDto = DocumentDto.builder()
                .documentLabel(uniqueFileName)
                .typeDocument(fileExtension)
                .dateUpload(getLastModifiedTime(uniqueFileName))
                .size(getReadableFileSize(uniqueFileName))
                .build();

        Document document = documentMapper.documentDtoToDocument(documentDto);
        documentRepository.save(document);

        return documentMapper.documentToDocumentDto(document);

        //return response.etag();
        //return uniqueFileName;
    }

    public InputStream downloadFile(String fileName) throws Exception {
        try {
            GetObjectResponse in = minioClient
                    .getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            return in;
        } catch (Exception e) {
            logger.error("Error occurred while downloading the file: " + e.getMessage());
            throw e;
        }
    }


    public String getReadableFileSize(String fileName) throws Exception {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            long size = stat.size();
            if (size <= 0) return "0 B";

            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int unitIndex = (int) (Math.log10(size) / Math.log10(1024));
            double readableSize = size / Math.pow(1024, unitIndex);

            return String.format("%.1f %s", readableSize, units[unitIndex]);

        } catch (Exception e) {
            logger.error("Error occurred while retrieving file size: " + e.getMessage());
            throw e;
        }
    }


    public String getLastModifiedTime(String fileName) throws Exception {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withLocale(Locale.ENGLISH)
                    .withZone(ZoneId.systemDefault());

            return formatter.format(stat.lastModified());

        } catch (Exception e) {
            logger.error("Error occurred while retrieving last modified time: " + e.getMessage());
            throw e;
        }
    }


    public DocumentDto updateDocument(Long id, DocumentDto documentDto){
        Document existingDocument = documentRepository.findById(id).orElseThrow(
                () -> new DocumentNotFoundException("Document with id " + id + " was not found !"));

        existingDocument.setAuthorName(documentDto.getAuthorName());
        existingDocument.setActionLabel(documentDto.getActionLabel());
        existingDocument.setProjectId(documentDto.getProjectId());
        existingDocument.setRequiredActionId(documentDto.getRequiredActionId());
        existingDocument.setGateLabel(documentDto.getGateLabel());
        existingDocument.setDepartment(documentDto.getDepartment());


        existingDocument.setDocumentLabel(documentDto.getDocumentLabel());
        existingDocument.setDateUpload(documentDto.getDateUpload());
        existingDocument.setSize(documentDto.getSize());
        existingDocument.setTypeDocument(documentDto.getTypeDocument());




        documentRepository.save(existingDocument);
        return documentMapper.documentToDocumentDto(existingDocument);
    }


    public Set<DocumentDto> getAllDocuments(){
        List<Document> documents = documentRepository.findAll();
        if (documents.isEmpty()){
            throw new DocumentNotFoundException("No document was not found !");
        }
        return documents.stream()
                .filter(document -> !document.isDeleted())
                .map(documentMapper::documentToDocumentDto)
                .collect(Collectors.toSet());
    }

    public Set<DocumentDto> getDocumentsByProjectId(Long projectId) {
        Set<DocumentDto> documentDtos = getAllDocuments();
        Set<DocumentDto> filteredDocuments = documentDtos.stream()
                .filter(documentDto ->
                        documentDto.getProjectId() != null && documentDto.getProjectId().equals(projectId))
                .collect(Collectors.toSet());

        if (filteredDocuments.isEmpty()) {
            throw new DocumentNotFoundException("No document was found for project ID: " + projectId);
        }

        return filteredDocuments;
    }



    public Set<DocumentDto> getDocumentsByRequiredActionId(Long requiredActionId) {
        Set<DocumentDto> documentDtos = getAllDocuments();
        Set<DocumentDto> filteredDocuments = documentDtos.stream()
                .filter(documentDto ->
                        documentDto.getProjectId() != null && documentDto.getRequiredActionId().equals(requiredActionId))
                .collect(Collectors.toSet());

        if (filteredDocuments.isEmpty()) {
            throw new DocumentNotFoundException("No document was found for requiredActionId ID: " + requiredActionId);
        }

        return filteredDocuments;
    }






    /*
    public List<Document> filterDocuments(String department, String authorName, GateType gateLabel) {
        return documentRepository.findAll(
                Specification.where(DocumentSpec.hasDepartement(department))
                        .and(DocumentSpec.hasAuthorName(authorName))
                        .and(DocumentSpec.hasGateLabel(gateLabel))
        );
    }
     */

    public List<Document> filterDocuments(String department, String authorName, GateType gateLabel) {
        Specification<Document> spec = Specification.where(null);

        if (department != null && !department.isEmpty()) {
            spec = spec.and(DocumentSpec.hasDepartement(department));
        }

        if (authorName != null && !authorName.isEmpty()) {
            spec = spec.and(DocumentSpec.hasAuthorName(authorName));
        }

        if (gateLabel != null) {
            spec = spec.and(DocumentSpec.hasGateLabel(gateLabel));
        }

        return documentRepository.findAll(spec);
    }


    /*
    public List<DocumentDto> getDocumentsByAuthorUsername(String username) {
        log.info("Fetching documents for author: {}", username);

        List<Document> documents = documentRepository.findDocumentByAuthorName(username);

        if (documents.isEmpty()) {
            log.warn("No documents found for author: {}", username);
            throw new DocumentNotFoundException("No document is found!");
        }

        List<DocumentDto> documentDtos = documents.stream()
                .map(documentMapper::documentToDocumentDto)
                .collect(Collectors.toList());

        log.info("Found {} document(s) for author: {}", documentDtos.size(), username);
        return documentDtos;
    }


     */


    private List<DocumentDto> filterLatestByActionLabel(List<DocumentDto> documents) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, DocumentDto> latestByActionLabel = new HashMap<>();

        for (DocumentDto doc : documents) {
            String actionLabel = doc.getActionLabel();
            LocalDateTime currentDocDate = LocalDateTime.parse(doc.getDateUpload(), formatter);

            DocumentDto existing = latestByActionLabel.get(actionLabel);
            if (existing == null) {
                latestByActionLabel.put(actionLabel, doc);
            } else {
                LocalDateTime existingDocDate = LocalDateTime.parse(existing.getDateUpload(), formatter);
                if (currentDocDate.isAfter(existingDocDate)) {
                    latestByActionLabel.put(actionLabel, doc);
                }
            }
        }

        return new ArrayList<>(latestByActionLabel.values());
    }


    public List<DocumentDto> getDocumentsByAuthorUsername(String username) {
        log.info("Fetching documents for author: {}", username);

        List<Document> documents = documentRepository.findDocumentByAuthorName(username);

        if (documents.isEmpty()) {
            log.warn("No documents found for author: {}", username);
            throw new DocumentNotFoundException("No document is found!");
        }

        List<DocumentDto> documentDtos = documents.stream()
                .map(documentMapper::documentToDocumentDto)
                .collect(Collectors.toList());

        List<DocumentDto> filteredDocuments = filterLatestByActionLabel(documentDtos);

        log.info("Found {} latest document(s) for author: {}", filteredDocuments.size(), username);
        return filteredDocuments;
    }









}
