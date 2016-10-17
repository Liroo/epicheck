package epicheck.utils.nfc;

import com.mb3364.http.AsyncHttpClient;
import com.mb3364.http.HttpClient;
import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;
import epicheck.utils.ApiUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.nfctools.api.ApduTag;
import org.nfctools.api.NfcTagListener;
import org.nfctools.api.Tag;
import org.nfctools.api.TagType;
import org.nfctools.mf.ul.*;
import org.nfctools.spi.acs.AcrMfUlReaderWriter;
import org.nfctools.utils.NfcUtils;

import java.util.List;
import java.util.Map;

import static epicheck.utils.ApiUtils.RequestType.POST;

public class TagListener implements NfcTagListener {

    public TagListener() {
    }

    @Override
    public boolean canHandle(Tag tag) {
        return tag.getTagType().equals(TagType.MIFARE_ULTRALIGHT);
    }

    @Override
    public void handleTag(Tag tag) {
        MfUlReaderWriter readerWriter = new AcrMfUlReaderWriter((ApduTag)tag);
        try {
            RequestParams params = new RequestParams("id", NfcUtils.convertBinToASCII(readerWriter.getTagInfo().getId()) + "9000");
            ApiUtils.get().exec(POST, "http://localhost:3000/students/get", params, new HttpResponseHandler() {
                @Override
                public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                    try {
                        TagTask.get().callListener(new JSONObject(new String(bytes)));
                    } catch (JSONException e) {
                        TagTask.get().errorListener("Error while reading card");
                    }
                }

                @Override
                public void onFailure(int i, Map<String, List<String>> map, byte[] bytes) {
                    TagTask.get().errorListener(new String(bytes));
                }

                @Override
                public void onFailure(Throwable throwable) {
                    TagTask.get().errorListener("connection interrupted");
                }
            });
        }
        catch (Exception e) {
            TagTask.get().errorListener("Error while reading card");
        }
    }
}