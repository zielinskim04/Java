# WeatherAppProject

This Java application was developed as part of the "Advanced Object-Oriented and Functional Programming" course during the third semester of my studies at Warsaw University of Technology. The application allows users to view current, historical, and forecasted weather data for a selected location on a world map.

## Key Features
- Displaying current weather conditions
- Changing weather units (e.g., Celsius/Fahrenheit)
- Selecting a time range for weather data visualization
- Dynamically updating weather charts
- Saving weather data to a PDF file
- Displaying the name of the selected location on the map

## Technical Details
Each time a user selects a point on the map, chooses a weather type, and sets the desired time range, weather data is fetched via an API request from [Open-Meteo](https://open-meteo.com/). The name of the selected location is retrieved using [Nominatim](https://nominatim.org/).

The application uses the Java component **JMapViewer** for map visualization. The project follows the **Maven** structure, and the graphical user interface is built using the **Swing** library.

You can see the app here:
[![Demo Video](https://youtu.be/0VVqYSDy_Y4)](https://youtu.be/0VVqYSDy_Y4)

## Authors
- [Ada Wojterska](https://github.com/adawojterska)
- [Miłosz Zieliński](https://github.com/zielinskim04)

