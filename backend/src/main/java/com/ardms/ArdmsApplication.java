package com.ardms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Main entry point for the Automated Release & Deployment Management System (ARDMS).
 * <p>
 * This application provides a comprehensive platform for managing software releases,
 * tracking deployments across environments, and automating rollback procedures.
 */
@SpringBootApplication
public class ArdmsApplication {

    private static final Logger logger = LogManager.getLogger(ArdmsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ArdmsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("========================================================");
        logger.info("  ARDMS - Automated Release & Deployment Management System");
        logger.info("  Version: 1.0.0");
        logger.info("  Status: RUNNING");
        logger.info("  Swagger UI: http://localhost:8080/api/swagger-ui.html");
        logger.info("  API Docs: http://localhost:8080/api/v3/api-docs");
        logger.info("  Actuator: http://localhost:8080/api/actuator");
        logger.info("========================================================");
    }
}
