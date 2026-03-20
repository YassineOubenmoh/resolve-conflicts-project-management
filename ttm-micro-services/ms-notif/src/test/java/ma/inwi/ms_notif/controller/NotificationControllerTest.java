package ma.inwi.ms_notif.controller;


import ma.inwi.ms_notif.controllers.NotificationController;
import ma.inwi.ms_notif.dto.NotificationDto;
import ma.inwi.ms_notif.entities.Notification;
import ma.inwi.ms_notif.exceptions.NotificationNotFoundException;
import ma.inwi.ms_notif.mappers.NotificationMapper;
import ma.inwi.ms_notif.repositories.NotificationRepository;
import ma.inwi.ms_notif.service.EmailService;
import ma.inwi.ms_notif.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private EmailService emailService;


    // ---- Test: getAllNotifications ----
    @Test
    void testGetAllNotifications_Success() {
        Set<NotificationDto> mockNotifications = Set.of(new NotificationDto());
        when(notificationService.getAllNotifications()).thenReturn(mockNotifications);

        ResponseEntity<Set<NotificationDto>> response = notificationController.getAllNotifications();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockNotifications, response.getBody());
    }

    @Test
    void testGetAllNotifications_EmptySet() {
        when(notificationService.getAllNotifications()).thenReturn(Collections.emptySet());

        ResponseEntity<Set<NotificationDto>> response = notificationController.getAllNotifications();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }

    // ---- Test: markAsConsumed ----
    @Test
    void testMarkAsConsumed_Success() {
        Long notificationId = 1L;
        Notification mockNotification = new Notification();
        mockNotification.setId(notificationId);
        mockNotification.setConsumed(false);

        NotificationDto mockDto = new NotificationDto();
        mockDto.setId(notificationId);
        mockDto.setConsumed(true);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(mockNotification));
        when(notificationMapper.notificationToNotificationDto(mockNotification)).thenReturn(mockDto);
        when(notificationMapper.notificationDtoToNotification(mockDto)).thenReturn(mockNotification);

        ResponseEntity<NotificationDto> response = notificationController.markAsConsumed(notificationId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isConsumed());
        verify(notificationRepository).save(mockNotification);
    }

    @Test
    void testMarkAsConsumed_NotificationNotFound() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationController.markAsConsumed(1L));
    }



    // ---- Test: getAllUnConsumedNotificationsBySenderEmail ----
    @Test
    void testGetAllUnConsumedNotificationsBySenderEmail_Success() {
        String senderEmail = "sender@example.com";
        Set<NotificationDto> mockNotifications = Set.of(new NotificationDto());

        when(emailService.getAllUnConsumedNotificationsBySenderEmail(senderEmail)).thenReturn(mockNotifications);

        ResponseEntity<Set<NotificationDto>> response = notificationController.getAllUnConsumedNotificationsBySenderEmail(senderEmail);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockNotifications, response.getBody());
    }

    @Test
    void testGetAllUnConsumedNotificationsBySenderEmail_NotFound() {
        String senderEmail = "sender@example.com";
        when(emailService.getAllUnConsumedNotificationsBySenderEmail(senderEmail)).thenReturn(Collections.emptySet());

        ResponseEntity<Set<NotificationDto>> response = notificationController.getAllUnConsumedNotificationsBySenderEmail(senderEmail);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    // ---- Test: getAllUnConsumedNotificationsByReceiverEmail ----
    @Test
    void testGetAllUnConsumedNotificationsByReceiverEmail_Success() {
        String receiverEmail = "receiver@example.com";
        Set<NotificationDto> mockNotifications = Set.of(new NotificationDto());

        when(emailService.getAllUnConsumedNotificationsByReceiverEmail(receiverEmail)).thenReturn(mockNotifications);

        ResponseEntity<Set<NotificationDto>> response = notificationController.getAllUnConsumedNotificationsByReceiverEmail(receiverEmail);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockNotifications, response.getBody());
    }

    @Test
    void testGetAllUnConsumedNotificationsByReceiverEmail_NotFound() {
        String receiverEmail = "receiver@example.com";
        when(emailService.getAllUnConsumedNotificationsByReceiverEmail(receiverEmail)).thenReturn(Collections.emptySet());

        ResponseEntity<Set<NotificationDto>> response = notificationController.getAllUnConsumedNotificationsByReceiverEmail(receiverEmail);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
