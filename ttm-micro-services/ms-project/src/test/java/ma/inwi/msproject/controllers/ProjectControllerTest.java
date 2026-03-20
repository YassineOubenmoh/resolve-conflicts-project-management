package ma.inwi.msproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.inwi.msproject.controller.ProjectController;
import ma.inwi.msproject.dto.ProjectDto;
import ma.inwi.msproject.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .title("New Project")
                .description("Test Project Description")
                .marketType("Market")
                .projectType("Type")
                .ttmComitteeSubCategory("SubCategory")
                .subcategoryCommercialCodir("Codir")
                .isConfidential(false)
                .trackingId(100L)
                .build();
    }

    @Test
    void testAddProject() throws Exception {
        Mockito.when(projectService.addProject(Mockito.any(ProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(post("/project/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(projectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Project"))
                .andExpect(jsonPath("$.description").value("Test Project Description"));
    }

    @Test
    void testGetAllProjects() throws Exception {
        Set<ProjectDto> projects = new HashSet<>(Collections.singletonList(projectDto));
        Mockito.when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/project/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetProjectById() throws Exception {
        Mockito.when(projectService.getProjectById(1L)).thenReturn(projectDto);

        mockMvc.perform(get("/project/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateProject() throws Exception {
        Mockito.when(projectService.updateProject(Mockito.eq(1L), Mockito.any(ProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(put("/project/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGoToNextGate() throws Exception {
        Mockito.doNothing().when(projectService).goToNextGateProject(1L, "yassine@example.com");

        mockMvc.perform(put("/project/next-gate/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully moved to the next gate for project ID: 1"));
    }

    @Test
    void testDeleteProject() throws Exception {
        Mockito.doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/project/delete/1"))
                .andExpect(status().isNoContent());
    }

 */
}

