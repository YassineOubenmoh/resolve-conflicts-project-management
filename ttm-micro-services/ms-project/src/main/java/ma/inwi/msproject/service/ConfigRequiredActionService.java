package ma.inwi.msproject.service;

import ma.inwi.msproject.dto.ConfigRequiredActionDto;
import ma.inwi.msproject.entities.Action;
import ma.inwi.msproject.entities.ConfigRequiredAction;
import ma.inwi.msproject.exceptions.ActionNotFoundException;
import ma.inwi.msproject.exceptions.ConfigRequiredActionNotFoundException;
import ma.inwi.msproject.mappers.ConfigRequiredActionMapper;
import ma.inwi.msproject.repositories.ConfigRequiredActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConfigRequiredActionService {

    private final ConfigRequiredActionRepository configRequiredActionRepository;
    private final ConfigRequiredActionMapper configRequiredActionMapper;

    @Autowired
    public ConfigRequiredActionService(ConfigRequiredActionRepository configRequiredActionRepository,
                                       ConfigRequiredActionMapper configRequiredActionMapper) {
        this.configRequiredActionRepository = configRequiredActionRepository;
        this.configRequiredActionMapper = configRequiredActionMapper;
    }

    public ConfigRequiredActionDto addConfigRequiredAction(ConfigRequiredActionDto configRequiredActionDto) {
        ConfigRequiredAction configRequiredAction = configRequiredActionRepository.findByDepartementIdAndGateId(configRequiredActionDto.getDepartementId(), configRequiredActionDto.getGateId());
        if (configRequiredAction != null){
            throw new ConfigRequiredActionNotFoundException("The configuration of required actions for department with ID " +
                    configRequiredActionDto.getDepartementId() + " and gate with ID " + configRequiredActionDto.getGateId() + " was already added !");
        }

        ConfigRequiredAction configRequiredAction1 = configRequiredActionMapper.configRequiredActionDtoToConfigRequiredAction(configRequiredActionDto);
        configRequiredActionRepository.save(configRequiredAction1);
        return configRequiredActionMapper.configRequiredActionToConfigRequiredActionDto(configRequiredAction1);
    }


    public ConfigRequiredActionDto getConfigRequiredActionById(Long id) {
        ConfigRequiredAction configRequiredAction = configRequiredActionRepository.findById(id).orElseThrow(
                () -> new ConfigRequiredActionNotFoundException("The required actions configuration with " + id + " was not found !"));

        if (configRequiredAction.isDeleted()){
            return null;
        }

        return configRequiredActionMapper.configRequiredActionToConfigRequiredActionDto(configRequiredAction);
    }

    public Set<ConfigRequiredActionDto> getAllConfigRequiredActions() {
        List<ConfigRequiredAction> configRequiredActions = configRequiredActionRepository.findAll();
        if (configRequiredActions.isEmpty()){
            throw new ConfigRequiredActionNotFoundException("No configuration of required actions is found !");
        }

        return configRequiredActions.stream()
                .filter(configRequiredAction -> !configRequiredAction.isDeleted())
                .map(configRequiredActionMapper::configRequiredActionToConfigRequiredActionDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ConfigRequiredActionDto updateConfigRequiredAction(Long id, ConfigRequiredActionDto configRequiredActionDto) {
        ConfigRequiredAction existingConfigRequiredAction = configRequiredActionRepository.findById(id).orElseThrow(
                () -> new ConfigRequiredActionNotFoundException("The configuration with ID " + id + " was not found !"));

        if (existingConfigRequiredAction.isDeleted()){
            return null;
        }

        existingConfigRequiredAction.setRequiredActions(configRequiredActionDto.getRequiredActions());
        existingConfigRequiredAction.setGateId(configRequiredActionDto.getGateId());
        existingConfigRequiredAction.setDepartementId(configRequiredActionDto.getDepartementId());

        configRequiredActionRepository.save(existingConfigRequiredAction);
        return configRequiredActionMapper.configRequiredActionToConfigRequiredActionDto(existingConfigRequiredAction);
    }


    public void deleteConfigRequiredAction(Long id) {
        ConfigRequiredAction configRequiredAction = configRequiredActionRepository.findById(id).orElseThrow(
                () -> new ConfigRequiredActionNotFoundException("The configuration with ID " + id + " was not found !"));
        configRequiredAction.setDeleted(true);
        configRequiredActionRepository.save(configRequiredAction);
    }
}
