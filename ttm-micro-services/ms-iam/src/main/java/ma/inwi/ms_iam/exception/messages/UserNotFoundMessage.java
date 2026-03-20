package ma.inwi.ms_iam.exception.messages;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundMessage {

    public static final String MESSAGE = "User not founded with username : " ;

    private UserNotFoundMessage() {
        throw new IllegalStateException("This class return an exception when a user not found");
    }
}
