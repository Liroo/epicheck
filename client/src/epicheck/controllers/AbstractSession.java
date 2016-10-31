package epicheck.controllers;

import epicheck.apimodels.Activity;
import org.json.JSONObject;

/**
 * Created by jean on 10/26/16.
 */
public abstract class AbstractSession {
    public abstract void setParams(Activity acti, JSONObject res);
}
