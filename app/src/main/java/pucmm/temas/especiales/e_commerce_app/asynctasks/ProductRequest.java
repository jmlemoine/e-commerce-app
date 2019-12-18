package pucmm.temas.especiales.e_commerce_app.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.utils.HttpConnection;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class ProductRequest extends AsyncTask <Void, Void, Result>{
    private final Product product;
    private final Response.Listener listener;
    private final Response.ErrorListener errorListener;
    private final String path;
    private final RequestMethod method;

    public ProductRequest(Product product, String path, RequestMethod method, Response.Listener listener, Response.ErrorListener errorListener) {
        this.listener = listener;
        this.errorListener = errorListener;
        this.product = product;
        this.path = path;
        this.method = method;
    }

    public ProductRequest(String path, RequestMethod method, Response.Listener listener, Response.ErrorListener errorListener) {
        this(null, path, method, listener, errorListener);
    }

    @Override
    protected Result doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        Log.i(this.toString(), "ProductRequest:doInBackground Called");
        final HttpConnection connection = new HttpConnection(SystemProperties.getResource(this.path), this.method);
        try {
            if (method.equals(RequestMethod.GET)) {
                return new Result(connection.execute());
            } else if (product != null) {
                if (method.equals(RequestMethod.POST)) {
                    return new Result(connection.execute(product.toJson()));
                } else if (method.equals(RequestMethod.PUT)) {
                    return new Result(connection.execute(product.toJson()));
                } else if (method.equals(RequestMethod.DELETE)) {
                    return new Result(connection.execute());
                } else {
                    return new Result(new Exception("Not found"));
                }
            } else {
                return new Result(new Exception("Not found"));
            }

        } catch (IOException e) {
            return new Result<>(new Exception(e));
        }
    }

    @Override
    protected void onPostExecute(final Result success) {

        Log.i("onPostExecute", success.getResult().toString());
        if (success.getResult() != null) {
            try {
                JSONObject object;
                JSONArray array = new JSONArray();

                if (method != RequestMethod.GET) {
                    object = new JSONObject(String.valueOf(success.getResult()));
                } else {
                    array = new JSONArray(String.valueOf(success.getResult()));
                    object = array.getJSONObject(array.length() - 1);
                }

                if ((boolean) object.get("success")) {
                    if (method != RequestMethod.GET) {
                        listener.onResponse(object);
                    } else {
                        listener.onResponse(array);
                    }

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
