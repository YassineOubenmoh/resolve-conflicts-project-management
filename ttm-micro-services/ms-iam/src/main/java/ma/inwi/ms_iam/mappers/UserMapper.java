package ma.inwi.ms_iam.mappers;

import ma.inwi.ms_iam.dto.UserDto;
import ma.inwi.ms_iam.dto.UserDtoRq;
import ma.inwi.ms_iam.dto.UserDtoRs;
import ma.inwi.ms_iam.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDtoRs userToUserDtoRs(User user);

    User userDtoRqToUser(UserDtoRq userDtoRq);


    User userDtoToUser(UserDto userDtoRq);

}
