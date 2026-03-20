package ma.inwi.msproject.service;

import jakarta.transaction.Transactional;
import lombok.Builder;
import ma.inwi.msproject.dto.TrackingDto;
import ma.inwi.msproject.entities.Action;
import ma.inwi.msproject.entities.Tracking;
import ma.inwi.msproject.exceptions.ActionAlreadyExistingException;
import ma.inwi.msproject.exceptions.TrackingAlreadyExistingException;
import ma.inwi.msproject.exceptions.TrackingNotFoundException;
import ma.inwi.msproject.mappers.GateMapper;
import ma.inwi.msproject.mappers.TrackingMapper;
import ma.inwi.msproject.repositories.GateRepository;
import ma.inwi.msproject.repositories.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Builder
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final TrackingMapper trackingMapper;
    private final GateRepository gateRepository;
    private final GateMapper gateMapper;

    @Autowired
    public TrackingService(TrackingRepository trackingRepository, TrackingMapper trackingMapper, GateRepository gateRepository, GateMapper gateMapper) {
        this.trackingRepository = trackingRepository;
        this.trackingMapper = trackingMapper;
        this.gateRepository = gateRepository;
        this.gateMapper = gateMapper;
    }

    public TrackingDto addTracking(TrackingDto trackingDto){
        /*
        Optional<Tracking> trackingOptional = trackingRepository.findByTrackingLabel(trackingDto.getTrackingType());

        if (trackingOptional.isPresent()){
            throw new TrackingAlreadyExistingException("The tracking with ID " + trackingOptional.get().getId() + " already exists !");
        }

         */

        Tracking tracking = trackingMapper.trackingDtoToTracking(trackingDto);
        trackingRepository.save(tracking);
        return trackingMapper.trackingToTrackingDto(tracking);
    }

    public TrackingDto getTrackingById(Long id){
        Tracking tracking = trackingRepository.findById(id).orElseThrow(
                () -> new TrackingNotFoundException("Tracking with " + id + " was not found ! "));

        if (tracking.isDeleted()){
            return null;
        }

        return trackingMapper.trackingToTrackingDto(tracking);
    }

    public Set<TrackingDto> getAllTrackings(){
        List<Tracking> trackings = trackingRepository.findAll();
        if (trackings.isEmpty()){
            throw new TrackingNotFoundException("No tracking was found !");
        }

        return trackings.stream()
                .filter(tracking -> !tracking.isDeleted())
                .map(trackingMapper::trackingToTrackingDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public TrackingDto updateTracking(Long id, TrackingDto updatedTracking) {
        Tracking existingTracking = trackingRepository.findById(id).orElseThrow(
                () -> new TrackingNotFoundException("Tracking with " + id + " was not found ! ")
        );
        if (existingTracking.isDeleted()){
            return null;
        }

        existingTracking.setTrackingType(updatedTracking.getTrackingType());


        trackingRepository.save(existingTracking);
        return trackingMapper.trackingToTrackingDto(existingTracking);
    }

    public void deleteTracking(Long id) {
        Tracking tracking = trackingRepository.findById(id).orElseThrow(
                () -> new TrackingNotFoundException("Tracking with " + id + " was not found !"));
        tracking.setDeleted(true);
        trackingRepository.save(tracking);
    }

}
