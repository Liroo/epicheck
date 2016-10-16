package epicheck;

/**
 * Created by Kevin on 08/10/2016.
 */

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private JFXTabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tab examTab = new Tab();
        Tab studentTab = new Tab();
        Tab paramsTab = new Tab();

        Pane activityPane = new Pane();
        Pane studentsPane = new Pane();
        Pane paramsPane = new Pane();

        try {
            activityPane = FXMLLoader.load(getClass().getResource("activity.fxml"));
            studentsPane = FXMLLoader.load(getClass().getResource("students.fxml"));
            paramsPane = FXMLLoader.load(getClass().getResource("params.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        examTab.setText("Activités");
        examTab.setContent(activityPane);
        /* list examens (name, debut, fin) (pour activité - année - module - ville semestre 1? - acti ID - event ID)
           ajouter examen / editer examen / supprimer examen (pop up)
           lancer la session (nouvelle page) - connecter rfid (si pas deja fait) - last photo + name (en dessous tableview all) - finir session
        */

        studentTab.setText("Étudiants");
        studentTab.setContent(studentsPane);
        /* list students (mail, rfid, ionisID)
         */

        paramsTab.setText("Paramètres");
        paramsTab.setContent(paramsPane);
        /*
            Connecter le lecteur / deconnecter
            Autologin (editText)
         */

        tabPane.getTabs().add(examTab);
        tabPane.getTabs().add(studentTab);
        tabPane.getTabs().add(paramsTab);
    }
}
