package epicheck.apimodels;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat extern = new SimpleDateFormat("HH:mm:ss");
            Date date_ret = format.parse(date.get());
            return (new SimpleStringProperty(extern.format(date_ret)));
        } catch (ParseException e) {
            return date;
        }
    }

    public void setDate() {
        Calendar now = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        now.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        date = new SimpleStringProperty(format.format(now.getTime()));
    }

    public void setDate(String date) {
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty getEmail() {
        return email;
    }
}
