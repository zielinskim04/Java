package org.example.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.parser.CurrentWeatherParser;

public class WeatherService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.open-meteo.com/v1";
    private String currentUnit = "metric"; // Domyślnie metryczny system

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Metoda do zmiany jednostek
    public void setUnit(String unit) {
        if (!unit.equals("metric") && !unit.equals("imperial")) {
            throw new IllegalArgumentException("Invalid unit. Allowed values: 'metric', 'imperial'");
        }
        this.currentUnit = unit;
    }

    // Zwracanie String do pdf
    public String getCurrentWeathertoPDF(double lat, double lon) throws Exception {
        // URL do pobierania danych w jednostkach metrycznych, dodane zachmurzenie
        String url = String.format(
                "%s/forecast?latitude=%.6f&longitude=%.6f&current=temperature_2m,wind_speed_10m,wind_direction_10m,precipitation,cloudcover&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm",
                BASE_URL, lat, lon
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch weather data: " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode current = root.get("current");

        double temperature = current.get("temperature_2m").asDouble();
        double windSpeed = current.get("wind_speed_10m").asDouble();
        int windDirection = current.get("wind_direction_10m").asInt();
        double precipitation = current.get("precipitation").asDouble();
        int cloudCover = current.get("cloudcover").asInt();

        // Zmiana jednostek
        if (currentUnit.equals("imperial")) {
            temperature = temperature * 9 / 5 + 32; // °C -> °F
            windSpeed = windSpeed / 1.609;          // km/h -> mph
            precipitation = precipitation / 25.4;    // mm -> inch
        }


        // Format: jedna linia z aktualnymi danymi
        return String.format(
                "Current Weather | Temp: %.1f %s | Wind: %.1f %s (%d°) | Precipitation: %.1f %s | Cloud cover: %d%%",
                temperature, currentUnit.equals("imperial") ? "°F" : "°C",
                windSpeed, currentUnit.equals("imperial") ? "mph" : "km/h",
                windDirection,
                precipitation, currentUnit.equals("imperial") ? "inch" : "mm",
                cloudCover
        );
    }

    public Map<String, Object> getCurrentWeather(double lat, double lon) throws Exception {
        // Pobieranie danych z API
        String url = String.format(
                "%s/forecast?latitude=%.6f&longitude=%.6f&current=temperature_2m,wind_speed_10m,wind_direction_10m,precipitation,cloudcover&temperature_unit=celsius&wind_speed_unit=kmh&precipitation_unit=mm",
                BASE_URL, lat, lon
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch weather data: " + response.statusCode());
        }

        // Parsowanie JSON
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode current = root.get("current");

        double temperature = current.get("temperature_2m").asDouble();
        double windSpeed = current.get("wind_speed_10m").asDouble();
        int windDirection = current.get("wind_direction_10m").asInt();
        double precipitation = current.get("precipitation").asDouble();
        int cloudCover = current.get("cloudcover").asInt();

        // Tworzenie parsera i pobieranie indeksów ikon
        CurrentWeatherParser parser = new CurrentWeatherParser();
        int tempIconIndex = parser.getTemperatureIconIndex(temperature);
        int windIconIndex = parser.getWindIconIndex(windSpeed);
        int cloudIconIndex = parser.getCloudIconIndex(cloudCover,precipitation);

        // Przeliczanie jednostek
        if (currentUnit.equals("imperial")) {
            temperature = temperature * 9 / 5 + 32; // °C -> °F
            windSpeed = windSpeed / 1.609;          // km/h -> mph
            precipitation = precipitation / 25.4;    // mm -> inch
        }

        String windDirectionString = getWindDirection(windDirection);

        // Przygotowanie mapy z danymi pogodowymi i ścieżkami do ikon
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("temperature", temperature);
        weatherData.put("windSpeed", windSpeed);
        weatherData.put("windDirection", windDirectionString);
        weatherData.put("precipitation", precipitation);
        weatherData.put("cloudCover", cloudCover);
        weatherData.put("temperatureIcon", parser.getTemperatureIcons().get(tempIconIndex));
        weatherData.put("windIcon", parser.getWindIcons().get(windIconIndex));
        weatherData.put("cloudIcon", parser.getCloudIcons().get(cloudIconIndex));

        return weatherData;
    }

    public String getForecast(double lat, double lon, int days) throws Exception {
        String url = String.format(
                "%s/forecast?latitude=%.6f&longitude=%.6f&daily=temperature_2m_max,temperature_2m_min,wind_speed_10m_max,wind_direction_10m_dominant,precipitation_sum,precipitation_probability_max&forecast_days=%d",
                BASE_URL, lat, lon, days);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode daily = root.get("daily");

        StringBuilder forecast = new StringBuilder();
        for (int i = 0; i < daily.get("time").size(); i++) {
            double maxTemp = daily.get("temperature_2m_max").get(i).asDouble();
            double minTemp = daily.get("temperature_2m_min").get(i).asDouble();
            double maxWind = daily.get("wind_speed_10m_max").get(i).asDouble();
            int windDirection = daily.get("wind_direction_10m_dominant").get(i).asInt();
            double precipitation = daily.get("precipitation_sum").get(i).asDouble();
            int precipProbability = daily.get("precipitation_probability_max").get(i).asInt();

            // Zmiana jednostek
            if (currentUnit.equals("imperial")) {
                maxTemp = maxTemp * 9 / 5 + 32;  // °C -> °F
                minTemp = minTemp * 9 / 5 + 32;  // °C -> °F
                maxWind = maxWind / 1.609;       // km/h -> mph
                precipitation = precipitation / 25.4; // mm -> inches
            }

            String windDirStr = getWindDirection(windDirection);

            forecast.append(String.format("Date: %s\n", daily.get("time").get(i).asText()));
            forecast.append(String.format("  Max temp: %.1f%s\n",
                    maxTemp, currentUnit.equals("imperial") ? "°F" : "°C"));
            forecast.append(String.format("  Min temp: %.1f%s\n",
                    minTemp, currentUnit.equals("imperial") ? "°F" : "°C"));
            forecast.append(String.format("  Max wind: %.1f%s from %s\n",
                    maxWind, currentUnit.equals("imperial") ? "mph" : "km/h", windDirStr));
            forecast.append(String.format("  Precipitation: %.1f%s (Probability: %d%%)\n\n",
                    precipitation, currentUnit.equals("imperial") ? "in" : "mm", precipProbability));
        }
        return forecast.toString();
    }

    public String getHistoricalWeather(double lat, double lon, int days) throws Exception {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1); // days - 1, aby uwzględnić dzisiejszy dzień

        String url = String.format("%s/forecast?latitude=%.6f&longitude=%.6f&start_date=%s&end_date=%s&daily=temperature_2m_max,temperature_2m_min,wind_speed_10m_max,wind_direction_10m_dominant,precipitation_sum",
                BASE_URL, lat, lon, startDate, endDate);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode daily = root.get("daily");

        StringBuilder historical = new StringBuilder();
        for (int i = 0; i < daily.get("time").size(); i++) {
            double maxTemp = daily.get("temperature_2m_max").get(i).asDouble();
            double minTemp = daily.get("temperature_2m_min").get(i).asDouble();
            int windDirection = daily.get("wind_direction_10m_dominant").get(i).asInt();
            double maxWind = daily.get("wind_speed_10m_max").get(i).asDouble();
            double precipitation = daily.get("precipitation_sum").get(i).asDouble();

            // Zmiana jednostek
            if (currentUnit.equals("imperial")) {
                maxTemp = maxTemp * 9 / 5 + 32;  // °C -> °F
                minTemp = minTemp * 9 / 5 + 32;  // °C -> °F
                maxWind = maxWind / 1.609;
                precipitation = precipitation / 25.4; // mm -> inches
            }

            String windDirStr = getWindDirection(windDirection);

            historical.append(String.format("Date: %s\n", daily.get("time").get(i).asText()));
            historical.append(String.format("  Max temp: %.1f%s\n",
                    maxTemp, currentUnit.equals("imperial") ? "°F" : "°C"));
            historical.append(String.format("  Min temp: %.1f%s\n",
                    minTemp, currentUnit.equals("imperial") ? "°F" : "°C"));
            historical.append(String.format("  Max wind: %.1f%s from %s\n",
                    maxWind, currentUnit.equals("imperial") ? "mph" : "km/h", windDirStr));
            historical.append(String.format("  Precipitation: %.1f%s\n\n",
                    precipitation, currentUnit.equals("imperial") ? "in" : "mm"));
        }
        return historical.toString();
    }

    // Metoda do wyznaczenia kierunku wiatru jako
    private String getWindDirection(int degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(((degrees % 360) / 22.5)) % 16;
        return directions[index];
    }

    public String getCurrentUnit() {
        return currentUnit;
    }
}
