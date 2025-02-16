package org.example.gui.panele;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.example.service.WeatherService;

public class AppMenu {

    private MapPanel mapPanel;  // Przechowujemy referencję do istniejącego MapPanel
    private final JFrame parentFrame;
    private final WeatherService weatherService;
    private final InfoPanel infoPanel;

    // Zmieniamy konstruktor, aby przyjmował istniejący MapPanel
    public AppMenu(JFrame parentFrame, MapPanel mapPanel, WeatherService weatherService, InfoPanel infoPanel) {
        this.parentFrame = parentFrame;
        this.mapPanel = mapPanel;  // Przechowujemy przekazaną referencję
        this.weatherService = weatherService;
        this.infoPanel = infoPanel;
    }

    public JMenuBar createMenu() {
        // Tworzenie paska menu
        JMenuBar menuBar = new JMenuBar();

        // --------------- Tworzenie "Exit" ----------------------------------
        JMenuItem exitItem = new JMenuItem("Exit");

        // Obsługa zamknięcia aplikacji
        exitItem.addActionListener(e -> System.exit(0));

        // --------------- Tworzenie menu "File" ----------------------------------
        JMenu fileMenu = new JMenu("File");
        JMenuItem pdfItem = new JMenuItem("Export as pdf");

        // Obsługa opcji "Export as pdf"
        pdfItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Teraz używamy istniejącego MapPanel do pobrania współrzędnych
                    double lat = mapPanel.getLat(); // Pobieramy lat
                    double lon = mapPanel.getLon();  // Pobieramy lon
                    exportToPDF(weatherService, lat, lon, 7);  // Wywołanie eksportu do PDF
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        fileMenu.add(pdfItem);

        // -------------------------- Tworzenie menu "Help" --------------------------------
        JMenu infoMenu = new JMenu("Info");
        JMenuItem aboutItem = new JMenuItem("About");

        // Obsługa opcji "About"
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(parentFrame,
                    "Map Viewer \nVersion 1.0\nDeveloped by \nAda Wojterska and Miłosz Zieliński",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        infoMenu.add(aboutItem);

        // -------------------------- Tworzenie menu "Jednostki" --------------------------------
        JMenu jednostkiMenu = new JMenu("Units");

        // Tworzenie elementów wyboru jednostek (metryczne i imperialne)
        JRadioButtonMenuItem metricSystem = new JRadioButtonMenuItem("Metric system");
        JRadioButtonMenuItem imperialSystem = new JRadioButtonMenuItem("Imperial system");

        // Ustawienie jednej jednostki jako domyślnej
        metricSystem.setSelected(true); // Domyślnie wybrany system metryczny

        // Dodanie elementów do grupy, aby zawsze była tylko jedna wybrana jednostka
        ButtonGroup group = new ButtonGroup();
        group.add(metricSystem);
        group.add(imperialSystem);

        // Dodanie elementów do menu
        jednostkiMenu.add(metricSystem);
        jednostkiMenu.add(imperialSystem);

        // Dodanie akcji do menu (można dodać logikę konwersji jednostek)
        metricSystem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weatherService.setUnit("metric"); // Zmiana na system metryczny

                clickLastClicked(mapPanel);

                ((WeatherDataRefresher)infoPanel).refreshWeatherData();
            }

        });

        imperialSystem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                weatherService.setUnit("imperial"); // Zmiana na system imperialny

                clickLastClicked(mapPanel);

                ((WeatherDataRefresher)infoPanel).refreshWeatherData();
            }
        });



        // ------------------------- Dodanie menu do paska menu ---------------------------
        menuBar.add(fileMenu);
        menuBar.add(infoMenu);
        menuBar.add(jednostkiMenu);
        menuBar.add(exitItem);

        return menuBar;
    }

    //aby nie powatrzać kodu w zamianie jednostek:)
    private void clickLastClicked(MapPanel mapPanel) {
        int i = mapPanel.getInfoPanel().getButtonPanel().getLastClicked();

        if (i == 1) {
            for (ActionListener listener : mapPanel.getInfoPanel().getButtonPanel().getCurrentWeatherBtn().getActionListeners()) {
                listener.actionPerformed(new ActionEvent(mapPanel.getInfoPanel().getButtonPanel().getCurrentWeatherBtn(), ActionEvent.ACTION_PERFORMED, null));
            }
        } else if (i == 2) {
            for (ActionListener listener : mapPanel.getInfoPanel().getButtonPanel().getForecastBtn().getActionListeners()) {
                listener.actionPerformed(new ActionEvent(mapPanel.getInfoPanel().getButtonPanel().getForecastBtn(), ActionEvent.ACTION_PERFORMED, null));
            }
        } else {
            for (ActionListener listener : mapPanel.getInfoPanel().getButtonPanel().getHistoricalBtn().getActionListeners()) {
                listener.actionPerformed(new ActionEvent(mapPanel.getInfoPanel().getButtonPanel().getHistoricalBtn(), ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }


    // Funkcja eksportu do PDF
    private static void exportToPDF(WeatherService weatherService, double lat, double lon, int days) throws Exception {
        // Określamy ścieżkę do zapisu pliku PDF
        String filePath = "weather_report.pdf";

        // Tworzymy PdfWriter
        PdfWriter writer = new PdfWriter(filePath);

        // Tworzymy PdfDocument
        PdfDocument pdf = new PdfDocument(writer);

        // Tworzymy obiekt Document
        Document document = new Document(pdf);


        // Dodajemy współrzędne (Latitude, Longitude)
        document.add(new Paragraph("Coordinates:"));
        document.add(new Paragraph("Latitude: " + lat + ", Longitude: " + lon));

        document.add(new Paragraph("\n"));

        // Dodajemy dane o aktualnej pogodzie
        String currentWeather = weatherService.getCurrentWeathertoPDF(lat, lon);
        document.add(new Paragraph(currentWeather));

        document.add(new Paragraph("\n"));

        String forecastWeather = weatherService.getForecast(lat, lon, days);
        document.add(new Paragraph(forecastWeather));

        document.add(new Paragraph("\n"));

        // Dodajemy dane o historii pogody
        String historicalWeather = weatherService.getHistoricalWeather(lat, lon, days);
        document.add(new Paragraph(historicalWeather));

        document.add(new Paragraph("\n"));

        // Zamykanie dokumentu
        document.close();

    }
}
