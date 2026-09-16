package com.backend.wms.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FlyWayRunner implements CommandLineRunner {

    private final Flyway flyway;

    public FlyWayRunner(Flyway flyway) {
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) {
        System.out.println("Flyway migration start");

        MigrateResult migrationsApplied = flyway.migrate();
        System.out.println("Applied " + migrationsApplied.migrationsExecuted);

        System.out.println("Applied migrations:");
        for (MigrationInfo info : flyway.info().applied()) {
            System.out.println(info.getVersion() + " - " + info.getDescription());
        }
    }
}
