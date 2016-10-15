package epicheck;

import java.io.IOException;
import javax.smartcardio.CardTerminal;

import org.nfctools.NfcAdapter;
import org.nfctools.api.TagScannerListener;
import org.nfctools.scio.TerminalMode;
import org.nfctools.spi.acs.AcsTerminal;
import org.nfctools.utils.CardTerminalUtils;

public class Acr122Device extends AcsTerminal implements TagScannerListener {

    private NfcAdapter adapter;

    public Acr122Device() {
        CardTerminal terminal = CardTerminalUtils.getTerminalByName("ACR122");
        setCardTerminal(terminal);
        adapter = new NfcAdapter(this, TerminalMode.INITIATOR, this);
        adapter.registerTagListener(new TagListener());
    }

    @Override
    public void open() throws IOException {
        System.out.println("Opening device");
        super.open();
        listen();
    }

    public void listen() {
        System.out.println("Listening for cards...");
        adapter.startListening();
    }

    public void stop() {
        adapter.stopListening();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing device");
        stop();
        super.close();
    }

    @Override
    public void onScanningEnded() {
    }

    @Override
    public void onScanningFailed(Throwable throwable) {
        //throwable.printStackTrace();
    }

    @Override
    public void onTagHandingFailed(Throwable throwable) {
        //throwable.printStackTrace();
    }
}