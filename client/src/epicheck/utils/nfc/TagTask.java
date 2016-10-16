package epicheck.utils.nfc;

public class TagTask {
    public interface TagListener {
        void scanCard(String tag);
        void scanError(String error);
    }
    private static TagTask self = null;
    private static TagListener _listener;

    private TagTask() {
        _listener = new TagListener() {
            @Override
            public void scanCard(String tag) {}

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

    public void callListener(String tag) {
        _listener.scanCard(tag);
    }

    public void errorListener(String error) { _listener.scanError(error); }
}
