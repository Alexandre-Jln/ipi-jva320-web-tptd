package com.ipi.jva320.service;

import com.ipi.jva320.model.SalarieAideADomicile;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Ajoute des données de test si vide au démarrage
 */
@Component
public class DataInitService implements CommandLineRunner {

    @Autowired
    private SalarieAideADomicileService salarieAideADomicileService;

    @Override
    public void run(String @NonNull ... argv) throws Exception {
        if (this.salarieAideADomicileService.countSalaries() != 0) {
            return;
        }

        this.salarieAideADomicileService.creerSalarieAideADomicile(
                new SalarieAideADomicile(
                        "Jean Dupont",
                        LocalDate.of(2022, 12, 5),
                        LocalDate.of(2022, 12, 5),
                        20, 0,
                        80, 10, 1
                )
        );

        this.salarieAideADomicileService.creerSalarieAideADomicile(
                new SalarieAideADomicile(
                        "Marie Martin",
                        LocalDate.of(2021, 3, 10),
                        LocalDate.now(),
                        5, 1,
                        60, 8, 0
                )
        );

        this.salarieAideADomicileService.creerSalarieAideADomicile(
                new SalarieAideADomicile(
                        "Lucas Bernard",
                        LocalDate.of(2020, 6, 1),
                        LocalDate.now(),
                        2, 0,
                        90, 12, 1
                )
        );

        this.salarieAideADomicileService.creerSalarieAideADomicile(
                new SalarieAideADomicile(
                        "Sofia Rossi",
                        LocalDate.of(2023, 1, 15),
                        LocalDate.now(),
                        0, 0,
                        40, 5, 0
                )
        );

        this.salarieAideADomicileService.creerSalarieAideADomicile(
                new SalarieAideADomicile(
                        "Karim Benali",
                        LocalDate.of(2019, 9, 20),
                        LocalDate.now(),
                        3, 2,
                        100, 15, 1
                )
        );
    }
}
