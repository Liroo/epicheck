package epicheck.apimodels;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by jean on 18/10/16.
 */
public class Student extends RecursiveTreeObject<Student> {
    private StringProperty email;
    private StringProperty date;

    public Student(String email, String date) {
        this.email = new SimpleStringProperty(email);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty getDate() {
        return date;
    }

    public StringProperty getEmail() {
        return email;
    }
}
