package epicheck.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.Main;
import epicheck.models.Activity;
import epicheck.utils.ApiRequest;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import static java.lang.System.in;
import static java.lang.System.load;

/**
 * Created by Kevin on 15/10/2016.
 */
public class ActivityController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXTreeTableView tableView;

    @FXML
    private JFXToggleButton pastActivity;

    @FXML
    private JFXButton btn_launch;

    private ObservableList<epicheck.apimodels.Activity> activities;
    private ObservableList<epicheck.apimodels.Activity> oldSessions;
    private ObservableList<epicheck.apimodels.Activity> searchActivities;

    private boolean oldSessions_cache;

    private JSONArray activities_json;
    private JSONArray old_activities_json;

    private JFXTreeTableColumn<epicheck.apimodels.Activity, String> name;
    private JFXTreeTableColumn<epicheck.apimodels.Activity, String> module;
    private JFXTreeTableColumn<epicheck.apimodels.Activity, String> beginDate;
    private JFXTreeTableColumn<epicheck.apimodels.Activity, String> endDate;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name = new JFXTreeTableColumn<>("Titre");
        module = new JFXTreeTableColumn<>("Module");
        beginDate = new JFXTreeTableColumn<>("Date début");
        endDate = new JFXTreeTableColumn<>("Date fin");

        name.setMinWidth(420);
        module.setMinWidth(320);
        beginDate.setMinWidth(218);
        endDate.setMinWidth(218);

        name.setCellValueFactory(param -> param.getValue().getValue().getActiTitle());
        module.setCellValueFactory(param -> param.getValue().getValue().getModuleTitle());
        beginDate.setCellValueFactory(param -> param.getValue().getValue().getDateFrom());
        endDate.setCellValueFactory(param -> param.getValue().getValue().getDateTo());

        activities = FXCollections.observableArrayList();
        searchActivities = FXCollections.observableArrayList();
        oldSessions = FXCollections.observableArrayList();
        oldSessions_cache = false;

        tableView.getColumns().setAll(name, module, beginDate, endDate);
        ApiRequest.get().getActivitiesFromIntra(getToday(), getToday(), new ApiRequest.JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                activities_json = res;
                Platform.runLater(() -> {
                    try {
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject obj = res.getJSONObject(i);
                            try {
                                activities.add(new epicheck.apimodels.Activity(obj.getString("acti_title"), obj.getString("titlemodule"), dateFormatIntra(obj.getString("start")), dateFormatIntra(obj.getString("end")),
                                        obj.getString("scolaryear"), obj.getString("codemodule"), obj.getString("codeinstance"), obj.getString("codeacti"), obj.getString("codeevent")));
                            } catch (Exception e) {}
                        }
                        final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
                        tableView.getColumns().setAll(name, module, beginDate, endDate);
                        tableView.setRoot(root);
                        tableView.setShowRoot(false);
                    } catch (Exception e) {
                        Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez recharger le calendrier", 3000));
                    }
                });
            }
            @Override
            public void onFailure(String err) {
                Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur de chargement des activités depuis l'intra. Vérifiez le lien d'autologin", 2000));
            }
        });
    }


    private void launchWindow(String scene_title, Parent root, AbstractSession controller)
    {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ActivityController.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setTitle(scene_title);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.primaryStage);
        stage.show();
        stage.setOnCloseRequest(we -> {
            Main.mainController.paramsController.refresh();
        });
    }

    private void newWindow(String scene_fxml, String scene_title, epicheck.apimodels.Activity activity, JSONArray students) throws IOException {
        FXMLLoader loader = new FXMLLoader(ActivityController.class.getResource(scene_fxml));
        Parent root = loader.load();
        AbstractSession controller = loader.getController();
        ApiRequest.get().addActivity(activity.getActiTitle().get(), activity.getDateFrom().get(), activity.getDateTo().get(), activity.getModuleTitle().get(), activity.getScholarYear().get(), activity.getCodeModule().get(), activity.getCodeInstance().get(), activity.getCodeActi().get(), activity.getCodeEvent().get(), students, new ApiRequest.JSONObjectListener() {
            @Override
            public void onComplete(JSONObject res) {
                ApiRequest.get().getActivity(activity.getCodeActi().get(), activity.getCodeEvent().get(), new ApiRequest.JSONObjectListener() {
                    @Override
                    public void onComplete(JSONObject res) {
                        Platform.runLater(() -> {
                            controller.setParams(activity, res);
                            launchWindow(scene_title, root, controller);
                        });
                    }

                    @Override
                    public void onFailure(String err) {
                        System.out.println("err = " + err);
                        Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez rééssayer", 3000));
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                try {
                    JSONObject res = new JSONObject(err);
                    if (res.getString("message").equals("activity already exist")) {
                        ApiRequest.get().getActivity(activity.getCodeActi().get(), activity.getCodeEvent().get(), new ApiRequest.JSONObjectListener() {
                            @Override
                            public void onComplete(JSONObject res) {
                                Platform.runLater(() -> {
                                    controller.setParams(activity, res);
                                    launchWindow(scene_title, root, controller);
                                });
                            }

                            @Override
                            public void onFailure(String err) {
                                Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez rééssayer", 3000));
                            }
                        });
                    }
                } catch (JSONException e) {
                    System.out.println("e = " + e);
                    Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez rééssayer", 3000));
                }
            }
        });
    }

    @FXML
    public void launchActivityScene() throws IOException {

        if (tableView.getSelectionModel().getSelectedIndex() == -1)
        {
            new JFXSnackbar(rootPane).show("Séléctionnez d'abord une activité", 5000);
            return ;
        }
        
        RecursiveTreeItem select = (RecursiveTreeItem) tableView.getSelectionModel().getSelectedItem();
        epicheck.apimodels.Activity activity = (epicheck.apimodels.Activity) select.getValue();

        activity.getRegisteredStudents(new ApiRequest.JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                Platform.runLater(() -> {
                    try {
                        if (pastActivity.isSelected())
                            newWindow("/epicheck/views/prev_session.fxml", "Aperçu de session", activity, res);
                        else
                            newWindow("/epicheck/views/session.fxml", "Session de validation", activity, res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = " + err);
                Platform.runLater(() -> new JFXSnackbar(rootPane).show(err, 3000));
            }
        });
    }

    public void refreshList() {
        activities.clear();
        ApiRequest.get().getActivitiesFromIntra(getToday(), getToday(), new ApiRequest.JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                activities_json = res;
                Platform.runLater(() -> {
                    try {
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject obj = res.getJSONObject(i);
                            try {
                                activities.add(new epicheck.apimodels.Activity(obj.getString("acti_title"), obj.getString("titlemodule"), dateFormatIntra(obj.getString("start")), dateFormatIntra(obj.getString("end")),
                                        obj.getString("scolaryear"), obj.getString("codemodule"), obj.getString("codeinstance"), obj.getString("codeacti"), obj.getString("codeevent")));
                            } catch (Exception e) {
                                System.out.println("e = " + e);
                            }
                        }
                        final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
                        tableView.getColumns().setAll(name, module, beginDate, endDate);
                        tableView.setRoot(root);
                        tableView.setShowRoot(false);
                        Platform.runLater(() -> new JFXSnackbar(rootPane).show("Rafraîchissement terminé", 3000));
                    } catch (Exception e) {
                        Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez recharger le calendrier", 3000));
                    }
                });
            }
            @Override
            public void onFailure(String err) {
                Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur de chargement des activités depuis l'intra. Vérifiez le lien d'autologin", 2000));
            }
        });
    }

    public void searchFilter() throws JSONException {
        searchActivities.clear();

        if (searchField.getText().length() == 0 && pastActivity.isSelected() == false) {
            final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
            Platform.runLater(() -> tableView.setRoot(root));
            return ;
        }
        JSONArray arr;
        if (pastActivity.isSelected()) {
            arr = old_activities_json;
        } else {
            arr = activities_json;
        }
        for (int i = 0; i < arr.length(); i++)
        {
            JSONObject obj;
            obj = arr.getJSONObject(i);
            if (pastActivity.isSelected()) {
                if (obj.getString("actiTitle").toLowerCase().contains(searchField.getText().toLowerCase()))
                    searchActivities.add(new epicheck.apimodels.Activity(obj.getString("actiTitle"), obj.getString("moduleTitle"), dateFormatAPI(obj.getString("dateFrom")), dateFormatAPI(obj.getString("dateTo")),
                            obj.getString("scholarYear"), obj.getString("codeModule"), obj.getString("codeInstance"), obj.getString("codeActi"), obj.getString("codeEvent")));
            } else {
                if (obj.getString("acti_title").toLowerCase().contains(searchField.getText().toLowerCase()))
                    searchActivities.add(new epicheck.apimodels.Activity(obj.getString("acti_title"), obj.getString("titlemodule"), dateFormatIntra(obj.getString("start")), dateFormatIntra(obj.getString("end")),
                            obj.getString("scolaryear"), obj.getString("codemodule"), obj.getString("codeinstance"), obj.getString("codeacti"), obj.getString("codeevent")));
            }
        }
        final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(searchActivities, RecursiveTreeObject::getChildren);
        Platform.runLater(() -> tableView.setRoot(root));
    }

    public void changeActivityState() {
        searchField.setText("");
        btn_launch.setText(pastActivity.isSelected() ? "Aperçu" : "Lancer session");
        if (pastActivity.isSelected()) {
            if (oldSessions_cache) {
                final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(oldSessions, RecursiveTreeObject::getChildren);
                Platform.runLater(() -> tableView.setRoot(root));
                return;
            }
            ApiRequest.get().getActivities(new ApiRequest.JSONArrayListener() {
                @Override
                public void onComplete(JSONArray res) {
                    Platform.runLater(() -> {
                        try {
                            old_activities_json = res;
                            for (int i = 0; i < res.length(); i++) {
                                JSONObject obj = res.getJSONObject(i);
                                oldSessions.add(new epicheck.apimodels.Activity(obj.getString("actiTitle"), obj.getString("moduleTitle"), dateFormatAPI(obj.getString("dateFrom")), dateFormatAPI(obj.getString("dateTo")),
                                        obj.getString("scholarYear"), obj.getString("codeModule"), obj.getString("codeInstance"), obj.getString("codeActi"), obj.getString("codeEvent")));
                            }
                            final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(oldSessions, RecursiveTreeObject::getChildren);
                            tableView.getColumns().setAll(name, module, beginDate, endDate);
                            tableView.setRoot(root);
                            tableView.setShowRoot(false);
                            oldSessions_cache = true;
                        } catch (Exception e) {
                            System.out.println(e.toString());
                            Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez recharger le calendrier", 3000));
                            pastActivity.setSelected(false);
                        }
                    });
                }

                @Override
                public void onFailure(String err) {
                }

            });
        } else {
            final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
            Platform.runLater(() -> tableView.setRoot(root));
        }
    }

    private String dateFormatAPI(String date) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat extern = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date_ret = format.parse(date);
            return (extern.format(date_ret));
        } catch (ParseException e) {
            return ("");
        }
    }

    private String dateFormatIntra(String date) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat extern = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date_ret = format.parse(date);
            return (extern.format(date_ret));
        } catch (ParseException e) {
            return ("");
        }
    }


    public String getToday() {
        Date date = new Date(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String today = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);

        return today;
    }
}
