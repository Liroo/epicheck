package epicheck.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.models.Activity;
import epicheck.utils.ApiRequest;
import javafx.application.Platform;
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
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.System.in;

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

        name.setMinWidth(400);
        module.setMinWidth(330);
        beginDate.setMinWidth(190);
        endDate.setMinWidth(190);
        name.setStyle("-fx-alignment: CENTER;");
        module.setStyle("-fx-alignment: CENTER;");
        beginDate.setStyle("-fx-alignment: CENTER;");
        endDate.setStyle("-fx-alignment: CENTER;");


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

        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
        ApiRequest.get().getActivitiesFromIntra("2016-10-18", "2016-10-18", new ApiRequest.JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                System.out.println("res = " + res);
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        try {
                            for (int i = 0; i < res.length(); i++) {
                                JSONObject obj = res.getJSONObject(i);
                                System.out.println("obj = " + obj);
                                activities.add(new epicheck.apimodels.Activity(obj.getString("acti_title"), obj.getString("titlemodule"), obj.getString("start"), obj.getString("end")));
                            }
                            final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<epicheck.apimodels.Activity>(activities, RecursiveTreeObject::getChildren);
                            tableView.getColumns().setAll(name, module, beginDate, endDate);
                            tableView.setRoot(root);
                            tableView.setShowRoot(false);
                        } catch (Exception e) {
                            System.out.println("Erreur");
                        }
                    }
                });
            }
            @Override
            public void onFailure(String err) {
                System.out.println("err = [" + err + "]");
            }
        });

    }

    @FXML
    public void searchFilter(Event event) {
        System.out.println(searchField.getText().toString());
    }

}
