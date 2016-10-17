package epicheck.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.models.Activity;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kevin on 15/10/2016.
 */
public class ActivityController implements Initializable {

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXTreeTableView tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXTreeTableColumn<epicheck.apimodels.Activity, String> name = new JFXTreeTableColumn<>("Titre");
        JFXTreeTableColumn<epicheck.apimodels.Activity, String> module = new JFXTreeTableColumn<>("Module");
        JFXTreeTableColumn<epicheck.apimodels.Activity, String> beginDate = new JFXTreeTableColumn<>("Date début");
        JFXTreeTableColumn<epicheck.apimodels.Activity, String> endDate = new JFXTreeTableColumn<>("Date fin");

        name.setMinWidth(300);
        module.setMinWidth(220);
        beginDate.setMinWidth(190);
        endDate.setMinWidth(190);

        name.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String> param) {
                return param.getValue().getValue().titleProperty();
            }
        });

        module.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String> param) {
                return param.getValue().getValue().moduleProperty();
            }
        });

        beginDate.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String> param) {
                return param.getValue().getValue().beginDateProperty();
            }
        });

        endDate.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<epicheck.apimodels.Activity, String> param) {
                return param.getValue().getValue().endDateProperty();
            }
        });


        ObservableList<epicheck.apimodels.Activity> activities = FXCollections.observableArrayList();
        activities.add(new epicheck.apimodels.Activity("Exam Reseau", "B5 - Reseaux", "19/09/2016 16H00", "19/09/2016 19H00"));
        activities.add(new epicheck.apimodels.Activity("KickOff 303", "B5 - Mathématiques", "19/09/2016 09H00", "19/09/2016 10H00"));
        activities.add(new epicheck.apimodels.Activity("Bootstrap 303", "B5 - Mathématiques", "19/09/2016 10H00", "19/09/2016 11H00"));


        final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<epicheck.apimodels.Activity>(activities, RecursiveTreeObject::getChildren);
        tableView.getColumns().setAll(name, module, beginDate, endDate);
        tableView.setRoot(root);
        tableView.setShowRoot(false);

        System.out.println("hello");


        Activity.getAllActivities();
    }

    @FXML
    public void searchFilter(Event event) {
        System.out.println(searchField.getText().toString());
    }

}
