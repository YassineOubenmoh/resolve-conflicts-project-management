package ma.inwi.ms_notif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsNotifApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsNotifApplication.class, args);
	}

}
