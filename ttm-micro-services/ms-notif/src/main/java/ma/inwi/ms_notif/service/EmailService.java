package ma.inwi.ms_notif.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import ma.inwi.ms_notif.dto.NotificationDto;
import ma.inwi.ms_notif.entities.Notification;
import ma.inwi.ms_notif.exceptions.NotificationNotFoundException;
import ma.inwi.ms_notif.mappers.NotificationMapper;
import ma.inwi.ms_notif.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public EmailService(JavaMailSender mailSender, Configuration freemarkerConfig, NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.mailSender = mailSender;
        this.freemarkerConfig = freemarkerConfig;
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Value("${spring.mail.username}")
    private String senderEmail;



    public void sendEmailWithTemplate(String to, String subject, Map<String, Object> model, String templateName, String from) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Template template = freemarkerConfig.getTemplate(templateName);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        if (from == null || from.trim().isEmpty()) {
            helper.setFrom(senderEmail);
        } else {
            helper.setFrom(from);
        }

        mailSender.send(message);
    }


    public NotificationDto getNotificationById(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new NotificationNotFoundException("Notification with ID : " + notificationId + " was not found !"));
        return notificationMapper.notificationToNotificationDto(notification);
    }


    @Transactional
    public Set<NotificationDto> getAllUnConsumedNotificationsBySenderEmail(String senderEmail) {
        logger.info("Fetching unconsumed notifications for senderEmail: {}", senderEmail);

        List<Notification> notifications = notificationRepository.findUnconsumedMessagesBySenderMail(senderEmail);
        logger.debug("Found {} unconsumed notifications for senderEmail: {}", notifications.size(), senderEmail);

        if (notifications.isEmpty()) {
            logger.warn("No unconsumed notifications found for senderEmail: {}", senderEmail);
            throw new NotificationNotFoundException("All notifications are consumed!");
        }

        Set<NotificationDto> dtos = notifications.stream()
                .map(notificationMapper::notificationToNotificationDto)
                .collect(Collectors.toSet());

        logger.info("Successfully mapped {} notifications to DTOs for senderEmail: {}", dtos.size(), senderEmail);
        return dtos;
    }



    @Transactional
    public Set<NotificationDto> getAllUnConsumedNotificationsByReceiverEmail(String receiverEmail) {
        logger.info("Fetching unconsumed notifications for receiverEmail: {}", receiverEmail);

        List<Notification> notifications = notificationRepository.findUnconsumedMessagesByReceiverMail(receiverEmail);
        logger.debug("Found {} unconsumed notifications for receiverEmail: {}", notifications.size(), receiverEmail);

        if (notifications.isEmpty()) {
            logger.warn("No unconsumed notifications found for receiverEmail: {}", receiverEmail);
            throw new NotificationNotFoundException("All notifications are consumed!");
        }

        Set<NotificationDto> dtos = notifications.stream()
                .map(notificationMapper::notificationToNotificationDto)
                .filter(dto -> !dto.getSenderMail().equals(dto.getReceiverMail())) // filtering step
                .collect(Collectors.toSet());

        logger.info("Successfully mapped and filtered {} notifications to DTOs for receiverEmail: {}", dtos.size(), receiverEmail);
        return dtos;
    }




    @Transactional
    public void sendAndSaveNotification(String to, String subject, Map<String, Object> model, String templateName, String from) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);



            // it will set the real sender or the custom sender in case the real one is missing
            String actualSender = (from == null || from.trim().isEmpty()) ? senderEmail : from;

            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(actualSender);
            mailSender.send(message);

            Notification notification = Notification.builder()
                    .senderMail(actualSender)
                    .receiverMail(to)
                    .content(htmlContent)
                    .consumed(false)
                    .build();

            notificationRepository.save(notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public String getSenderEmail() {
        return senderEmail;
    }
}

