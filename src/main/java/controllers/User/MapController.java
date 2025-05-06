package controllers.User;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.function.BiConsumer;
import java.util.Timer;
import java.util.TimerTask;

public class MapController {

    @FXML
    private WebView webView;
    
    @FXML
    private Button closeButton;
    
    private WebEngine webEngine;
    private BiConsumer<Double, Double> locationCallback;
    private double latitude;
    private double longitude;
    private Stage stage;
    private Timer pollTimer;
    
    public void initialize() {
        webEngine = webView.getEngine();
        
        // Enable hardware acceleration for better map performance
        webView.setContextMenuEnabled(false);
        
        // Set preferred size to ensure proper rendering
        webView.setPrefSize(800, 600);
        
        // Enable smooth scrolling for better map interaction
        webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/maps/smooth-scroll.css").toExternalForm());
        
        // Set up close button
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            this.stage = stage;
            stage.close();
        });
        
        // Enable JavaScript console logging
        webEngine.setOnAlert(event -> System.out.println("JS Alert: " + event.getData()));
    }
    
    /**
     * Loads the map for selecting a restaurant location
     * @param initialLat Initial latitude (0 if not set)
     * @param initialLng Initial longitude (0 if not set)
     * @param callback Callback function to receive the selected location
     */
    public void loadLocationPicker(double initialLat, double initialLng, BiConsumer<Double, Double> callback) {
        this.locationCallback = callback;
        this.latitude = initialLat;
        this.longitude = initialLng;
        
        System.out.println("Loading location picker with initial coordinates: " + initialLat + ", " + initialLng);
        
        URL mapUrl = getClass().getResource("/maps/restaurant-map.html");
        webEngine.load(mapUrl.toExternalForm());
        
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("Map loaded successfully");
                
                // Set initial coordinates in JavaScript
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("initialLat", initialLat);
                window.setMember("initialLng", initialLng);
                
                // Force map to redraw after a short delay
                Platform.runLater(() -> {
                    try {
                        // First invalidation
                        webEngine.executeScript("if (map) { map.invalidateSize(); }");
                        
                        // Schedule multiple invalidations to ensure tiles load properly
                        scheduleMapRefresh();
                        
                        // Add event listener for map move and zoom events
                        webEngine.executeScript(
                            "if (map) {" +
                            "  map.on('moveend', function() { map.invalidateSize(); });" +
                            "  map.on('zoomend', function() { map.invalidateSize(); });" +
                            "}"
                        );
                    } catch (Exception e) {
                        System.err.println("Error refreshing map: " + e.getMessage());
                    }
                });
                
                // Start polling for location confirmation
                startPollingForLocationConfirmation();
            }
        });
    }
    
    /**
     * Polls the hidden fields in the HTML to check if a location has been confirmed
     */
    private void startPollingForLocationConfirmation() {
        if (pollTimer != null) {
            pollTimer.cancel();
        }
        
        pollTimer = new Timer();
        pollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        String confirmed = (String) webEngine.executeScript(
                            "document.getElementById('locationConfirmed').value"
                        );
                        
                        if ("true".equals(confirmed)) {
                            // Get the selected coordinates
                            double lat = Double.parseDouble((String) webEngine.executeScript(
                                "document.getElementById('selectedLat').value"
                            ));
                            double lng = Double.parseDouble((String) webEngine.executeScript(
                                "document.getElementById('selectedLng').value"
                            ));
                            
                            // Reset the confirmation flag
                            webEngine.executeScript(
                                "document.getElementById('locationConfirmed').value = 'false';"
                            );
                            
                            // Update the stored coordinates
                            latitude = lat;
                            longitude = lng;
                            
                            System.out.println("Location confirmed: " + lat + ", " + lng);
                            
                            // Call the callback function
                            if (locationCallback != null) {
                                locationCallback.accept(lat, lng);
                            }
                            
                            // Close the dialog
                            if (closeButton != null && closeButton.getScene() != null) {
                                Stage stage = (Stage) closeButton.getScene().getWindow();
                                stage.close();
                            }
                            
                            // Stop polling
                            pollTimer.cancel();
                        }
                    } catch (Exception e) {
                        System.err.println("Error polling for location confirmation: " + e.getMessage());
                    }
                });
            }
        }, 1000, 1000); // Check every 1000ms instead of 500ms for better performance
    }
    
    /**
     * Loads the directions map to show route to a restaurant
     * @param lat Restaurant latitude
     * @param lng Restaurant longitude
     * @param restaurantName Restaurant name
     * @param restaurantAddress Restaurant address
     */
    public void loadDirectionsMap(double lat, double lng, String restaurantName, String restaurantAddress) {
        System.out.println("Loading directions map for restaurant: " + restaurantName);
        System.out.println("Restaurant coordinates: " + lat + ", " + lng);
        
        try {
            // Load the HTML file
            URL url = getClass().getResource("/maps/directions-map.html");
            webEngine.load(url.toExternalForm());
            
            // Wait for the page to load before setting the coordinates
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println("Directions map loaded successfully");
                    
                    // Set the restaurant information in JavaScript
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("restaurantLat", lat);
                    window.setMember("restaurantLng", lng);
                    window.setMember("restaurantName", restaurantName);
                    window.setMember("restaurantAddress", restaurantAddress);
                    
                    System.out.println("Restaurant information set in JavaScript");
                    
                    // Force map to redraw after a short delay
                    Platform.runLater(() -> {
                        try {
                            // First invalidation
                            webEngine.executeScript("if (map) { map.invalidateSize(); }");
                            
                            // Schedule multiple invalidations to ensure tiles load properly
                            scheduleMapRefresh();
                            
                            // Add event listener for map move and zoom events
                            webEngine.executeScript(
                                "if (map) {" +
                                "  map.on('moveend', function() { map.invalidateSize(); });" +
                                "  map.on('zoomend', function() { map.invalidateSize(); });" +
                                "}"
                            );
                        } catch (Exception e) {
                            System.err.println("Error refreshing map: " + e.getMessage());
                        }
                    });
                    
                    // Add console logging to debug JavaScript issues
                    webEngine.executeScript(
                        "console.log = function(message) { " +
                        "   if (window.java_console_log) { " +
                        "       window.java_console_log(message); " +
                        "   } else { " +
                        "       print(message); " +
                        "   } " +
                        "};"
                    );
                    
                    // Set up JavaScript alert handler
                    window.setMember("java_alert", new JavaScriptAlert());
                }
            });
            
            // Add error handler
            webEngine.getLoadWorker().exceptionProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    System.err.println("Error loading directions map: " + newValue.getMessage());
                    newValue.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error loading directions map: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void scheduleMapRefresh() {
        Platform.runLater(() -> {
            try {
                Thread.sleep(200);
                webEngine.executeScript("if (map) { map.invalidateSize(); }");
                
                Thread.sleep(400);
                webEngine.executeScript("if (map) { map.invalidateSize(); }");
            } catch (InterruptedException e) {
                System.err.println("Error refreshing map: " + e.getMessage());
            }
        });
    }
    
    public class JavaScriptAlert {
        public void alert(String message) {
            System.out.println("JavaScript alert: " + message);
        }
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (pollTimer != null) {
            pollTimer.cancel();
        }
        super.finalize();
    }
}
