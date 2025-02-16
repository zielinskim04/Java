package org.example.gui.panele;

import org.example.gui.components.MapMarkerPin;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.example.service.WeatherService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapPanel extends JMapViewer {

    // Ładowanie nowej pinezki
    ImageIcon icon = new ImageIcon(getClass().getResource("/pin2.png"));
    Image pinImage = icon.getImage();

    private double lat;
    private double lon;
    private InfoPanel infoPanel;
    private MapMarkerPin lastMarker;
    private WeatherService weatherService; // Dodano obsługę serwisu pogodowego

    public MapPanel() {
        // Ustawienie źródła kafelków
        setTileSource(new OsmTileSource.Mapnik());

        // Włączenie przesuwania mapy
        setScrollWrapEnabled(true);

        // Obsługa kliknięć na mapie
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Lewy przycisk myszy
                    int x = e.getX();
                    int y = e.getY();
                    lat = getPosition(x,y).getLat();
                    lon = getPosition(x,y).getLon();

                    // Wyświetlenie współrzędnych w panelu
                    if (infoPanel != null) {
                        infoPanel.changeCoordinates(lat, lon);
                    }

                    // Wywołanie aktualizacji danych pogodowych
                    if (weatherService != null && infoPanel != null) {
                        // Współrzędne zostaną zaktualizowane przez changeCoordinates z wyższego warunku
                        // a następnie fetchCurrentWeather zostanie wywołane
                        infoPanel.fetchCurrentWeather();
                    }

                    // Usunięcie poprzedniego znacznika, jeśli istnieje
                    if (lastMarker != null) {
                        removeMapMarker(lastMarker);
                    }

                    // Dodanie nowego znacznika
                    lastMarker = new MapMarkerPin(lat, lon, pinImage);
                    //lastMarker = new MapMarkerDot(lat, lon);
                    addMapMarker(lastMarker);

                    // Wymuszenie odświeżenia mapy - wreszcie dziala ow
                    SwingUtilities.invokeLater(() -> {
                        revalidate(); // Wymusza przeliczenie układu
                        repaint();    // Rysowanie mapy i wszystkich markerów
                    });

                }
            }
        });

        // Obsługa przesuwania mapy
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                    repaint();
                }
            }
        });
    }

    // Metoda do przekazania panelu info
    protected void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    // Metoda do przekazania serwisu pogodowego
    protected void setWeatherService(WeatherService weatherService) { this.weatherService = weatherService; }

    // gettery, bo potrzebne do pdf

    protected double getLat() {
        return lat;
    }

    protected double getLon() {
        return lon;
    }

    protected InfoPanel getInfoPanel() {
        return infoPanel;
    }
}
