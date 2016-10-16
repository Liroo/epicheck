package epicheck.utils.nfc;

import org.nfctools.api.ApduTag;
import org.nfctools.api.NfcTagListener;
import org.nfctools.api.Tag;
import org.nfctools.api.TagType;
import org.nfctools.mf.ul.*;
import org.nfctools.spi.acs.AcrMfUlReaderWriter;
import org.nfctools.utils.NfcUtils;

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
            TagTask.get().callListener(NfcUtils.convertBinToASCII(readerWriter.getTagInfo().getId()));
        }
        catch (Exception e) {
            TagTask.get().errorListener("Error while reading card");
        }
    }
}