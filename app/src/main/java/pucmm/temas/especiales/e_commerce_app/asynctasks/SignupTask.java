package pucmm.temas.especiales.e_commerce_app.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.HttpConnection;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class SignupTask extends AsyncTask<Void, Void, Result> {
    private static final String LOGIN_URL = "http://ec2-3-86-40-181.compute-1.amazonaws.com:6789/register";
    private User user;
    private Response.Listener listener;
    private Response.ErrorListener errorListener;

    public SignupTask() { }

    public SignupTask(User user, Response.Listener listener, Response.ErrorListener errorListener) {
        this.user = user;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Result doInBackground(Void... params) {
        final HttpConnection connection = new HttpConnection(LOGIN_URL, "POST");
        String result;
        if(this.user != null){
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            try{
                return new Result(connection.post(gson.toJson(this.user).toString()));
            }catch (IOException e){
                return new Result<>(new Exception(e));
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        if(result.getResult() != null){
            try {
                Log.i("INFORMATION", result.getResult().toString());
                JSONObject jsonObject = new JSONObject(result.getResult().toString());
                if(jsonObject.getString("error").equals("false")){
                    listener.onResponse(jsonObject);
                }else{
                    errorListener.onErrorResponse(new Exception(jsonObject.getString("message")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled() {
        errorListener.onErrorResponse(new Exception("Task cancelled"));
    }
}
