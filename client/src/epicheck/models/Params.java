package epicheck.models;

import epicheck.utils.nfc.Acr122Device;

import java.io.IOException;

/**
 * Created by Kevin on 17/10/2016.
 */
public class Params {

    private static Acr122Device acr122;
    private static boolean connected = false;

    public static boolean connect() throws Exception
    {
        try {
            acr122 = new Acr122Device();
        } catch (RuntimeException re) {
            connected = false;
            return false;
        }
        acr122.open();
        connected = true;
        return true;
    }

    public static boolean isConnected() {
        return (connected);
    }

    public static void disconnect() throws IOException {
        acr122.close();
        connected = false;
    }
}
