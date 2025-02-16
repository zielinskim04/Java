package org.example.parser;

import java.util.Arrays;
import java.util.List;

// Dane do pogody obecnej nie są zwracane w postaci string, więc ta klasa działą odmiennie
// od HistoricalDataParser i ForecastParser, zawarte są tu odniesienia do grafik i odpowiednie ich wywoływanie
public class CurrentWeatherParser {
    private List<String> temperatureIcons = Arrays.asList(
            "src/main/resources/icons/freezing.png",
            "src/main/resources/icons/cool.png",
            "src/main/resources/icons/hot.png",
            "src/main/resources/icons/very_hot.png"
    );

    private List<String> windIcons = Arrays.asList(
            "src/main/resources/icons/light_wind.png",
            "src/main/resources/icons/moderate_wind.png",
            "src/main/resources/icons/strong_wind.png"
    );

    private List<String> cloudIcons = Arrays.asList(
            "src/main/resources/icons/sun.png",
            "src/main/resources/icons/partlycloudy.png",
            "src/main/resources/icons/cloudy.png",
            "src/main/resources/icons/rain_without_clouds.png",
            "src/main/resources/icons/rain_sun.png",
            "src/main/resources/icons/rain.png"
    );

    public int getTemperatureIconIndex(double temp) {
        if (temp < 0) return 0;
        if (temp < 20) return 1;
        if (temp < 35) return 2;
        return 3;
    }

    public int getWindIconIndex(double speed) {
        if (speed < 5) return 0;
        if (speed > 35) return 2;
        return 1;
    }

    public int getCloudIconIndex(int cloudCover,double precipitation) {
        if (cloudCover < 30 && precipitation < 0.1) return 0;
        if (cloudCover < 30 && precipitation > 0.1) return 3;
        if (cloudCover < 70 && precipitation < 0.1) return 1;
        if (cloudCover < 70 && precipitation > 0.1) return 4;
        if (precipitation < 0.1) return 2;
        return 5;
    }

    public List<String> getTemperatureIcons() { return temperatureIcons; }
    public List<String> getWindIcons() { return windIcons; }
    public List<String> getCloudIcons() { return cloudIcons; }
}
