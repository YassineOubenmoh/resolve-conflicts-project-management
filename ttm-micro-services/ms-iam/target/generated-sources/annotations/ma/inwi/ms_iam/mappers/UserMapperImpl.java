package ma.inwi.ms_iam.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import ma.inwi.ms_iam.dto.UserDto;
import ma.inwi.ms_iam.dto.UserDtoRq;
import ma.inwi.ms_iam.dto.UserDtoRs;
import ma.inwi.ms_iam.dto.UserDtoRs.UserDtoRsBuilder;
import ma.inwi.ms_iam.entities.User;
import ma.inwi.ms_iam.entities.User.UserBuilder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-12T16:47:44+0100",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDtoRs userToUserDtoRs(User user) {
        if ( user == null ) {
            return null;
        }

        UserDtoRsBuilder userDtoRs = UserDtoRs.builder();

        userDtoRs.firstName( user.getFirstName() );
        userDtoRs.lastName( user.getLastName() );
        userDtoRs.username( user.getUsername() );
        userDtoRs.department( user.getDepartment() );
        userDtoRs.email( user.getEmail() );
        List<Long> list = user.getProjectsId();
        if ( list != null ) {
            userDtoRs.projectsId( new ArrayList<Long>( list ) );
        }
        List<String> list1 = user.getRoles();
        if ( list1 != null ) {
            userDtoRs.roles( new ArrayList<String>( list1 ) );
        }

        return userDtoRs.build();
    }

    @Override
    public User userDtoRqToUser(UserDtoRq userDtoRq) {
        if ( userDtoRq == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.firstName( userDtoRq.getFirstName() );
        user.lastName( userDtoRq.getLastName() );
        user.username( userDtoRq.getUsername() );
        user.department( userDtoRq.getDepartment() );
        user.email( userDtoRq.getEmail() );
        List<Long> list = userDtoRq.getProjectsId();
        if ( list != null ) {
            user.projectsId( new ArrayList<Long>( list ) );
        }
        List<String> list1 = userDtoRq.getRoles();
        if ( list1 != null ) {
            user.roles( new ArrayList<String>( list1 ) );
        }

        return user.build();
    }

    @Override
    public User userDtoToUser(UserDto userDtoRq) {
        if ( userDtoRq == null ) {
            return null;
        }

        UserBuilder user = User.builder();

        user.firstName( userDtoRq.getFirstName() );
        user.lastName( userDtoRq.getLastName() );
        user.username( userDtoRq.getUsername() );
        user.department( userDtoRq.getDepartment() );
        user.email( userDtoRq.getEmail() );
        List<String> list = userDtoRq.getRoles();
        if ( list != null ) {
            user.roles( new ArrayList<String>( list ) );
        }

        return user.build();
    }
}
