package pucmm.temas.especiales.e_commerce_app.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.HttpConnection;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class RegisterRequest extends AsyncTask<Void, Void, Result> {
    private final User user;
    private final Response.Listener listener;
    private final Response.ErrorListener errorListener;
    private final String path;
    private final RequestMethod method;

    public RegisterRequest(User user, String path, RequestMethod method, Response.Listener listener, Response.ErrorListener errorListener) {
        this.listener = listener;
        this.errorListener = errorListener;
        this.user = user;
        this.path = path;
        this.method = method;
    }

    @Override
    protected Result doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        Log.i(this.toString(), "RegisterRequest:doInBackground Called");

        final HttpConnection connection = new HttpConnection(SystemProperties.getResource(this.path), this.method);
        try {
            return new Result(connection.execute(user.toJson()));
        } catch (IOException e) {
            return new Result<>(new Exception(e));
        }
    }

    @Override
    protected void onPostExecute(final Result success) {

        if (success.getResult() != null) {
            try {
                final JSONObject object = new JSONObject(String.valueOf(success.getResult()));

                if ((boolean) object.get("success")) {
                    listener.onResponse(object);
                } else {
                    errorListener.onErrorResponse(new Exception(object.getString("message")));
                }

            } catch (JSONException e) {
                errorListener.onErrorResponse(new Exception(e.getCause()));
            }
        } else {
            errorListener.onErrorResponse(success.getError());
        }
    }

    @Override
    protected void onCancelled() {
        errorListener.onErrorResponse(new Exception("Task cancelled"));
    }
}
