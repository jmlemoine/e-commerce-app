package pucmm.temas.especiales.e_commerce_app.receiver;

import android.content.BroadcastReceiver;

public final class BroadcastReceiverManager {
    private static BroadcastReceiverManager sInstance;

    private BroadcastReceiver receiverBadge;

    public static BroadcastReceiverManager obtain() {
        if (sInstance == null) {
            sInstance = new BroadcastReceiverManager();
        }
        return sInstance;
    }

    public void setReceiverBadge(BroadcastReceiver receiverBadge) {
        this.receiverBadge = receiverBadge;
    }

    public BroadcastReceiver getReceiverBadge() {
        return receiverBadge;
    }
}
