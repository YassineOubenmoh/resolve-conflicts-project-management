package ma.inwi.ms_notif.mappers;

import ma.inwi.ms_notif.dto.NotificationDto;
import ma.inwi.ms_notif.dto.NotificationFrontDto;
import ma.inwi.ms_notif.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto notificationToNotificationDto(Notification notification);

    Notification notificationDtoToNotification(NotificationDto notificationDto);

}