package epicheck;

import org.nfctools.ndef.NdefListener;
import org.nfctools.ndef.NdefOperations;
import org.nfctools.ndef.NdefOperationsListener;
import org.nfctools.ndef.Record;
import org.nfctools.snep.Sneplet;
import org.nfctools.utils.NfcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by jean on 11/10/16.
 */
public class LoggerListener implements NdefListener, NdefOperationsListener, Sneplet {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onNdefMessages(Collection<Record> records) {
        //for (Record record : records) {
        //    log.info(record.toString());
        //}
    }

    @Override
    public void onNdefOperations(NdefOperations ndefOperations) {
        log.info("Tag ID: " + NfcUtils.convertBinToASCII(ndefOperations.getTagInfo().getId()));
        if (ndefOperations.isFormatted()) {
            if (ndefOperations.hasNdefMessage())
                onNdefMessages(ndefOperations.readNdefMessage());
            else
                log.info("Empty formatted tag. Size: " + ndefOperations.getMaxSize() + " bytes");
        }
        else
            log.info("Empty tag. NOT formatted. Size: " + ndefOperations.getMaxSize() + " bytes");
    }

    @Override
    public Collection<Record> doGet(Collection<Record> requestRecords) {
        log.info("SNEP get");
        onNdefMessages(requestRecords);
        return null;
    }

    @Override
    public void doPut(Collection<Record> requestRecords) {
        log.info("SNEP put");
        onNdefMessages(requestRecords);
    }
}
