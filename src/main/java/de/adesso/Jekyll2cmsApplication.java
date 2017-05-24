package de.adesso;

import de.adesso.service.CmdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jekyll2cmsApplication implements ApplicationRunner {

    @Autowired
    private CmdService cmdService;

    public static void main(String[] args) {
        // TODO: Replace in production
        // SpringApplication.run(Jekyll2cmsApplication.class, args).close();
        SpringApplication.run(Jekyll2cmsApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cmdService.init(args);
        boolean isParsed = cmdService.parse();
        if (isParsed) cmdService.execute();
    }
}
