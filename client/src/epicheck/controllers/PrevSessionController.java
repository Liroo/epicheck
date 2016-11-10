package epicheck.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.apimodels.Activity;
import epicheck.apimodels.Student;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.codehaus.plexus.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Created by jean on 10/26/16.
 */
public class PrevSessionController extends AbstractSession implements Initializable {
    private JSONObject activity_json;
    private Stage stage;

    @FXML
    AnchorPane root;

    @FXML
    JFXButton btn_export;

    @FXML
    private JFXTreeTableView table;

    private JSONArray studs;
    private ObservableList<Student> students;


    @FXML
    private void export() throws IOException, JSONException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportation de la liste des inscrits à : " + activity_json.getString("actiTitle"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fileChooser.setInitialFileName(activity_json.getString("actiTitle"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            if (!FileUtils.getExtension(file.getName()).equalsIgnoreCase("csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
            openFile(file);
        }
    }

    private void openFile(File file) {
        try {
            String data = "Email,Date\r\n";
            for (int i = 0; i < students.size(); i++)
                data += students.get(i).getEmail().get() + "," + students.get(i).getExportDate().get() + "\r\n";
            Files.write(Paths.get(file.getAbsolutePath()), data.getBytes());
            Platform.runLater(() -> new JFXSnackbar(root).show("Exportation terminée", 2000));
        } catch (IOException ex) {
            Platform.runLater(() -> new JFXSnackbar(root).show("Une erreur s'est produite lors de l'exportation", 2000));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setParams(Activity acti, JSONObject res) {
        this.activity_json = res;

        JFXTreeTableColumn<Student, String> email = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<Student, String> check = new JFXTreeTableColumn<>("Validation");


        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());

//        email.setCellFactory(param -> {
//            return new TableCell<Student, Student>() {
//                @Override
//                protected void updateItem(Student item, boolean empty) {
//                    super.updateItem(item, empty);
//                    if (item == null || empty) {
//                        setText(null);
//                        setStyle("");
//                    } else {
//                        // Format date.
//                        setText(item.getEmail().toString());
//                        // Style all dates in March with a different color.
//                        if (item.getForce()) {
//                            setStyle("-fx-background-color: red");
//                        } else {
//                            setStyle("");
//                        }
//                    }
//                }
//            };
//        });
        check.setCellValueFactory(param -> param.getValue().getValue().getDate());

        table.setRowFactory(new Callback<TreeTableView, TreeTableRow>() {
            @Override
            public TreeTableRow call(TreeTableView param) {
                final TreeTableRow<Student> row = new TreeTableRow<Student>() {
                    @Override
                    protected void updateItem(Student person, boolean empty){
                        super.updateItem(person, empty);
                        System.out.println("hello");
                        if (person != null && person.getDate().get().equals("17:56")) {
                            setStyle("-fx-background-color: #DCDCDC;");
                        }
                    }
                };
                return row;
            }
        });

        System.out.println("stop");

        email.setEditable(true);
        check.setEditable(true);

        email.setMaxWidth(260);
        check.setMinWidth(200);

        students = FXCollections.observableArrayList();

        try {
            studs = activity_json.getJSONArray("students");

            for(int i = 0; i < studs.length(); i++) {
                JSONObject stud = studs.getJSONObject(i);
                students.add(new Student(stud.getString("email"), stud.getJSONObject("presence").getString("date"), stud.getJSONObject("presence").getBoolean("present"), stud.getJSONObject("presence").getBoolean("force")));
            }

            Platform.runLater(() -> {
                TreeItem<Student> root = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
                table.getColumns().setAll(email, check);
                table.setRoot(root);
                table.setShowRoot(false);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
