package epicheck.utils.nfc;

import org.json.JSONArray;
import org.json.JSONObject;

public class TagTask {
    public interface TagListener {
        void scanCard(JSONObject student);
        void scanError(String error);
    }
    private static TagTask self = null;
    private static TagListener _listener;

    private TagTask() {
        _listener = new TagListener() {
            @Override
            public void scanCard(JSONObject res) {}

            @Override
            public void scanError(String error) {}
        };
    }

    public static TagTask get() {
        if (self == null)
            self = new TagTask();
        return (self);
    }

    public void setListener(TagListener listener) {
        _listener = listener;
    }

    public void callListener(JSONObject tag) {
        _listener.scanCard(tag);
    }

    public void errorListener(String error) { _listener.scanError(error); }
}
