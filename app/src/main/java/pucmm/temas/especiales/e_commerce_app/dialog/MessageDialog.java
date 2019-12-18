package pucmm.temas.especiales.e_commerce_app.dialog;

import android.app.AlertDialog;
import android.content.Context;

import pucmm.temas.especiales.e_commerce_app.R;

public class MessageDialog {
    private static MessageDialog instance;
    private static AlertDialog.Builder builder;

    private MessageDialog(){ }

    public static MessageDialog getInstance(Context context){
        builder = new AlertDialog.Builder(context);
        if(instance == null){
            instance = new MessageDialog();
        }
        return instance;
    }

    public void informationDialog(String message){
        builder.setIcon(R.drawable.ic_error_alert);
        builder.setTitle("Information Message").setMessage(message);
        builder.create().show();
    }

    public void errorDialog(String message){
        builder.setIcon(R.drawable.ic_error);
        builder.setTitle("Error Message").setMessage(message);
        builder.create().show();
    }
}
