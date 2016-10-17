package epicheck.utils.nfc;

import com.mb3364.http.AsyncHttpClient;
import com.mb3364.http.HttpClient;
import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;
import epicheck.utils.ApiRequest;
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
            ApiRequest.get().getStudent(NfcUtils.convertBinToASCII(readerWriter.getTagInfo().getId()) + "9000", new ApiRequest.JSONObjectListener() {
                @Override
                public void onComplete(JSONObject res) {
                    TagTask.get().callListener(res);
                }

                @Override
                public void onFailure(String err) {
                    TagTask.get().errorListener(err);
                }
            });
        }
        catch (Exception e) {
            TagTask.get().errorListener("Error while reading card");
        }
    }
}