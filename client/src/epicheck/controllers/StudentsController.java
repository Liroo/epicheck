package epicheck.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kevin on 17/10/2016.
 */
public class StudentsController implements Initializable {

    @FXML
    private Circle circleClip;

    @FXML
    private ImageView profilePicture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            final Circle clip = new Circle(55D, 55D, 55);
            profilePicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/kevin.empociello.bmp"));
            profilePicture.setClip(clip);
        });

    }
}
