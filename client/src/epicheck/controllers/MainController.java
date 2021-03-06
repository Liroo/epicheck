package epicheck.controllers;

/**
 * Created by Kevin on 08/10/2016.
 */

import com.jfoenix.controls.JFXTabPane;
import epicheck.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private JFXTabPane tabPane;

    public ActivityController activityController;
    public StudentsController studentsController;
    public ParamsController paramsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tab examTab = new Tab();
        Tab studentTab = new Tab();
        Tab paramsTab = new Tab();

        Pane activityPane = new Pane();
        Pane studentsPane = new Pane();
        Pane paramsPane = new Pane();

        try {
            FXMLLoader activityLoader = new FXMLLoader(getClass().getResource("/epicheck/views/activity.fxml"));
            FXMLLoader studentsLoader = new FXMLLoader(getClass().getResource("/epicheck/views/students.fxml"));
            FXMLLoader paramsLoader = new FXMLLoader(getClass().getResource("/epicheck/views/params.fxml"));

            activityPane = (Pane) activityLoader.load();
            studentsPane = (Pane) studentsLoader.load();
            paramsPane = (Pane) paramsLoader.load();

            activityController = activityLoader.getController();
            studentsController = studentsLoader.getController();
            paramsController = paramsLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        examTab.setText("Activités");
        examTab.setContent(activityPane);
        studentTab.setText("Étudiants");
        studentTab.setContent(studentsPane);

        paramsTab.setText("Paramètres");
        paramsTab.setContent(paramsPane);

        tabPane.getTabs().add(examTab);
        tabPane.getTabs().add(studentTab);
        tabPane.getTabs().add(paramsTab);
    }

}