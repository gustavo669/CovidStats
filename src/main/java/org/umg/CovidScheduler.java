package org.umg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.umg.service.CovidApiService;

import java.util.logging.Logger;

@Component
public class CovidScheduler {

    private static final Logger logger = Logger.getLogger(CovidScheduler.class.getName());

    @Value("${custom.scheduler.delay}")
    private long delay;

    private final CovidApiService covidApiService;

    public CovidScheduler(CovidApiService covidApiService) {
        this.covidApiService = covidApiService;
    }

    private long startTime = System.currentTimeMillis();

    @Scheduled(fixedDelay = 1000)
    public void scheduleTask() {
        if (System.currentTimeMillis() > (startTime + delay)) {
            logger.info("🚀 Ejecutando hilo para consumir la API de COVID-19...");

            // Consumir y almacenar las regiones
            covidApiService.fetchAndStoreRegions();

            // Consumir y almacenar las provincias de Guatemala (ISO = GTM)
            covidApiService.fetchAndStoreProvinces("GTM");

            // Consumir y almacenar reportes de COVID-19 para Guatemala en una fecha específica
            covidApiService.fetchAndStoreReport("GTM", "2022-04-16");

            logger.info("✅ Proceso de consumo y almacenamiento finalizado.");
        }
    }
}
