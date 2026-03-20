package ma.inwi.msproject.service;

import lombok.Builder;
import ma.inwi.msproject.dto.TrackingGateDto;
import ma.inwi.msproject.entities.Gate;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.entities.TrackingGate;
import ma.inwi.msproject.enums.GateType;
import ma.inwi.msproject.exceptions.GateNotFoundException;
import ma.inwi.msproject.exceptions.TrackingNotFoundException;
import ma.inwi.msproject.mappers.TrackingGateMapper;
import ma.inwi.msproject.repositories.GateRepository;
import ma.inwi.msproject.repositories.TrackingGateRepository;
import ma.inwi.msproject.repositories.TrackingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Builder
public class TrackingGateService {

    private final TrackingGateRepository trackingGateRepository;
    private final TrackingGateMapper trackingGateMapper;
    private final TrackingRepository trackingRepository;
    private final GateRepository gateRepository;

    public TrackingGateService(TrackingGateRepository trackingGateRepository,
                               TrackingGateMapper trackingGateMapper,
                               TrackingRepository trackingRepository,
                               GateRepository gateRepository) {
        this.trackingGateRepository = trackingGateRepository;
        this.trackingGateMapper = trackingGateMapper;
        this.trackingRepository = trackingRepository;
        this.gateRepository = gateRepository;
    }

    public Set<TrackingGateDto> getRelatedGatesToTracking(Long trackingId){
        Tracking tracking = trackingRepository.findById(trackingId).orElseThrow(
                () -> new TrackingNotFoundException("The tracking with id " + trackingId + " was not found !"));
        //putGatesInOrder(trackingId);
        Set<TrackingGate> trackingGates = trackingGateRepository.findByTracking(tracking);
        return trackingGates.stream()
                .map(trackingGateMapper::trackingGateToTrackingGateDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public TrackingGateDto affectGateToTracking(Long gateId, Long trackingId){

        Gate gate = gateRepository.findById(gateId).orElseThrow(
                () -> new GateNotFoundException("The gate with the id " + gateId + " was not found !"));
        Tracking tracking = trackingRepository.findById(trackingId).orElseThrow(
                () -> new TrackingNotFoundException("The tracking with id " + trackingId + " was not found !"));

        TrackingGate trackingGate = TrackingGate.builder()
                .gate(gate)
                .tracking(tracking)
                .build();

        trackingGateRepository.save(trackingGate);
        putGatesInOrder(trackingId);

        return trackingGateMapper.trackingGateToTrackingGateDto(trackingGate);
    }


    public void putGatesInOrder(Long trackingId) {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(() -> new TrackingNotFoundException("Tracking not found with id: " + trackingId));

        List<TrackingGate> trackingGates = trackingGateRepository.findByTracking(tracking)
                .stream()
                .sorted((tg1, tg2) -> compareGateTypes(tg1.getGate().getGateType(), tg2.getGate().getGateType()))  // Sort by GateType
                .toList();

        for (int i = 0; i < trackingGates.size(); i++) {
            TrackingGate currentGate = trackingGates.get(i);

            if (i > 0) {
                currentGate.setGateBefore(trackingGates.get(i - 1).getGate());
            }

            if (i < trackingGates.size() - 1) {
                currentGate.setGateAfter(trackingGates.get(i + 1).getGate());
            }

            trackingGateRepository.save(currentGate);
        }
    }

    private int compareGateTypes(GateType gateType1, GateType gateType2) {
        return gateType1.compareTo(gateType2);
    }





}
