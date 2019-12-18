package pucmm.temas.especiales.e_commerce_app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pucmm.temas.especiales.e_commerce_app.listener.ReloadBadgeInterface;

public class BadgeReceiver extends BroadcastReceiver {
    private final ReloadBadgeInterface reloadBadgeInterface;

    public BadgeReceiver(ReloadBadgeInterface reloadBadgeInterface) {
        this.reloadBadgeInterface = reloadBadgeInterface;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        reloadBadgeInterface.reloadBadge();

    }
}
