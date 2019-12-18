package pucmm.temas.especiales.e_commerce_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Networking {
    public static boolean getConnectionStatus(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}