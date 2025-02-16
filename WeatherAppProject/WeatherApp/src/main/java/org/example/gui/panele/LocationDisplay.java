package org.example.gui.panele;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationDisplay extends JLabel{


    public LocationDisplay() {

        // Ustawienie początkowego tekstu
        setText("Click on the map");

        // Ustawienie czcionki i koloru
        setFont(new Font("Dialog", Font.BOLD, 20));
        setForeground(Color.BLACK);
        setSize(new Dimension(330, 50));

        setBackground(new Color(0xEEEEEE));
        setFocusable(false);  // Zablokowanie fokusu

        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

    }

    // Tworzenie zapytania o nazwę lokalizacji na podstawie współrzędnych
    public void updateCoordinates(double lat, double lon) {
        String locationName = "Unknown location";

        try {
            // Tworzymy URL dla żądania do Nominatim
            String urlString = String.format(
                    "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=%.6f&lon=%.6f",
                    lat, lon
            );
            URL url = new URL(urlString);

            // Wysyłamy żądanie HTTP
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "AplikacjaPogodowa/1.0 (adawojterska@gmail.com)");

            // Czytamy odpowiedź
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parsujemy odpowiedź JSON
            JSONObject json = new JSONObject(response.toString());
            if (json.optJSONObject("address") != null) {
                JSONObject address = json.optJSONObject("address");
                if (address.optString("city") != null && !address.optString("city").isEmpty()) {
                    locationName = address.optString("city");
                } else if (address.optString("town") != null && !address.optString("town").isEmpty()) {
                    locationName = address.optString("town");
                } else if (address.optString("village") != null && !address.optString("village").isEmpty()) {
                    locationName = address.optString("village");
                } else if (address.optString("country") != null && !address.optString("country").isEmpty()) {
                    locationName = address.optString("country");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            locationName = "Error retrieving location";
        }

        String text = "<html><div style='text-align:center;'>" +
                "Location:<br>" + locationName;

        setText(text);
    }
}