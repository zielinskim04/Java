package org.example.gui.panele;

import javax.swing.*;
import java.awt.*;

// Obsługa przycisków do ziany pogody
public class WeatherButtonPanel extends JPanel {
    private final JButton currentWeatherBtn;
    private final JButton forecastBtn;
    private final JButton historicalBtn;
    private int lastClicked;

    public WeatherButtonPanel(Runnable currentWeatherAction,
                              Runnable forecastAction,
                              Runnable historicalAction) {
        currentWeatherBtn = new JButton("Current weather");
        forecastBtn = new JButton("Forecast");
        historicalBtn = new JButton("Historic data");

        currentWeatherBtn.addActionListener(e -> {
            lastClicked = 1; // Ustawienie wartości lastClicked na 1
            currentWeatherAction.run(); // Wywołanie akcji przypisanej do przycisku
        });
        forecastBtn.addActionListener(e -> {
            lastClicked = 2;
            forecastAction.run();
        });
        historicalBtn.addActionListener(e -> {
            lastClicked = 3;
            historicalAction.run();
        });

        add(currentWeatherBtn);
        add(forecastBtn);
        add(historicalBtn);

        enableButtons(false);
    }

    protected void enableButtons(boolean enable) {
        currentWeatherBtn.setEnabled(enable);
        forecastBtn.setEnabled(enable);
        historicalBtn.setEnabled(enable);
    }

    protected JButton getCurrentWeatherBtn() {
        return currentWeatherBtn;
    }

    protected JButton getForecastBtn() {
        return forecastBtn;
    }

    protected JButton getHistoricalBtn() {
        return historicalBtn;
    }

    protected int getLastClicked() {
        return lastClicked;
    }
}
