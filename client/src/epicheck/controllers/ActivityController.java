package epicheck.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import epicheck.models.Activity;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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
        JFXTreeTableColumn name = new JFXTreeTableColumn("Titre");
        JFXTreeTableColumn module = new JFXTreeTableColumn("Module");
        JFXTreeTableColumn beginDate = new JFXTreeTableColumn("Date début");
        JFXTreeTableColumn endDate = new JFXTreeTableColumn("Date fin");

        name.setMinWidth(300);
        module.setMinWidth(220);
        beginDate.setMinWidth(190);
        endDate.setMinWidth(190);

        tableView.getColumns().addAll(name, module, beginDate, endDate);

        System.out.println("hello");

        Activity.getAllActivities();
    }

    @FXML
    public void searchFilter(Event event) {
        System.out.println(searchField.getText().toString());
    }

}
