package ma.inwi.ms_notif.controllers;

import lombok.RequiredArgsConstructor;
import ma.inwi.ms_notif.dto.NotificationDto;
import ma.inwi.ms_notif.entities.Notification;
import ma.inwi.ms_notif.exceptions.NotificationNotFoundException;
import ma.inwi.ms_notif.mappers.NotificationMapper;
import ma.inwi.ms_notif.repositories.NotificationRepository;
import ma.inwi.ms_notif.service.EmailService;
import ma.inwi.ms_notif.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "*")
@RequestMapping("api/internal")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);


    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;


    @GetMapping("/notifs")
    public ResponseEntity<Set<NotificationDto>> getAllNotifications() {
        return new ResponseEntity<>(notificationService.getAllNotifications(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable("id") Long id) {
        logger.info("Received request to get notification by ID: {}", id);

        try {
            NotificationDto notification = emailService.getNotificationById(id);
            logger.info("Notification fetched successfully: {}", notification);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching notification with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/consume/{id}")
    public ResponseEntity<NotificationDto> markAsConsumed(@PathVariable("id") Long id) {
        Notification notificationOptional = notificationRepository.findById(id).orElseThrow(
                () -> new NotificationNotFoundException("Notification with ID : " + id + " was not found !"));

        NotificationDto notificationDto = notificationMapper.notificationToNotificationDto(notificationOptional);
        notificationDto.setConsumed(true);

        logger.info("The notification with id {} is consumed", id);

        notificationRepository.save(notificationMapper.notificationDtoToNotification(notificationDto));
        return new ResponseEntity<>(notificationDto, HttpStatus.OK);
    }

    @GetMapping("/unconsumed-by-sender/{senderEmail}")
    public ResponseEntity<Set<NotificationDto>> getAllUnConsumedNotificationsBySenderEmail(@PathVariable("senderEmail") String senderEmail) {
        Set<NotificationDto> notificationDtos = emailService.getAllUnConsumedNotificationsBySenderEmail(senderEmail);
        if (notificationDtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // or some error message
        }
        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
    }


    @GetMapping("/unconsumed-by-receiver/{receiverEmail}")
    public ResponseEntity<Set<NotificationDto>> getAllUnConsumedNotificationsByReceiverEmail(@PathVariable("receiverEmail") String receiverEmail){
        Set<NotificationDto> notificationDtos = emailService.getAllUnConsumedNotificationsByReceiverEmail(receiverEmail);
        if (notificationDtos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(notificationDtos, HttpStatus.OK);
    }


}
