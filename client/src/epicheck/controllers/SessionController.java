package epicheck.controllers;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.apimodels.Activity;
import epicheck.apimodels.Student;
import epicheck.utils.ApiRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jean on 18/10/16.
 */
public class SessionController implements Initializable {
    private Activity activity;
    private JSONObject activity_json;

    @FXML
    private JFXTreeTableView table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setParams(Activity activity, JSONObject activity_json) {
        this.activity = activity;
        this.activity_json = activity_json;

        JFXTreeTableColumn<Student, String> email = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<Student, String> check = new JFXTreeTableColumn<>("Validation");

        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());
        check.setCellValueFactory(param -> param.getValue().getValue().getDate());

        ObservableList<Student> students = FXCollections.observableArrayList();

        System.out.println("activity_json = " + activity_json);

        try {
            JSONArray studs = activity_json.getJSONArray("students");

            System.out.println("studs = " + studs);

            for(int i = 0; i < studs.length(); i++) {
                JSONObject stud = studs.getJSONObject(i);
                students.add(new Student(stud.getString("email"), stud.getJSONObject("presence").getString("date")));
            }

            Platform.runLater(() -> {
                TreeItem<Student> root = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
                table.getColumns().setAll(email, check);
                table.setRoot(root);
                table.setShowRoot(false);
            });


        } catch (JSONException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        System.out.println("finish");


    }

    public void test() {
        System.out.println("activity_json = " + activity_json);
    }

}
