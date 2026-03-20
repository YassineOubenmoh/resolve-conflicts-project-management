package ma.inwi.msproject.service;

import ma.inwi.msproject.dto.GateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.Project;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.exceptions.DepartementNotFoundException;
import ma.inwi.msproject.exceptions.GateAlreadyExistingException;
import ma.inwi.msproject.exceptions.GateNotFoundException;
import ma.inwi.msproject.exceptions.ProjectAlreadyExistingException;
import ma.inwi.msproject.mappers.GateMapper;
import ma.inwi.msproject.repositories.GateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GateService {

    private static final Logger logger = LoggerFactory.getLogger(GateService.class);  // Logger declaration

    private final GateRepository gateRepository;
    private final GateMapper gateMapper;

    @Autowired
    public GateService(GateRepository gateRepository, GateMapper gateMapper) {
        this.gateRepository = gateRepository;
        this.gateMapper = gateMapper;
    }

    public GateDto addGate(GateDto gateDto) {
        Optional<Gate> gateOptional = gateRepository.findByGateType(gateDto.getGateType());

        if (gateOptional.isPresent()){
            throw new GateAlreadyExistingException("The gate with ID " + gateOptional.get().getId() + " already exists !");
        }

        Gate gate = gateMapper.gateDtoToGate(gateDto);
        gate.setGateType(GateType.valueOf(String.valueOf(gateDto.getGateType())));
        gateRepository.save(gate);

        logger.info("Gate added successfully with ID: {}", gate.getId());
        return gateMapper.gateToGateDto(gate);
    }

    public GateDto getGateById(Long id) {
        logger.info("Fetching gate with ID: {}", id);  // Log when fetching gate
        Gate gate = gateRepository.findById(id).orElseThrow(
                () -> new GateNotFoundException("Gate with " + id + " was not found !"));

        if (gate.isDeleted()){
            return null;
        }

        logger.info("Gate with ID: {} found.", id);  // Log if found
        return gateMapper.gateToGateDto(gate);
    }

    public Set<GateDto> getAllGates() {
        List<Gate> gates = gateRepository.findAll();
        if (gates.isEmpty()){
            throw new GateNotFoundException("No gate was found !");
        }

        return gateRepository.findAll().stream()
                .filter(gate -> !gate.isDeleted())
                .map(gateMapper::gateToGateDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public GateDto updateGate(Long id, GateDto updatedGateDto) {
        logger.info("Updating gate with ID: {}", id);

        Gate existingGate = gateRepository.findById(id).orElseThrow(
                () -> new GateNotFoundException("Gate with ID: " + id + " was not found!")
        );

        if (existingGate.isDeleted()){
            return null;
        }

        logger.info("Existing Gate: {}", existingGate);
        existingGate.setGateType(GateType.valueOf(String.valueOf(updatedGateDto.getGateType())));

        logger.info("Updated Gate: {}", existingGate);
        gateRepository.save(existingGate);
        logger.info("Gate with ID: {} updated successfully.", id);

        return gateMapper.gateToGateDto(existingGate);
    }

    public void deleteGate(Long id) {
        logger.info("Deleting gate with ID: {}", id);  // Log before deletion
        Gate gate = gateRepository.findById(id).orElseThrow(
                () -> new GateNotFoundException("Gate with " + id + " was not found !"));
        gate.setDeleted(true);
        logger.info("Gate with ID: {} deleted successfully.", id);

        gateRepository.save(gate);
    }

}
