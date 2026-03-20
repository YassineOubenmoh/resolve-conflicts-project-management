package ma.inwi.ms_iam.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {

    private String accessToken;
    private Long expIn;
    private Long refExpIn;
    private String refreshToken;
    private long refreshExpiresIn;
    private String tokenType;


    public TokenDto(String accessToken, String refreshToken) {
    }
}