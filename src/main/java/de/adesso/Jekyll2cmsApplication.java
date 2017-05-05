package de.adesso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jekyll2cmsApplication implements ApplicationRunner {

	@Autowired private RepoController repoController;
	@Autowired private JekyllController jekyllController;

	public static void main(String[] args) {
		SpringApplication.run(Jekyll2cmsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		for(String argument : args.getNonOptionArgs()) {
			System.out.println("Argument: " + argument);
		}

		// Clone Repo
		repoController.initLocalRepo();
		repoController.cloneRemoteRepo();

		// Run Jekyll
		jekyllController.runJekyll();
	}
}
