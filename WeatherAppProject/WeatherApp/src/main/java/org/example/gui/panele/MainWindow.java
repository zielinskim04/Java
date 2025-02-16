package org.example.gui.panele;

import org.example.service.WeatherService;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    protected MapPanel mapPanel;

    public MainWindow() {
        super("Map Viewer");

        // Ustawienia okna głównego
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // Tworzenie paneli
        mapPanel = new MapPanel();
        WeatherService weatherService = new WeatherService();
        InfoPanel infoPanel = new InfoPanel(weatherService);

        // Ustawienie lokalizacji początkowej na nasze kochane MINI
        mapPanel.setDisplayPosition(new Coordinate(52.22194, 21.006775), 18);

        // Przekazanie panelu współrzędnych do panelu mapy
         mapPanel.setInfoPanel(infoPanel);

        // Przekazanie serwisu pogodowego
        mapPanel.setWeatherService(weatherService);

        // Dodanie paneli do okna
        add(mapPanel, BorderLayout.CENTER); // Mapa na środku
        add(infoPanel, BorderLayout.EAST); // Współrzędne po prawej

        // Dodanie menu (AppMenu)
        AppMenu appMenu = new AppMenu(this, mapPanel,weatherService, infoPanel);  // Przekazujemy istniejący MapPanel
        setJMenuBar(appMenu.createMenu());

        // Dodanie ikonki aplikacji
        setIconImage(new ImageIcon(getClass().getResource("/logo.png")).getImage());

        // Ustawienie na pełen ekran
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setVisible(true);
    }


}

