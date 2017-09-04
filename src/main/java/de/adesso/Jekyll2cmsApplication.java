package de.adesso;

import de.adesso.service.InitializationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jekyll2cmsApplication implements ApplicationRunner {

	@Autowired
	private InitializationService initService;

	public static void main(String[] args) {
		SpringApplication.run(Jekyll2cmsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (!initService.init()) {
			System.exit(1);
		}
	}
}
