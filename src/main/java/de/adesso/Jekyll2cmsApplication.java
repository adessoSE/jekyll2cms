package de.adesso;

		import de.adesso.service.InitializationService;

		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.boot.ApplicationArguments;
		import org.springframework.boot.ApplicationRunner;
		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jekyll2cmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Jekyll2cmsApplication.class, args);
	}
}