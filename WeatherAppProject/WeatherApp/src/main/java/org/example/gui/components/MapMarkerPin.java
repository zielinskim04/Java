package org.example.gui.components;

import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import java.awt.*;

public class MapMarkerPin extends MapMarkerDot {
    private final Image pinImage;

    public MapMarkerPin(double lat, double lon, Image pinImage) {
        super(lat, lon);
        // Skalowanie obrazu pinezki (zmniejszenie 10 razy)
        this.pinImage = pinImage.getScaledInstance(
                pinImage.getWidth(null) / 10,
                pinImage.getHeight(null) / 10,
                Image.SCALE_SMOOTH
        );
    }

    @Override
    public void paint(Graphics g, Point position, int radius) {
        // Rysowanie obrazu pinezki
        if (pinImage != null) {
            int imageWidth = pinImage.getWidth(null);
            int imageHeight = pinImage.getHeight(null);
            g.drawImage(pinImage, position.x - imageWidth / 2, position.y - imageHeight, null);
        } else {
            // Fallback: domyślny znacznik, jeśli obraz nie został załadowany
            super.paint(g, position, radius);
        }
    }
}
