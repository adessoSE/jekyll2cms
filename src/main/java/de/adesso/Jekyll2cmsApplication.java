package de.adesso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jekyll2cmsApplication implements CommandLineRunner {

	@Autowired private RepoController repoController;
	@Autowired private JekyllController jekyllController;

	public static void main(String[] args) {
		SpringApplication.run(Jekyll2cmsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("Starting ...");

		// Clone Repo
		repoController.initRepo();
		repoController.cloneRemoteRepo();

		// Run Jekyll
		jekyllController.initJekyll();
		jekyllController.runJekyll();
	}
}
