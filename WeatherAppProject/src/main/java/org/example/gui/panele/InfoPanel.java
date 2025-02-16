package org.example.gui.panele;

import org.example.parser.*;
import org.example.service.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

public class InfoPanel extends JPanel implements WeatherDataRefresher{

    private final LocationDisplay locationDisplay;
    private final WeatherButtonPanel buttonPanel;
    private final WeatherDataPanel weatherDataPanel;
    private final WeatherService weatherService;

    private double currentLat;
    private double currentLon;
    private JSlider daysSlider; // Suwak do wyboru liczby dni


    public InfoPanel(WeatherService weatherService) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(500, 600));

        this.weatherService = weatherService;

        locationDisplay = new LocationDisplay();
        buttonPanel = new WeatherButtonPanel(this::fetchCurrentWeather, this::fetchForecast, this::fetchHistoricalData);
        weatherDataPanel = new WeatherDataPanel();

        // Suwak i etykieta dla wyboru liczby dni prognozy
        daysSlider = new JSlider(3, 14, 7);
        daysSlider.setMajorTickSpacing(1);
        daysSlider.setPaintTicks(true);
        daysSlider.setPaintLabels(true);

        setupLayout();
    }

    private void setupLayout() {
        // Ustawienie siatki głównej
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridy = 0;
        add(locationDisplay, gbc);

        gbc.gridy = 1;
        add(buttonPanel, gbc);

        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(weatherDataPanel, gbc);

        // Dodanie suwaka (JSlider) do wyboru liczby dni prognozy
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        daysSlider.addChangeListener( e -> {
            int i = buttonPanel.getLastClicked();
            if (i == 1){
                for (ActionListener listener : buttonPanel.getCurrentWeatherBtn().getActionListeners()) {
                    listener.actionPerformed(new ActionEvent(buttonPanel.getCurrentWeatherBtn(), ActionEvent.ACTION_PERFORMED, null));
                }

            } else if (i == 2){
                for (ActionListener listener : buttonPanel.getForecastBtn().getActionListeners()) {
                    listener.actionPerformed(new ActionEvent(buttonPanel.getForecastBtn(), ActionEvent.ACTION_PERFORMED, null));
                }

            }else{
                for (ActionListener listener : buttonPanel.getHistoricalBtn().getActionListeners()) {
                    listener.actionPerformed(new ActionEvent(buttonPanel.getHistoricalBtn(), ActionEvent.ACTION_PERFORMED, null));
                }

            }

            ((WeatherDataRefresher)this).refreshWeatherData();
        });
        daysSlider.setMajorTickSpacing(1);
        daysSlider.setPaintTicks(true);
        daysSlider.setPaintLabels(true);
        add(daysSlider, gbc);
    }


    // Metoda do zmiany współrzędnych w locationDisplay
    public void changeCoordinates(double lat, double lon) {
        this.currentLat = lat;
        this.currentLon = lon;
        locationDisplay.updateCoordinates(lat, lon);
        buttonPanel.enableButtons(true);
    }

    // Metody do wywoływania danych pogodowych z WeatherService
    public void fetchCurrentWeather() {
        try {
            Map<String, Object> weatherData = weatherService.getCurrentWeather(currentLat, currentLon);

            // Pobranie aktualnej jednostki
            String currentUnit = weatherService.getCurrentUnit();

            // Usunięcie poprzednich danych
            weatherDataPanel.remove();
            // Wyciąganie danych i ikon
            double temperature = (double) weatherData.get("temperature");
            double windSpeed = (double) weatherData.get("windSpeed");
            String windDirectionString = (String) weatherData.get("windDirection");
            double precipitation = (double) weatherData.get("precipitation");
            int cloudCover = (int) weatherData.get("cloudCover");
            String tempIcon = (String) weatherData.get("temperatureIcon");
            String windIcon = (String) weatherData.get("windIcon");
            String cloudIcon = (String) weatherData.get("cloudIcon");

            // Przekazanie danych do panelu wyświetlającego pogodę
            weatherDataPanel.displayWeatherData(
                    temperature, windSpeed, windDirectionString, precipitation, cloudCover,
                    tempIcon, windIcon, cloudIcon, currentUnit
            );
        } catch (Exception e) {
            handleError(e);
        }
    }


    private void fetchForecast() {
        try {
            // Pobranie liczby dni z suwaka
            int days = daysSlider.getValue();

            // Wywołanie metody getForecast z liczbą dni
            String forecast = weatherService.getForecast(currentLat, currentLon, days);

            // Parsowanie danych prognozy
            ForecastParser parser = new ForecastParser(forecast);

            // Czyszczenie panelu i dodawanie nowych danych
            weatherDataPanel.remove();

            // Wyświetlanie wykresów (maks./min. temperatury oraz maks. wiatru)
            weatherDataPanel.displayChart3(parser.maxminTempChart());
            weatherDataPanel.displayChart3(parser.maxWindChart());

            // Wyświetlanie tabeli prognozy
            weatherDataPanel.displayTable(parser.createForecastTable(), "Weather forecast");

        } catch (Exception e) {
            handleError(e);
        }
    }


    private void fetchHistoricalData() {
        try {
            int days = daysSlider.getValue(); // Pobierz wartość z suwaka
            String historical = weatherService.getHistoricalWeather(currentLat, currentLon, days);
            HistoricalDataParser parser = new HistoricalDataParser(historical);

            // Aktualizacja widoków
            weatherDataPanel.remove();
            weatherDataPanel.displayChart3(parser.maxminTempChart());
            weatherDataPanel.displayChart3(parser.maxWindChart());
            weatherDataPanel.displayTable(parser.createHistoricalTable(), "Historical weather (" + days + " days)");
        } catch (Exception e) {
            handleError(e);
        }
    }

    // Metoda do obsługi błędów
    private void handleError(Exception e) {
        JOptionPane.showMessageDialog(this, "Error during downloading data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    @Override
    public void refreshWeatherData() {
    }

    protected WeatherButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}
