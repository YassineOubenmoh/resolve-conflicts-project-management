package ma.inwi.msproject.service;

import ma.inwi.msproject.dto.DepartementDto;
import ma.inwi.msproject.entities.Action;
import ma.inwi.msproject.entities.Departement;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.exceptions.ActionNotFoundException;
import ma.inwi.msproject.exceptions.DepartementAlreadyExistingException;
import ma.inwi.msproject.exceptions.DepartementNotFoundException;
import ma.inwi.msproject.exceptions.ProjectAlreadyExistingException;
import ma.inwi.msproject.mappers.DepartementMapper;
import ma.inwi.msproject.repositories.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final DepartementMapper departementMapper;

    @Autowired
    public DepartementService(DepartementRepository departementRepository,
                              DepartementMapper departementMapper) {
        this.departementRepository = departementRepository;
        this.departementMapper = departementMapper;
    }

    public DepartementDto addDepartement(DepartementDto departementDto) {
        Optional<Departement> departementOptional = departementRepository.findDepartementByLabel(departementDto.getDepartement());

        if (departementOptional.isPresent()){
            throw new DepartementAlreadyExistingException("The department with ID " + departementOptional.get().getDepartement() + " already exists !");
        }

        Departement departement = departementMapper.departementDtoToDepartement(departementDto);
        departementRepository.save(departement);
        return departementMapper.departementToDepartementDto(departement);
    }


    public DepartementDto getDepartementById(Long id) {
        Departement departement = departementRepository.findById(id).orElseThrow(
                () -> new DepartementNotFoundException("Departement with " + id + " was not found !"));

        if (departement.isDeleted()){
            return null;
        }

        return departementMapper.departementToDepartementDto(departement);
    }

    public Set<DepartementDto> getAllDepartements() {
        List<Departement> departements = departementRepository.findAll();
        if (departements.isEmpty()){
            throw new DepartementNotFoundException("No department was found !");
        }

        return departements.stream()
                .filter(departement -> !departement.isDeleted())
                .map(departementMapper::departementToDepartementDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public DepartementDto updateDepartement(Long id, DepartementDto updatedDepartementDto) {
        Departement existingDepartement = departementRepository.findById(id).orElseThrow(
                () -> new DepartementNotFoundException("Departement with " + id + " was not found !"));

        if (existingDepartement.isDeleted()){
            return null;
        }

        existingDepartement.setDepartement(updatedDepartementDto.getDepartement());

        departementRepository.save(existingDepartement);
        return departementMapper.departementToDepartementDto(existingDepartement);
    }


    public void deleteDepartement(Long id) {
        Departement departement = departementRepository.findById(id).orElseThrow(
                () -> new DepartementNotFoundException("Departement with " + id + " was not found !"));
        departement.setDeleted(true);
        departementRepository.save(departement);
    }
}
