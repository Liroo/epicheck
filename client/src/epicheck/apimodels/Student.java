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
public class Student extends RecursiveTreeObject<Student> implements Cloneable {
    private StringProperty email;
    private StringProperty date;
    private StringProperty title;
    private StringProperty pictureUrl;
    private StringProperty studentYear;

    public Student(String email, String date) {
        this.email = new SimpleStringProperty(email);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty getDate() {
        if (date.get().equals("null"))
            return (new SimpleStringProperty(""));
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat extern = new SimpleDateFormat("HH:mm");
            Date date_ret = format.parse(date.get());
            return (new SimpleStringProperty(extern.format(date_ret)));
        } catch (ParseException e) {
            return date;
        }
    }

    public StringProperty getExportDate() {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            DateFormat extern = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date_ret = format.parse(date.get());
            return (new SimpleStringProperty(extern.format(date_ret)));
        } catch (ParseException e) {
            return date;
        }
    }


    public void setTitle(String title) {
        this.title = new SimpleStringProperty(title);
    }

    public void setPictureUrl(String title) {
        this.pictureUrl = new SimpleStringProperty(title);
    }

    public void setStudentYear(String year) {
        this.studentYear = new SimpleStringProperty(year);
    }

    public StringProperty getTitle() {
        return this.title;
    }

    public StringProperty getStudentYear() {
        return this.studentYear;
    }

    public StringProperty getPictureUrl() {
        return this.pictureUrl;
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

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
