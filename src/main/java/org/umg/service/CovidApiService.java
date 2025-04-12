package org.umg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.umg.model.Province;
import org.umg.model.Region;
import org.umg.model.Report;
import org.umg.repository.ProvinceRepository;
import org.umg.repository.RegionRepository;
import org.umg.repository.ReportRepository;

import java.util.logging.Logger;

@Service
public class CovidApiService {

    private static final Logger logger = Logger.getLogger(CovidApiService.class.getName());

    @Value("${custom.scheduler.delay}")
    private long delay;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    private final RestTemplate restTemplate;
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    public CovidApiService(RestTemplate restTemplate, RegionRepository regionRepository,
                           ProvinceRepository provinceRepository, ReportRepository reportRepository) {
        this.restTemplate = restTemplate;
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = new ObjectMapper();
    }

    // ✅ Obtener y almacenar regiones
    public void fetchAndStoreRegions() {
        String url = "https://covid-19-statistics.p.rapidapi.com/regions";
        String responseJson = makeApiRequest(url);
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode dataArray = root.get("data");

            for (JsonNode regionNode : dataArray) {
                String iso = regionNode.get("iso").asText();
                String name = regionNode.get("name").asText();

                Region region = new Region();
                region.setIso(iso);
                region.setName(name);

                regionRepository.save(region);
                logger.info("🌍 Región guardada: " + name);
            }

        } catch (Exception e) {
            logger.severe("❌ Error al parsear regiones: " + e.getMessage());
        }
    }

    // ✅ Obtener y almacenar provincias
    public void fetchAndStoreProvinces(String iso) {
        String url = "https://covid-19-statistics.p.rapidapi.com/provinces";
        String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("iso", iso)
                .toUriString();

        String responseJson = makeApiRequest(fullUrl);
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode dataArray = root.get("data");

            Region region = regionRepository.findByIso(iso);

            for (JsonNode provinceNode : dataArray) {
                String name = provinceNode.get("province").asText();

                Province province = new Province();
                province.setIso(iso);
                province.setName(name);
                province.setRegion(region);

                provinceRepository.save(province);
                logger.info("🏙️ Provincia guardada: " + name);
            }

        } catch (Exception e) {
            logger.severe("❌ Error al parsear provincias: " + e.getMessage());
        }
    }

    // ✅ Obtener y almacenar reportes
    public void fetchAndStoreReport(String iso, String date) {
        String url = "https://covid-19-statistics.p.rapidapi.com/reports";
        String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("iso", iso)
                .queryParam("date", date)
                .toUriString();

        String responseJson = makeApiRequest(fullUrl);
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode dataArray = root.get("data");

            for (JsonNode reportNode : dataArray) {
                String provinceName = reportNode.get("province").asText();
                int confirmed = reportNode.get("confirmed").asInt();
                int deaths = reportNode.get("deaths").asInt();
                int recovered = reportNode.get("recovered").asInt();

                Province province = provinceRepository.findByRegionIso(iso).stream()
                        .filter(p -> p.getName().equalsIgnoreCase(provinceName))
                        .findFirst()
                        .orElse(null);

                if (province == null) continue;

                Report report = new Report();
                report.setIso(iso);
                report.setProvince(provinceName);
                report.setDate(date);
                report.setConfirmed(confirmed);
                report.setDeaths(deaths);
                report.setRecovered(recovered);
                report.setProvinceEntity(province);

                reportRepository.save(report);
                logger.info("📊 Reporte guardado: " + provinceName + " [" + date + "]");
            }

        } catch (Exception e) {
            logger.severe("❌ Error al parsear reportes: " + e.getMessage());
        }
    }

    // ✅ Método para hacer la solicitud HTTP con cabeceras
    private String makeApiRequest(String url) {
        try {
            logger.info("📡 Haciendo solicitud a: " + url);
            return restTemplate.getForObject(url, String.class);
        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            logger.severe("❌ Error al consumir la API: Límite de solicitudes alcanzado. Esperando...");
            // Puedes poner un tiempo de espera o una política de reintentos aquí
            try {
                Thread.sleep(60000); // Espera 1 minuto antes de volver a intentar
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return null;
        } catch (Exception e) {
            logger.severe("❌ Error al consumir la API: " + e.getMessage());
            return null;
        }
    }
}
