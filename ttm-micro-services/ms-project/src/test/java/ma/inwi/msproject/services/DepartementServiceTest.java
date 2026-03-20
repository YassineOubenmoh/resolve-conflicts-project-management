package ma.inwi.msproject.services;

import ma.inwi.msproject.dto.DepartementDto;
import ma.inwi.msproject.entities.Departement;
import ma.inwi.msproject.exceptions.DepartementNotFoundException;
import ma.inwi.msproject.mappers.DepartementMapper;
import ma.inwi.msproject.repositories.DepartementRepository;
import ma.inwi.msproject.service.DepartementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartementServiceTest {

    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private DepartementMapper departementMapper;

    @InjectMocks
    private DepartementService departementService;

    private Departement departement;
    private DepartementDto departementDto;

    @BeforeEach
    void setUp() {
        departement = Departement.builder()
                .id(1L)
                .departement("IT")
                .build();

        departementDto = DepartementDto.builder()
                .id(1L)
                .departement("IT")
                .build();
    }


    @Test
    void testAddDepartement() {
        // Mock repository behavior
        when(departementMapper.departementDtoToDepartement(departementDto)).thenReturn(departement);
        when(departementRepository.save(departement)).thenReturn(departement);
        when(departementMapper.departementToDepartementDto(departement)).thenReturn(departementDto);

        // Execute method
        DepartementDto result = departementService.addDepartement(departementDto);

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(departementDto.getId(), result.getId());
        assertEquals(departementDto.getDepartement(), result.getDepartement());
        verify(departementRepository, times(1)).save(departement);
    }




    @Test
    void testGetDepartementById_Success() {
        // Mock repository behavior
        when(departementRepository.findById(1L)).thenReturn(Optional.of(departement));
        when(departementMapper.departementToDepartementDto(departement)).thenReturn(departementDto);

        // Execute method
        DepartementDto result = departementService.getDepartementById(1L);

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(departementDto.getId(), result.getId());
        assertEquals(departementDto.getDepartement(), result.getDepartement());
        verify(departementRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDepartementById_NotFound() {
        // Mock repository behavior
        when(departementRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute and verify exception
        DepartementNotFoundException exception = assertThrows(DepartementNotFoundException.class, () -> {
            departementService.getDepartementById(1L);
        });
        assertEquals("Departement with 1 was not found !", exception.getMessage());
    }

    @Test
    void testGetAllDepartements_Success() {
        // Mock repository behavior
        Set<Departement> departements = new HashSet<>();
        departements.add(departement);

        // Convert the Set to a List
        when(departementRepository.findAll()).thenReturn(new ArrayList<>(departements));
        when(departementMapper.departementToDepartementDto(departement)).thenReturn(departementDto);

        // Execute method
        Set<DepartementDto> result = departementService.getAllDepartements();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(departementDto));
        verify(departementRepository, times(1)).findAll();
        verify(departementMapper, times(1)).departementToDepartementDto(departement);
    }

    @Test
    void testUpdateDepartement_Success() {
        // Mock repository behavior
        when(departementRepository.findById(1L)).thenReturn(Optional.of(departement));
        when(departementRepository.save(departement)).thenReturn(departement);
        when(departementMapper.departementToDepartementDto(departement)).thenReturn(departementDto);

        DepartementDto updatedDepartementDto = DepartementDto.builder()
                .id(1L)
                .departement("Human Resources")
                .build();

        // Execute method
        DepartementDto result = departementService.updateDepartement(1L, updatedDepartementDto);

        // Verify interactions and assertions
        assertNotNull(result);
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testUpdateDepartement_NotFound() {
        // Mock repository behavior
        when(departementRepository.findById(1L)).thenReturn(Optional.empty());

        DepartementDto updatedDepartementDto = DepartementDto.builder()
                .id(1L)
                .departement("Human Resources")
                .build();

        // Execute and verify exception
        DepartementNotFoundException exception = assertThrows(DepartementNotFoundException.class, () -> {
            departementService.updateDepartement(1L, updatedDepartementDto);
        });
        assertEquals("Departement with 1 was not found !", exception.getMessage());
    }

    @Test
    void deleteDepartement_ShouldDeleteDepartement_WhenIdIsValid() {
        // Given
        Long id = 1L;
        Departement departement = new Departement();
        departement.setId(id);

        // Mocking repository methods
        when(departementRepository.findById(id)).thenReturn(Optional.of(departement));
        when(departementRepository.save(any(Departement.class))).thenReturn(departement);

        // Act
        departementService.deleteDepartement(id);

        // Assert
        assertTrue(departement.isDeleted());  // Ensure the departement is marked as deleted

        // Verifications
        verify(departementRepository, times(1)).findById(id);  // Verify the findById is called
        verify(departementRepository, times(1)).save(departement);   // Verify that save is called to persist the changes
    }

    @Test
    void deleteDepartement_ShouldThrowDepartementNotFoundException_WhenDepartementNotFound() {
        // Given
        Long id = 1L;

        // Mocking repository methods to return empty
        when(departementRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        DepartementNotFoundException exception = assertThrows(DepartementNotFoundException.class, () -> {
            departementService.deleteDepartement(id);
        });

        assertEquals("Departement with 1 was not found !", exception.getMessage());

        // Verifications
        verify(departementRepository, times(1)).findById(id);   // Verify findById is called once
        verify(departementRepository, times(0)).save(any(Departement.class));  // Verify save is not called
    }



}