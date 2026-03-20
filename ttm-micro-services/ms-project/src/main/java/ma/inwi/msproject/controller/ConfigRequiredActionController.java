package ma.inwi.msproject.controller;

import ma.inwi.msproject.dto.ConfigRequiredActionDto;
import ma.inwi.msproject.service.ConfigRequiredActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/config-required-actions")
public class ConfigRequiredActionController {

    private final ConfigRequiredActionService configRequiredActionService;

    @Autowired
    public ConfigRequiredActionController(ConfigRequiredActionService configRequiredActionService) {
        this.configRequiredActionService = configRequiredActionService;
    }

    @PostMapping
    public ResponseEntity<ConfigRequiredActionDto> addConfigRequiredAction(@RequestBody ConfigRequiredActionDto dto) {
        ConfigRequiredActionDto createdDto = configRequiredActionService.addConfigRequiredAction(dto);
        return ResponseEntity.ok(createdDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfigRequiredActionDto> getConfigRequiredActionById(@PathVariable("id") Long id) {
        ConfigRequiredActionDto dto = configRequiredActionService.getConfigRequiredActionById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Set<ConfigRequiredActionDto>> getAllConfigRequiredActions() {
        Set<ConfigRequiredActionDto> dtos = configRequiredActionService.getAllConfigRequiredActions();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigRequiredActionDto> updateConfigRequiredAction(
            @PathVariable("id") Long id,
            @RequestBody ConfigRequiredActionDto dto) {
        ConfigRequiredActionDto updatedDto = configRequiredActionService.updateConfigRequiredAction(id, dto);
        if (updatedDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfigRequiredAction(@PathVariable("id") Long id) {
        configRequiredActionService.deleteConfigRequiredAction(id);
        return ResponseEntity.noContent().build();
    }
}