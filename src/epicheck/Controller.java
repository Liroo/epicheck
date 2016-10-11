package epicheck;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {
    @FXML
    private Button connect;

    @FXML
    private Label txt;

    @FXML
    private void connectClick() throws Exception {
        if (epicheck.Main.isConnected())
        {
            txt.setText("Device disconnected");
            epicheck.Main.disconnect();
            connect.setText("Connect");
        } else {
            if (!epicheck.Main.connect())
                txt.setText("No device found");
            else {
                txt.setText("Device connected");
                connect.setText("Disconnect");
            }
        }
    }

}
