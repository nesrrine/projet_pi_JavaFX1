package controllers.User;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GainController {

    @FXML
    private Label timerLabel;

    @FXML
    private Label resultLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    private AnimationTimer timer;
    private long startTime;
    private long elapsedMillis;

    @FXML
    public void initialize() {
        stopButton.setDisable(true);
        timerLabel.setText("00.000");
        resultLabel.setText("Essayez d'arrêter exactement à 10.000 secondes pour gagner 40% de réduction !");
    }

    @FXML
    public void startTimer() {
        resultLabel.setText("");
        stopButton.setDisable(false);
        startButton.setDisable(true);

        startTime = System.nanoTime();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                elapsedMillis = (now - startTime) / 1_000_000; // convert to milliseconds
                long seconds = elapsedMillis / 1000;
                long millis = elapsedMillis % 1000;
                timerLabel.setText(String.format("%02d.%03d", seconds, millis));
            }
        };

        timer.start();
    }

    @FXML
    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }

        stopButton.setDisable(true);
        startButton.setDisable(false);

        long diff = Math.abs(elapsedMillis - 10000); // distance from 10 sec

        if (diff <= 50) { // +/- 50 ms tolerance
            resultLabel.setText("🔥 Bravo ! Vous avez arrêté à 10.000s ! Vous gagnez une réduction de 40% sur votre prochaine réservation !");
        } else {
            resultLabel.setText(String.format("⏱ Vous avez arrêté à %.3f secondes. Essayez d’arrêter à 10.000s pour gagner 40%% de réduction !", elapsedMillis / 1000.0));
        }
    }
}
