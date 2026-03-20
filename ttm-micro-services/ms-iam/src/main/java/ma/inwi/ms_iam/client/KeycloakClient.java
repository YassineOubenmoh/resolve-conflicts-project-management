package ma.inwi.ms_iam.client;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "keycloak-client",
        url = "http://localhost:8091")
public interface KeycloakClient {

    @PostMapping(
            value = "/realms/ttm/protocol/openid-connect/token",
            consumes = "application/x-www-form-urlencoded")
    ResponseEntity<AccessTokenResponse> login(@RequestBody Map<String, ?> form);


    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/logout",
            consumes = "application/x-www-form-urlencoded")
    ResponseEntity<Void> logout(@PathVariable String realm, @RequestBody Map<String, ?> form);


}
