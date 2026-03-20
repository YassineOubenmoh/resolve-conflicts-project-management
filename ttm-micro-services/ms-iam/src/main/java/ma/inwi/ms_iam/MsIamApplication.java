package ma.inwi.ms_iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsIamApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsIamApplication.class, args);
	}

}
