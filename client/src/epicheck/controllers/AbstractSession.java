package epicheck.controllers;

import epicheck.apimodels.Activity;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jean on 10/26/16.
 */
public abstract class AbstractSession {
    public abstract void setParams(Activity acti, JSONObject res);
    public abstract void setStage(Stage stage);
}
