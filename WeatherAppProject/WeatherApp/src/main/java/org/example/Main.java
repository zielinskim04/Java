package org.example;
import org.example.gui.panele.MainWindow;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        // Ustawienie User-Agent do api
        System.setProperty("http.agent", "MapApp/1.0");

        // Wymuszenie lokalizacji na US (kwestia odczytywania przez system kropek i przecinków)
        Locale.setDefault(Locale.US);

        new MainWindow();
    }
}
