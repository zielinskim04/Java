package org.example.gui.panele;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class WeatherDataPanel extends JPanel {
    private final JPanel contentPanel;

    public WeatherDataPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(234, 234, 234));


        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(234, 234, 234));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

    }

    public void remove(){
        contentPanel.removeAll();
        revalidate();
        repaint();
    }

    public void displayWeatherData(
            double temperature, double windSpeed, String windDirectionString,
            double precipitation, int cloudCover,
            String tempIconPath, String windIconPath, String cloudIconPath,
            String currentUnit
    ) {
        // Dynamiczna zmiana jednostek
        String tempUnit = currentUnit.equals("imperial") ? "°F" : "°C";
        String speedUnit = currentUnit.equals("imperial") ? "mph" : "km/h";
        String precipUnit = currentUnit.equals("imperial") ? "inch" : "mm";

        // Tworzenie JLabel dla danych pogodowych
        JLabel tempLabel = new JLabel(String.format("Temperature: %.1f%s", temperature, tempUnit));
        JLabel windLabel = new JLabel(String.format("Wind: %.1f %s from %s", windSpeed, speedUnit, windDirectionString));
        JLabel precipitationLabel = new JLabel(String.format("Precipitation: %.1f %s", precipitation, precipUnit));
        JLabel cloudCoverLabel = new JLabel(String.format("Cloud cover: %d%%", cloudCover));

        // Zwiększenie czcionki i wyśrodkowanie tekstu
        Font largerFont = new Font("SansSerif", Font.BOLD, 16);
        tempLabel.setFont(largerFont);
        windLabel.setFont(largerFont);
        precipitationLabel.setFont(largerFont);
        cloudCoverLabel.setFont(largerFont);

        // Skalowanie ikon
        ImageIcon tempIcon = scaleIcon(tempIconPath, 70, 70);
        ImageIcon windIcon = scaleIcon(windIconPath, 70, 70);
        ImageIcon cloudIcon = scaleIcon(cloudIconPath, 70, 70);

        JLabel tempIconLabel = new JLabel(tempIcon);
        JLabel windIconLabel = new JLabel(windIcon);
        JLabel cloudIconLabel = new JLabel(cloudIcon);

        // Ustawienie wyśrodkowania elementów
        tempLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        tempIconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        windLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        windIconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        precipitationLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        cloudCoverLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        cloudIconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        // Dodawanie odstępu między elementami
        contentPanel.add(Box.createVerticalStrut(20)); // Odstęp od góry
        contentPanel.add(tempLabel);
        contentPanel.add(tempIconLabel);
        contentPanel.add(Box.createVerticalStrut(20)); // Odstęp między elementami
        contentPanel.add(windLabel);
        contentPanel.add(windIconLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(precipitationLabel);
        contentPanel.add(cloudCoverLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(cloudIconLabel);

        contentPanel.revalidate();
        contentPanel.repaint();
    }



    // Metoda do skalowania ikon
    private ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }


    protected void displayTable(JTable table, String titleText) {

        // Dodajemy wykres do contentPanel
        if (table != null) {

            // Stylizacja tabeli
            table.setFillsViewportHeight(true);
            table.setRowHeight(30);
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setBackground(Color.LIGHT_GRAY);
            table.setShowGrid(false);
            table.setGridColor(Color.DARK_GRAY);

            table.setBackground(null);

            // Dodanie ramki do tabeli (na zewnątrz)
            Border outerBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 2); // Jasna ramka
            table.setBorder(outerBorder); // Ustawienie ramki dla tabeli

            Border innerBorder = BorderFactory.createLineBorder(new Color(220, 220, 220), 1); // Jaśniejsza ramka dla komórek
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    cellComponent.setBackground(null);
                    setBorder(innerBorder); // Dodanie ramki do komórek
                    setHorizontalAlignment(CENTER); // Wyśrodkowanie tekstu w komórkach
                    return cellComponent;
                }
            });

            JScrollPane scrollPanel = new JScrollPane(table); // Tworzymy JScrollPane z tabelą

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 0, 20, 0)); // Odstępy w panelu
            JLabel title = new JLabel(titleText, SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 18));
            panel.add(title, BorderLayout.NORTH); // Dodanie tytułu do panelu
            panel.add(scrollPanel, BorderLayout.CENTER); // Dodanie JScrollPane do panelu
            if (titleText == "Weather forecast"){
                panel.setPreferredSize(new Dimension(400, 420));
            } else {
                panel.setPreferredSize(new Dimension(400, 420));
            }

            table.setPreferredSize(new Dimension(400, 420)); // Ustawienie preferowanego rozmiaru tabeli
            contentPanel.add(panel); // Dodanie panelu do contentPanel

            revalidate();
            repaint();
        } else {
            System.err.println("Table is null or invalid.");
        }
    }

    protected void displayChart3(ChartPanel chartPanel) {
        // Dodajemy wykres do contentPanel
        if (chartPanel != null) {
            chartPanel.setPreferredSize(new Dimension(400, 300)); // Rozmiar wykresu
            contentPanel.add(chartPanel); // Dodanie wykresu do panelu
            contentPanel.add(Box.createVerticalStrut(10)); // Dodanie odstępu poniżej wykresu

            // Uzyskujemy dostęp do CategoryPlot
            JFreeChart chart = chartPanel.getChart();

            chart.setBackgroundPaint(null);

            // Usunięcie tła za tytułem
            if (chart.getTitle() != null) {
                chart.getTitle().setBackgroundPaint(null); // Usunięcie tła tytułu
                chart.getTitle().setFont(new Font("Arial", Font.BOLD, 17));
                chart.getTitle().setMargin(10,1,10,0);
            }

            // Usunięcie tła za legendą
            if (chart.getLegend() != null) {
                chart.getLegend().setBackgroundPaint(null); // Usunięcie tła legendy
            }

            CategoryPlot plot = (CategoryPlot) chart.getPlot();

            plot.setOutlinePaint(Color.DARK_GRAY); // Obramowanie wykresu
            plot.setBackgroundPaint(new Color(220, 220, 220)); // Gradientowe tło
            plot.setOutlinePaint(null); // Obramowanie wykresu

            CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesStroke(1, new BasicStroke(2.0f)); // Grubość linii dla pierwszej serii
            renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Grubość linii dla drugiej serii

            // Ustawienia dla punktów na linii
            renderer.setSeriesShapesVisible(0, true); // Widoczność punktów na linii dla pierwszej serii
            renderer.setSeriesShapesVisible(1, true); // Widoczność punktów na linii dla drugiej serii
            renderer.setSeriesShape(1, new Ellipse2D.Double(-3, -3, 6, 6)); // Kształt punktów dla pierwszej serii
            renderer.setSeriesShape(0, new Rectangle2D.Double(-3, -3, 6, 6)); // Kształt punktów dla drugiej serii
            renderer.setSeriesPaint(0, new Color(192, 80, 77));
            renderer.setSeriesPaint(1, new Color(79, 129, 189));

            // Przełądowanie i przerysowanie panelu
            revalidate();
            repaint();
        } else {
            System.err.println("Chart is null or invalid.");
        }
    }

}