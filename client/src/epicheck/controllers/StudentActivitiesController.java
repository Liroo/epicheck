package epicheck.controllers;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.apimodels.Activity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kevin on 30/10/2016.
 */
public class StudentActivitiesController implements Initializable {

    @FXML
    private JFXTreeTableView tableView;

    private ObservableList<Activity> activities;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Welcome in student's activities");

        JFXTreeTableColumn<Activity, String> name = new JFXTreeTableColumn<>("Activité");
        JFXTreeTableColumn<epicheck.apimodels.Activity, String> presence = new JFXTreeTableColumn<>("Présence");

        name.setMinWidth(420);
        presence.setMinWidth(420);
        name.setMaxWidth(420);
        presence.setMaxWidth(420);

        name.setCellValueFactory(param -> param.getValue().getValue().getActiTitle());
        //presence.setCellValueFactory(param -> param.getValue().getValue());

        activities = FXCollections.observableArrayList();

        final TreeItem<Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
        tableView.getColumns().setAll(name, presence);
        tableView.setRoot(root);
        tableView.setShowRoot(false);
    }
}
