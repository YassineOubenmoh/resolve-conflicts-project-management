package ma.inwi.ms_document.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.ms_document.dtos.DocumentDto;
import ma.inwi.ms_document.exceptions.UnauthorizedExtensionException;
import ma.inwi.ms_document.services.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.when;

//@WebMvcTest(FileController.class)
class FileControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadFile_shouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "dummy content".getBytes());

        DocumentDto documentDto = new DocumentDto();
        documentDto.setId(1L);
        documentDto.setDocumentLabel("report_123.pdf");

        when(fileService.uploadFile(file)).thenReturn(documentDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/files/upload")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(documentDto)));
    }

    @Test
    void uploadFile_shouldReturnBadRequestForUnauthorizedExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "dummy content".getBytes());

        when(fileService.uploadFile(file)).thenThrow(new UnauthorizedExtensionException("File type not allowed."));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/files/upload")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void downloadFile_shouldReturnFileContent() throws Exception {
        byte[] fileContent = "dummy content".getBytes();
        when(fileService.downloadFile("report.pdf")).thenReturn(new ByteArrayInputStream(fileContent));

        mockMvc.perform(MockMvcRequestBuilders.get("/files/download/{fileName}", "report.pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().bytes(fileContent))
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"report.pdf\""));
    }

    @Test
    void downloadFile_shouldReturnNotFoundForMissingFile() throws Exception {
        when(fileService.downloadFile("missingfile.pdf")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/files/download/{fileName}", "missingfile.pdf"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getFileSize_shouldReturnSize() throws Exception {
        when(fileService.getReadableFileSize("report.pdf")).thenReturn("1.0 MB");

        mockMvc.perform(MockMvcRequestBuilders.get("/files/size/{fileName}", "report.pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1.0 MB"));
    }

    @Test
    void getFileSize_shouldReturnErrorForFileNotFound() throws Exception {
        when(fileService.getReadableFileSize("missingfile.pdf")).thenThrow(new Exception("Error retrieving file size"));

        mockMvc.perform(MockMvcRequestBuilders.get("/files/size/{fileName}", "missingfile.pdf"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error retrieving file size: Error retrieving file size"));
    }

    @Test
    void getFileLastModified_shouldReturnLastModifiedDate() throws Exception {
        when(fileService.getLastModifiedTime("report.pdf")).thenReturn("2024-04-01T12:30:00Z");

        mockMvc.perform(MockMvcRequestBuilders.get("/files/lastmodified/{fileName}", "report.pdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("2024-04-01T12:30:00Z"));
    }

    @Test
    void getFileLastModified_shouldReturnErrorForFileNotFound() throws Exception {
        when(fileService.getLastModifiedTime("missingfile.pdf")).thenThrow(new Exception("Error retrieving last modified time"));

        mockMvc.perform(MockMvcRequestBuilders.get("/files/lastmodified/{fileName}", "missingfile.pdf"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error retrieving last modified time: Error retrieving last modified time"));
    }

 */
}
