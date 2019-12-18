package pucmm.temas.especiales.e_commerce_app.asynctasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pucmm.temas.especiales.e_commerce_app.utils.HttpConnection;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class ForgotTask extends AsyncTask<Void, Void, Result> {

    private static final String FORGOT_URL = "http://ec2-3-86-40-181.compute-1.amazonaws.com:6789/forgot";

    private final String email;
    private final Response.Listener listener;
    private final Response.ErrorListener errorListener;

    public ForgotTask(String email, Response.Listener listener, Response.ErrorListener errorListener) {
        this.email = email;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Result doInBackground(Void... voids) {
        final HttpConnection connection = new HttpConnection(FORGOT_URL, "POST");
        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            return new Result(connection.post("{\"email\":\""+email+"\"}"));
        }catch (IOException e){
            return new Result<>(new Exception(e));
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result.getResult() != null) {
            try {
                JSONObject jsonObject = new JSONObject(result.getResult().toString());
                if(jsonObject.getString("error").equals("false")){
                    listener.onResponse(jsonObject);
                }else{
                    errorListener.onErrorResponse(new Exception(jsonObject.getString("message")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            errorListener.onErrorResponse(result.getError());
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
