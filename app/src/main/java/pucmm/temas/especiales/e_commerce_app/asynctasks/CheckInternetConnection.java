package pucmm.temas.especiales.e_commerce_app.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class CheckInternetConnection {
    Context context;

    public CheckInternetConnection(Context context) {
        this.context = context;
    }

    public void checkConnection() {

        //TODO: No Internet, Cannot connect to a servers
        if (!isInternetConnected()) {
            Log.i("CheckInternetConnection", "There is no Internet conection");
        } else {
            Log.i("CheckInternetConnection", "There is internet connection");
        }
    }

    public boolean isInternetConnected() {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true;
                }
                return false;
            }
        } else {
            try {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    Log.i("update_statut", "Network is available : true");
                    return true;
                }
            } catch (Exception e) {
                Log.i("update_statut", "" + e.getMessage());
            }
        }
        Log.i("update_statut", "Network is available : FALSE ");
        return false;

    }
}
