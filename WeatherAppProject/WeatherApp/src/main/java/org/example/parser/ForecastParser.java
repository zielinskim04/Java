package org.example.parser;

import java.awt.*;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

// Obiekt ten pomaga stworzyć odpowiednie struktury do prognozy pogody
public class ForecastParser  {
    private final String rawData;
    private String[] dates;
    private double[] maxTemp;
    private double[] minTemp;
    private double[] maxWind;
    private String[] windDirections;
    private double[] precipitation;
    private int[] precipitationProbability;
    private String tempUnit;  // °C lub °F
    private String windUnit;  // km/h lub mph
    private String precipUnit; // mm lub in

    public ForecastParser(String rawData) {
        this.rawData = rawData;

        String[] details = rawData.split("\n");

        // Określamy ilość dni na podstawie ilości dat w danych
        int numberOfDays = (int) rawData.lines()
                .filter(line -> line.trim().startsWith("Date:"))
                .count();

        // Deklaracja tablic
        dates = new String[numberOfDays];
        maxTemp = new double[numberOfDays];
        minTemp = new double[numberOfDays];
        maxWind = new double[numberOfDays];
        windDirections = new String[numberOfDays];
        precipitation = new double[numberOfDays];
        precipitationProbability = new int[numberOfDays];
        int dayIndex = -1;

        // Najpierw sprawdzamy jednostki z pierwszego wystąpienia
        for (String detail : details) {
            if (detail.contains("°F")) {
                tempUnit = "°F";
            } else if (detail.contains("°C")) {
                tempUnit = "°C";
            }
            if (detail.contains("mph")) {
                windUnit = "mph";
            } else if (detail.contains("km/h")) {
                windUnit = "km/h";
            }
            if (detail.contains("in") && detail.contains("Precipitation")) {
                precipUnit = "in";
            } else if (detail.contains("mm") && detail.contains("Precipitation")) {
                precipUnit = "mm";
            }
            if (tempUnit != null && windUnit != null && precipUnit != null) break;
        }

        // Iteracja przez dane
        for (String detail : details) {
            detail = detail.trim();
            if (detail.startsWith("Date:")) {
                dayIndex++;
                dates[dayIndex] = detail.replace("Date: ", "").trim();
            } else if (detail.startsWith("Max temp:")) {
                String value = detail.replace("Max temp: ", "")
                        .replace("°F", "")
                        .replace("°C", "")
                        .trim();
                maxTemp[dayIndex] = Double.parseDouble(value);
            } else if (detail.startsWith("Min temp:")) {
                String value = detail.replace("Min temp: ", "")
                        .replace("°F", "")
                        .replace("°C", "")
                        .trim();
                minTemp[dayIndex] = Double.parseDouble(value);
            } else if (detail.startsWith("Max wind:")) {
                String windInfo = detail.replace("Max wind: ", "").trim();
                String[] windParts = windInfo.split("from");
                String windValue = windParts[0]
                        .replace("mph", "")
                        .replace("km/h", "")
                        .trim();
                maxWind[dayIndex] = Double.parseDouble(windValue);
                if (windParts.length > 1) {
                    windDirections[dayIndex] = windParts[1].trim();
                }
            } else if (detail.startsWith("Precipitation:")) {
                String precipInfo = detail.replace("Precipitation: ", "").trim();
                if (precipInfo.contains("(")) {
                    String[] parts = precipInfo.split("\\(");
                    String precipValue = parts[0]
                            .replace("in", "")
                            .replace("mm", "")
                            .trim();
                    precipitation[dayIndex] = Double.parseDouble(precipValue);
                    if (parts.length > 1) {
                        String probValue = parts[1]
                                .replace("Probability: ", "")
                                .replace("%)", "")
                                .trim();
                        precipitationProbability[dayIndex] = Integer.parseInt(probValue);
                    }
                } else {
                    String precipValue = precipInfo
                            .replace("in", "")
                            .replace("mm", "")
                            .trim();
                    precipitation[dayIndex] = Double.parseDouble(precipValue);
                }
            }
        }
    }

    // Wykres temperatury
    public ChartPanel maxminTempChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < dates.length; i++) {
            dataset.addValue(maxTemp[i], "Max Temp", dates[i]);
            dataset.addValue(minTemp[i], "Min Temp", dates[i]);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Min and max temperature over dates",
                "Dates",
                "Temperature (" + tempUnit + ")",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        return chartPanel;
    }
    // Wykres do maksymalnej prędkości wiatru
    public ChartPanel maxWindChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < dates.length; i++) {
            dataset.addValue(maxWind[i], "Max Wind", dates[i]);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Max wind over dates",
                "Dates",
                "Max wind (" + windUnit + ")",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 200));

        return chartPanel;
    }

    // Tabela z danymi
    public JTable createForecastTable() {
//        String[] columnNames = {
//                "Date",
//                "Max Temp (" + tempUnit + ")",
//                "Min Temp (" + tempUnit + ")",
//                "Max Wind (" + windUnit + ")",
//                "Wind Direction",
//                "Precipitation (" + precipUnit + ")",
//                "Precip. Probability (%)"
//        };

        String[] columnNames = {
                "<html>Date</html>",
                "<html>Max Temp<br>(" + tempUnit + ")</html>",
                "<html>Min Temp<br>(" + tempUnit + ")</html>",
                "<html>Max Wind<br>(" + windUnit + ")</html>",
                "<html>Wind<br>Direction</html>",
                "<html>Precipitation<br>(" + precipUnit + ")</html>",
                "<html>Precip. <br>Probability (%)</html>"
        };

        Object[][] data = new Object[dates.length][7];

        for (int i = 0; i < dates.length; i++) {
            data[i][0] = dates[i];
            data[i][1] = String.format("%.1f", maxTemp[i]);
            data[i][2] = String.format("%.1f", minTemp[i]);
            data[i][3] = String.format("%.1f", maxWind[i]);
            data[i][4] = windDirections[i];
            data[i][5] = String.format("%.1f", precipitation[i]);
            data[i][6] = precipitationProbability[i];
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return table;
    }

}