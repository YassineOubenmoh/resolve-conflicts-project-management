package com.inwi.ttm_ai;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recipes")
public class RecipesController {

    private final RestTemplate restTemplate = new RestTemplate();

    /*
    @PostMapping("/suggest")
    public Map<String, Object> suggestRecipe(@RequestBody Map<String, Object> body) {
        body.put("stream", false); // ensure streaming is disabled
        String url = "http://127.0.0.1:11434/api/generate";

        RequestEntity<Map<String, Object>> request = RequestEntity
                .post(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        return response.getBody();
    }

     */

    @PostMapping(value = "/suggest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> suggestRecipe(
            @RequestParam("message") String message,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        StringBuilder combinedPrompt = new StringBuilder();

        // Add user message
        combinedPrompt.append("User message: ").append(message).append("\n\n");

        // Handle file if provided
        if (file != null && !file.isEmpty()) {
            try {
                String filename = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
                String fileContent;

                if (filename.endsWith(".pdf")) {
                    // Extract text from PDF
                    try (PDDocument document = PDDocument.load(file.getInputStream())) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        fileContent = stripper.getText(document);
                    }
                } else if (filename.endsWith(".docx")) {
                    // Extract text from DOCX
                    try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
                         XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                        fileContent = extractor.getText();
                    }
                } else {
                    // Fallback to plain text
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                        fileContent = reader.lines().collect(Collectors.joining("\n"));
                    }
                }

                combinedPrompt.append("Document content:\n").append(fileContent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to read uploaded file", e);
            }
        }

        // Build request to Ollama
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama3.2");
        requestBody.put("prompt", combinedPrompt.toString());
        requestBody.put("stream", false);

        String url = "http://127.0.0.1:11434/api/generate";

        RequestEntity<Map<String, Object>> request = RequestEntity
                .post(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody);

        ResponseEntity<Map> response = restTemplate.exchange(request, Map.class);
        return response.getBody();
    }
}
