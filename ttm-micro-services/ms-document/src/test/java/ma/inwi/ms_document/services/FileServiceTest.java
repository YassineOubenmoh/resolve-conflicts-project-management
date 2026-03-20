package ma.inwi.ms_document.services;

import io.minio.*;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private FileService fileService;

    private String bucketName = "test-bucket";

    @BeforeEach
    void setUp() throws Exception {
        Field field = FileService.class.getDeclaredField("bucketName");
        field.setAccessible(true);
        field.set(fileService, "test-bucket");
    }

    @Test
    void generateFileNameWithTimestamp_shouldAppendTimestamp() {
        String original = "document.pdf";
        String result = fileService.generateFileNameWithTimestamp(original);
        assertTrue(result.startsWith("document_"));
        assertTrue(result.endsWith(".pdf"));
    }

    /*
    @Test
    void uploadFile_shouldUploadSuccessfully() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("report.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("dummy".getBytes()));
        when(file.getSize()).thenReturn(5L);
        when(file.getContentType()).thenReturn("application/pdf");

        when(minioClient.bucketExists(any())).thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(mock(ObjectWriteResponse.class));

        String result = String.valueOf(fileService.uploadFile(file));
        assertTrue(result.startsWith("report_") && result.endsWith(".pdf"));
    }

     */

    /*
    @Test
    void uploadFile_shouldRejectInvalidExtension() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFile(file);
        });

        assertEquals("File type not allowed. Allowed types: PPTX, DOCX, PDF, XLSX", exception.getMessage());
    }

     */

    @Test
    void downloadFile_shouldReturnInputStream() throws Exception {
        GetObjectResponse response = mock(GetObjectResponse.class);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        InputStream result = fileService.downloadFile("file.pdf");
        assertEquals(response, result);
    }


    @Test
    void getReadableFileSize_shouldReturnZeroForEmptyFile() throws Exception {
        StatObjectResponse stat = mock(StatObjectResponse.class);
        when(stat.size()).thenReturn(0L);
        when(minioClient.statObject(any())).thenReturn(stat);

        String size = fileService.getReadableFileSize("file.pdf");
        assertEquals("0 B", size);
    }

    @Test
    void getLastModifiedTime_shouldReturnFormattedDate() throws Exception {
        StatObjectResponse stat = mock(StatObjectResponse.class);
        Instant fakeInstant = Instant.parse("2024-04-01T12:30:00Z");
        // Specify a ZoneId (e.g., UTC)
        ZonedDateTime zonedDateTime = fakeInstant.atZone(ZoneId.of("UTC"));
        when(stat.lastModified()).thenReturn(zonedDateTime);
        when(minioClient.statObject(any())).thenReturn(stat);

        String result = fileService.getLastModifiedTime("file.pdf");
        assertTrue(result.startsWith("2024-04-01"));
    }

}
