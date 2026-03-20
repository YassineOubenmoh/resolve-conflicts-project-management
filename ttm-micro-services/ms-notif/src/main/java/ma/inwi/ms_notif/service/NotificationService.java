package ma.inwi.ms_notif.service;

import ma.inwi.ms_notif.dto.*;
import ma.inwi.ms_notif.entities.Notification;
import ma.inwi.ms_notif.mappers.NotificationMapper;
import ma.inwi.ms_notif.repositories.NotificationRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ma.inwi.ms_notif.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static ma.inwi.ms_notif.constants.Constants.*;

@Service
public class NotificationService {

    private final EmailService emailService;
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);


    public NotificationService(EmailService emailService, NotificationMapper notificationMapper, NotificationRepository notificationRepository) {
        this.emailService = emailService;
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
    }

    public Set<NotificationDto> getAllNotifications(){
        return notificationRepository.findAll().stream()
                .map(notificationMapper::notificationToNotificationDto)
                .filter(notification -> !notification.getSenderMail().equals(notification.getReceiverMail()))
                .collect(Collectors.toSet());
    }




    public void markNotificationAsConsumed(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setConsumed(true);
            notificationRepository.save(notification);
        });
    }



    /*
    @RabbitListener(queues = RabbitMQConfig.QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessage(UserDetails[] details) {
        List<UserDetails> dto = Arrays.asList(details);

        dto.forEach(user -> {
            Map<String, Object> model = new HashMap<>();
            model.put("firstName", user.getFirstName());
            model.put("lastName", user.getLastName());
            model.put("projectName", user.getProjectName().toUpperCase());
            model.put("department", user.getDepartment().toUpperCase());

            try {
                logger.info("Attempting to send email to {}", user.getEmail());

                sendAndSaveNotification(user.getEmail(), "New Project Created", model, "email-template.ftl");

                logger.info("Email successfully sent to {}", user.getEmail());


            } catch (Exception e) {
                // Handle logging or retries
                logger.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage(), e);
            }
        });
    }

     */

    @RabbitListener(queues = RabbitMQConfig.QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessage(NewProjectCreationMailDto[] messages) {
        List<NewProjectCreationMailDto> mailDtos = Arrays.asList(messages);

        mailDtos.forEach(dto -> {
            UserDetails user = dto.getUserDetails();
            Map<String, Object> model = new HashMap<>();

            model.put(FIRSTNAME, user.getFirstName());
            model.put(LASTNAME, user.getLastName());
            model.put(USERNAME, user.getUsername());
            model.put(PROJECT_NAME, user.getProjectName().toUpperCase());
            model.put(DEPARTMENT, user.getDepartment().toUpperCase());
            model.put(OWNER_NAME, dto.getOwnerName());
            model.put(DESCRIPTION, dto.getDescription());
            model.put(MARKET_TYPE, dto.getMarketType());
            model.put(PROJECT_TYPE, dto.getProjectType());
            model.put(TTM_COM_SUB_CAT, dto.getTtmComitteeSubCategory());
            model.put(SUB_CAT_COM_COD, dto.getSubcategoryCommercialCodir());
            model.put(CONFIDENTIAL, dto.getConfidential());
            model.put(DATE_START_TTM, dto.getDateStartTtm());

            try {
                logger.info(ATTEMPTING_TO_SEND_EMAIL, user.getEmail());
                logger.info("The owner full name is : {}", dto.getOwnerName());
                emailService.sendAndSaveNotification(user.getEmail(), "🎉 New Project Created", model, "email-template.ftl", null);

                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, user.getEmail());

            } catch (Exception e) {
                logger.error(FAILED_SEND_EMAIL_TO, user.getEmail(), e.getMessage(), e);
            }
        });
    }



    @RabbitListener(queues = RabbitMQConfig.AFFECTATION_GATES_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageGateAffectationToDepartment(DepartmentsGatesAffectationEmailDto[] details) {
        List<DepartmentsGatesAffectationEmailDto> dto = Arrays.asList(details);

        dto.forEach(user -> {
            // Prepare model for email template
            Map<String, Object> model = new HashMap<>();
            model.put(FIRSTNAME, user.getUserDetails().getFirstName());
            model.put(LASTNAME, user.getUserDetails().getLastName());
            model.put(PROJECT_NAME, user.getUserDetails().getProjectName().toUpperCase());
            model.put(DEPARTMENT, user.getUserDetails().getDepartment().toUpperCase());

            // We now get the departmentGateRequiredActions directly from the DTO
            model.put(GATE, user.getDepartmentGateRequiredActions().getGate());
            model.put(REQUIRED_ACTION, user.getDepartmentGateRequiredActions().getRequiredActions());

            try {
                // Log the attempt to send the email
                logger.info(ATTEMPTING_TO_SEND_EMAIL, user.getUserDetails().getEmail());
                // Send email using the template with the model

                emailService.sendAndSaveNotification(user.getUserDetails().getEmail(), "Gates Affectation", model, "departments-gates-affectation.ftl", user.getEmailSender());

                /*
                emailService.sendEmailWithTemplate(
                        user.getUserDetails().getEmail(),
                        "Gates Affectation",
                        model,
                        "departments-gates-affectation.ftl",
                        null
                );

                 */
                // Log successful email sending
                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, user.getUserDetails().getEmail());
            } catch (Exception e) {
                // Handle logging or retries in case of failure
                logger.error(FAILED_SEND_EMAIL_TO, user.getUserDetails().getEmail(), e.getMessage(), e);
            }
        });
    }


    @RabbitListener(queues = RabbitMQConfig.NEXT_GATE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageNextGateTransition(NextGateTransitionMailDto[] details) {
        List<NextGateTransitionMailDto> dtoList = Arrays.asList(details);

        dtoList.forEach(dto -> {
            Map<String, Object> model = new HashMap<>();
            model.put(FIRSTNAME, dto.getUserDetails().getFirstName());
            model.put(LASTNAME, dto.getUserDetails().getLastName());
            model.put(PROJECT_NAME, dto.getUserDetails().getProjectName().toUpperCase());
            model.put(DEPARTMENT, dto.getUserDetails().getDepartment().toUpperCase());
            model.put(FUTURE_GATE, dto.getFutureGate());
            model.put(PASSED_GATE, dto.getPassedGate());
            model.put(INFORMATION, dto.getInformation());
            model.put(ACTIONS, dto.getActions());
            model.put(DECISIONS, dto.getDecisions());
            model.put(PASSING_DATE, dto.getPassingDate());

            try {
                logger.info(ATTEMPTING_TO_SEND_EMAIL, dto.getUserDetails().getEmail());
                if (dto.getFutureGate() == null){
                    model.put("daysTtm", dto.getDaysTtm());
                    emailService.sendAndSaveNotification(dto.getUserDetails().getEmail(), "✅ Project Completed!", model, "end-project-template.ftl", dto.getEmailSender());
                    /*
                    emailService.sendEmailWithTemplate(
                            dto.getUserDetails().getEmail(),
                            "✅ Project Completed!",
                            model,
                            "end-project-template.ftl",
                            null
                    );

                     */
                }
                else{
                    emailService.sendAndSaveNotification(dto.getUserDetails().getEmail(), "Gate Transition Notification", model, "next-gate-template.ftl", dto.getEmailSender());
                    /*
                    emailService.sendEmailWithTemplate(
                            dto.getUserDetails().getEmail(),
                            "Gate Transition Notification",
                            model,
                            "next-gate-template.ftl",
                            null
                    );

                     */
                }

                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, dto.getUserDetails().getEmail());
            } catch (Exception e) {
                logger.error(FAILED_SEND_EMAIL_TO, dto.getUserDetails().getEmail(), e.getMessage(), e);
            }
        });
    }



    @RabbitListener(queues = RabbitMQConfig.IMPACT_ADDED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageNewImpactAdded(NewImpactAddedMailDto[] details) {
        List<NewImpactAddedMailDto> dtoList = Arrays.asList(details);

        dtoList.forEach(dto -> {
            Map<String, Object> model = new HashMap<>();
            model.put(FIRSTNAME, dto.getUserDetails().getFirstName());
            model.put(LASTNAME, dto.getUserDetails().getLastName());
            model.put(PROJECT_NAME, dto.getUserDetails().getProjectName().toUpperCase());
            model.put(DEPARTMENT, dto.getUserDetails().getDepartment().toUpperCase());
            model.put(ACTION_LABEL, dto.getActionLabel());
            model.put(COMMENTS, dto.getComments());
            model.put(ACTION_CREATED_BY, dto.getActionCreatedBy());
            model.put(IMPACT_SENDER_MAIL, dto.getImpactSenderEmail());
            model.put(ACTION_DOCUMENT, dto.getActionDocument());
            model.put(REQUIRED_ACTION, dto.getRequiredAction());

            try {
                logger.info(ATTEMPTING_TO_SEND_EMAIL, dto.getUserDetails().getEmail());


                emailService.sendAndSaveNotification(dto.getUserDetails().getEmail(), "Add New Impact", model, "new-impact-added-template.ftl", dto.getImpactSenderEmail());
                /*
                emailService.sendEmailWithTemplate(
                        dto.getUserDetails().getEmail(),
                        "Add New Impact",
                        model,
                        "new-impact-added-template.ftl",
                        dto.getImpactSenderEmail()
                );

                 */

                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, dto.getUserDetails().getEmail());
            } catch (Exception e) {
                logger.error(FAILED_SEND_EMAIL_TO, dto.getUserDetails().getEmail(), e.getMessage(), e);
            }
        });
    }



    @RabbitListener(queues = RabbitMQConfig.RESPONSE_ADDED_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageNewResponseToImpact(NewResponseToImpactMailDto response) {
            Map<String, Object> model = new HashMap<>();
            model.put("projectName", response.getUserDetails().getProjectName());
            model.put("responseToActionLabel", response.getResponseToActionLabel());

            switch (response.getValidationStatus()){
                case ACCEPTER -> model.put("validationStatus", "✅ Votre impact a été accepté !");
                case REFUSER -> model.put("validationStatus", "❌ Votre impact a été refusé !");
                case A_MODIFIER -> model.put("validationStatus", "Votre impact doit subir quelques modifications !");
            }

            model.put("justificationStatus", response.getJustificationStatus());
            model.put("validatedBy", response.getValidatedBy());
            model.put("responseDocument", response.getResponseDocument());

            try {
                logger.info(ATTEMPTING_TO_SEND_EMAIL, response.getUserDetails().getEmail());

                emailService.sendAndSaveNotification(response.getUserDetails().getEmail(), "Response To Impact", model, "new-response-impact-template.ftl", response.getResponseEmailSender());

                /*
                emailService.sendEmailWithTemplate(
                        response.getUserDetails().getEmail(),
                        "Response To Impact",
                        model,
                        "new-response-impact-template.ftl",
                        response.getResponseEmailSender()
                );

                 */

                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, response.getUserDetails().getEmail());
            } catch (Exception e) {
                logger.error(FAILED_SEND_EMAIL_TO, response.getUserDetails().getEmail(), e.getMessage(), e);
            }

    }



    @RabbitListener(queues = RabbitMQConfig.AFFECTATION_IMPACT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageProjectAffectedToInterlocutorSignalingImpact(ProjectAffectedToInterlocutorImpactDto response) {
        Map<String, Object> model = new HashMap<>();
        model.put(PROJECT_NAME, response.getUserDetails().getProjectName());
        model.put(FIRSTNAME, response.getUserDetails().getFirstName());
        model.put(LASTNAME, response.getUserDetails().getLastName());
        model.put(DEPARTMENT, response.getUserDetails().getDepartment());

        try {
            logger.info(ATTEMPTING_TO_SEND_EMAIL, response.getUserDetails().getEmail());

            emailService.sendAndSaveNotification(response.getUserDetails().getEmail(), "You are Interlocutor!", model, "project-affected-interlocutor-impact.ftl", response.getSenderMail());

                /*
                emailService.sendEmailWithTemplate(
                        response.getUserDetails().getEmail(),
                        "Response To Impact",
                        model,
                        "new-response-impact-template.ftl",
                        response.getResponseEmailSender()
                );

                 */

            logger.info(EMAIL_SUCCESSFULLY_SEND_TO, response.getUserDetails().getEmail());
        } catch (Exception e) {
            logger.error(FAILED_SEND_EMAIL_TO, response.getUserDetails().getEmail(), e.getMessage(), e);
        }

    }




    @RabbitListener(queues = RabbitMQConfig.PROJECT_AFFECTED_INTERLOCUTOR_RESPONSE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageProjectAffectedToInterlocutorRespondingImpact(ProjectAffectedToInterlocutorImpactDto response) {
        Map<String, Object> model = new HashMap<>();
        model.put(PROJECT_NAME, response.getUserDetails().getProjectName());
        model.put(FIRSTNAME, response.getUserDetails().getFirstName());
        model.put(LASTNAME, response.getUserDetails().getLastName());
        model.put(DEPARTMENT, response.getUserDetails().getDepartment());

        try {
            logger.info(ATTEMPTING_TO_SEND_EMAIL, response.getUserDetails().getEmail());

            emailService.sendAndSaveNotification(response.getUserDetails().getEmail(), "You are Interlocutor!", model, "project-affected-interlocutor-respond.ftl", response.getSenderMail());

                /*
                emailService.sendEmailWithTemplate(
                        response.getUserDetails().getEmail(),
                        "Response To Impact",
                        model,
                        "new-response-impact-template.ftl",
                        response.getResponseEmailSender()
                );

                 */

            logger.info(EMAIL_SUCCESSFULLY_SEND_TO, response.getUserDetails().getEmail());
        } catch (Exception e) {
            logger.error(FAILED_SEND_EMAIL_TO, response.getUserDetails().getEmail(), e.getMessage(), e);
        }

    }



    @RabbitListener(queues = RabbitMQConfig.IMPACT_MODIFICATION_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageImpactModified(NewImpactAddedMailDto[] details) {
        List<NewImpactAddedMailDto> dtoList = Arrays.asList(details);

        dtoList.forEach(dto -> {
            Map<String, Object> model = new HashMap<>();
            model.put(FIRSTNAME, dto.getUserDetails().getFirstName());
            model.put(LASTNAME, dto.getUserDetails().getLastName());
            model.put(PROJECT_NAME, dto.getUserDetails().getProjectName().toUpperCase());
            model.put(DEPARTMENT, dto.getUserDetails().getDepartment().toUpperCase());
            model.put(ACTION_LABEL, dto.getActionLabel());
            model.put(COMMENTS, dto.getComments());
            model.put(ACTION_CREATED_BY, dto.getActionCreatedBy());
            model.put(IMPACT_SENDER_MAIL, dto.getImpactSenderEmail());
            model.put(ACTION_DOCUMENT, dto.getActionDocument());
            model.put(REQUIRED_ACTION, dto.getRequiredAction());

            try {
                logger.info(ATTEMPTING_TO_SEND_EMAIL, dto.getUserDetails().getEmail());


                emailService.sendAndSaveNotification(dto.getUserDetails().getEmail(), "Impact Updated !", model, "impact-modified-template.ftl", dto.getImpactSenderEmail());
                /*
                emailService.sendEmailWithTemplate(
                        dto.getUserDetails().getEmail(),
                        "Add New Impact",
                        model,
                        "new-impact-added-template.ftl",
                        dto.getImpactSenderEmail()
                );

                 */

                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, dto.getUserDetails().getEmail());
            } catch (Exception e) {
                logger.error(FAILED_SEND_EMAIL_TO, dto.getUserDetails().getEmail(), e.getMessage(), e);
            }
        });
    }




    @RabbitListener(queues = RabbitMQConfig.GATE_SUSPENSION_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void consumeMessageGateStatus(SuspensionGateMailDto[] messages) {
        List<SuspensionGateMailDto> mailDtos = Arrays.asList(messages);

        mailDtos.forEach(dto -> {
            UserDetails user = dto.getUserDetails();
            Map<String, Object> model = new HashMap<>();

            model.put(FIRSTNAME, user.getFirstName());
            model.put(LASTNAME, user.getLastName());
            model.put(USERNAME, user.getUsername());
            model.put(PROJECT_NAME, user.getProjectName().toUpperCase());
            model.put(GATE, dto.getGate());

            try {
                logger.info(ATTEMPTING_TO_SEND_EMAIL, user.getEmail());
                if (dto.isInProgress() == false){
                    emailService.sendAndSaveNotification(user.getEmail(), "⛔ Gate Progress Suspension", model, "gate-suspended-template.ftl", dto.getEmailOwner());
                }
                else{
                    emailService.sendAndSaveNotification(user.getEmail(), "Gate Progress In Motion", model, "gate-activated-template.ftl", dto.getEmailOwner());
                }

                logger.info(EMAIL_SUCCESSFULLY_SEND_TO, user.getEmail());

            } catch (Exception e) {
                logger.error(FAILED_SEND_EMAIL_TO, user.getEmail(), e.getMessage(), e);
            }
        });
    }

}


