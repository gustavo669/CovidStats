package org.umg;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CovidDataScheduler {

    @Value("${custom.scheduler.delay}")
    private long delay;

    private boolean firstRun = true;

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void delayedTask() throws InterruptedException {
        if (firstRun) {
            Thread.sleep(delay); // Esperar X milisegundos (15s)
            System.out.println("🕒 Ejecutando hilo después de 15 segundos...");
            // Aquí vas a llamar a tu servicio para consumir la API
            firstRun = false;
        }
    }
}