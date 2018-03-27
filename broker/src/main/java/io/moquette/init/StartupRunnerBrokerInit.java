package io.moquette.init;

import io.moquette.EmbeddedLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class StartupRunnerBrokerInit implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EmbeddedLauncher embeddedLauncher;

    @Autowired
    public StartupRunnerBrokerInit(EmbeddedLauncher embeddedLauncher) {
        this.embeddedLauncher = embeddedLauncher;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(">>>>>>>>>>>>>>> init moquette<<<<<<<<<<<<<");
        embeddedLauncher.initBroker();
    }
}
