package epicheck.apimodels;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mb3364.http.HttpResponseHandler;
import epicheck.utils.ApiRequest;
import epicheck.utils.ApiUtils;
import epicheck.utils.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 17/10/2016.
 */
public class Activity extends RecursiveTreeObject<Activity> {
    private StringProperty actiTitle;
    private StringProperty moduleTitle;
    private StringProperty dateFrom;
    private StringProperty dateTo;
    private StringProperty scholarYear;
    private StringProperty codeModule;
    private StringProperty codeInstance;
    private StringProperty codeActi;
    private StringProperty codeEvent;

    public Activity(String title, String module, String beginDate, String endDate) {
        this.actiTitle = new SimpleStringProperty(title);
        this.moduleTitle = new SimpleStringProperty(module);
        this.dateFrom = new SimpleStringProperty(beginDate);
        this.dateTo = new SimpleStringProperty(endDate);
    }

    public Activity(String actiTitle, String moduleTitle, String dateFrom, String dateTo, String scholarYear, String codeModule, String codeInstance, String codeActi, String codeEvent) {
        this.actiTitle = new SimpleStringProperty(actiTitle);
        this.moduleTitle = new SimpleStringProperty(moduleTitle);
        this.dateFrom = new SimpleStringProperty(dateFrom);
        this.dateTo = new SimpleStringProperty(dateTo);
        this.scholarYear = new SimpleStringProperty(scholarYear);
        this.codeModule = new SimpleStringProperty(codeModule);
        this.codeInstance = new SimpleStringProperty(codeInstance);
        this.codeActi = new SimpleStringProperty(codeActi);
        this.codeEvent = new SimpleStringProperty(codeEvent);
    }


    public StringProperty getActiTitle() {
        return actiTitle;
    }

    public StringProperty getCodeActi() {
        return codeActi;
    }

    public StringProperty getCodeEvent() {
        return codeEvent;
    }

    public StringProperty getCodeInstance() {
        return codeInstance;
    }

    public StringProperty getCodeModule() {
        return codeModule;
    }

    public StringProperty getDateFrom() {
        return dateFrom;
    }

    public StringProperty getDateTo() {
        return dateTo;
    }

    public StringProperty getModuleTitle() {
        return moduleTitle;
    }

    public StringProperty getScholarYear() {
        return scholarYear;
    }

    public void getRegisteredStudents(ApiRequest.JSONArrayListener call) {
        String URL = "https://intra.epitech.eu/" + Preferences.get().getAutoLogin() + "/module/" + scholarYear.get() + "/" + codeModule.get() + "/" + codeInstance.get() + "/" + codeActi.get() + "/" + codeEvent.get() + "/registered?format=json";
        ApiUtils.get().exec(ApiUtils.RequestType.GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    call.onComplete(new JSONArray(new String(bytes)));
                } catch (JSONException e) {
                    call.onFailure("Aucun étudiant inscrit à cette activité");
                }
            }

            @Override
            public void onFailure(int i, Map<String, List<String>> map, byte[] bytes) {
                call.onFailure(new String(bytes));
            }

            @Override
            public void onFailure(Throwable throwable) {
                call.onFailure("connection interrupted");
            }
        });
    }

    @Override
    public String toString() {
        return actiTitle.get() + " from " + moduleTitle.get() + " with codes : " + codeModule.get() + "/" + codeInstance.get() + "/" + codeActi.get() + "/" + codeEvent.get();
    }
}
