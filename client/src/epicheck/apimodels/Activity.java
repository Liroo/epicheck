package epicheck.apimodels;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Kevin on 17/10/2016.
 */
public class Activity extends RecursiveTreeObject<Activity> {
    private final SimpleStringProperty title;
    private final SimpleStringProperty module;
    private final SimpleStringProperty beginDate;
    private final SimpleStringProperty endDate;


    public Activity(String title, String module, String beginDate, String endDate) {
        this.title = new SimpleStringProperty(title);
        this.module = new SimpleStringProperty(module);
        this.beginDate = new SimpleStringProperty(beginDate);
        this.endDate = new SimpleStringProperty(endDate);
    }

    public String getEndDate() {
        return endDate.get();
    }

    public SimpleStringProperty endDateProperty() {
        return endDate;
    }

    public String getBeginDate() {
        return beginDate.get();
    }

    public SimpleStringProperty beginDateProperty() {
        return beginDate;
    }

    public String getModule() {
        return module.get();
    }

    public SimpleStringProperty moduleProperty() {
        return module;
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }
}
