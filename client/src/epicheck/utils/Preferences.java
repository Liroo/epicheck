package epicheck.utils;

import java.util.prefs.BackingStoreException;

/**
 * Created by jean on 16/10/16.
 */
public class Preferences {
    private static Preferences self = null;
    private java.util.prefs.Preferences prefs;

    private Preferences() {
        prefs = java.util.prefs.Preferences.userNodeForPackage(Preferences.class);
    }

    public static Preferences get() {
        if (self == null)
            self = new Preferences();
        return (self);
    }

    public String getAutoLogin() {
        return (prefs.get("autologin", ""));
    }

    public void setAutoLogin(String link) {
        try {
            prefs.put("autologin", link);
            prefs.flush();
        } catch (BackingStoreException e) {}
    }

    public String getEmailList() {
        return (prefs.get("emaillist", ""));
    }

    public void setEmailList(String list) {
        try {
            prefs.put("emaillist", list);
            prefs.flush();
        } catch (BackingStoreException e) {}
    }
}
