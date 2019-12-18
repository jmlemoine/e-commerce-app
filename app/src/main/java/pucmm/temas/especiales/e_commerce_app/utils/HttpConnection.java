package pucmm.temas.especiales.e_commerce_app.utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpConnection {
    private URL url;
    private boolean doInput;
    private boolean doOutput;
    private boolean useCaches;
    private String method;
    private HashMap<String, String> headers;
    private HttpURLConnection connection;


    private HttpConnection(String url, RequestMethod method, String s) {
    }

    public HttpConnection(final String url, final String method){
        this(url, (Map<String, String>) null, method);
    }

    public HttpConnection(final String url) {
        this(url, null, null, RequestMethod.GET);
    }

    public HttpConnection(final String url, final RequestMethod method) {
        this(url, null, null, method);
    }

    public HttpConnection(final String url, Map<String, String> params) {
        this(url, null, params, RequestMethod.GET);
    }

    public HttpConnection(final String url, final RequestMethod method, final Map<String, String> params) {
        this(url, null, params, method);
    }

    public HttpConnection(final String url, Map<String, String> requests, final String method){
        this.method = method;
        requests = new HashMap<>();
        requests.put("Content-Type", "application/json");
        requests.put("TOKEN", "Boq-xABm60zS-CZQe1NhnNkcv2tje6DGtPkrJGCM");

        if(requests != null){
            requests.putAll(requests);
        }

        try{
            this.url = new URL(url);
            connection = (HttpURLConnection) this.url.openConnection();
            connection.setRequestMethod(this.method);

            for(Map.Entry<String, String> entry: requests.entrySet()){
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if(method.equals("POST")){
                connection.setDoOutput(true);
            }else{
                connection.setDoOutput(false);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public HttpConnection(String url, final Map<String, String> headers, final Map<String, String> params, final RequestMethod method) {
        this.method = method.getValue();

        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json");
        this.headers.put("TOKEN", "bSLKWUH_YIlnpVALrY14dEMavpx0n-XXVfT5Yq2g");

        if (headers != null) {
            this.headers.putAll(headers);
        }

        if (params != null) {
            try {
                url = getUrlWithParameters(url, params);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            this.url = new URL(url);
            connection = (HttpURLConnection) this.url.openConnection();
            connection.setRequestMethod(this.method);

            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            if (RequestMethod.GET != method) {
                connection.setDoInput(true);
                connection.setDoOutput(true);
            }
            connection.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String getUrlWithParameters(final String url, final Map<String, String> params) throws UnsupportedEncodingException {
        if (params.isEmpty()) {
            return url;
        }
        final StringBuilder result = new StringBuilder()
                .append(url)
                .append("?");
        int size = params.size();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"));

            if (size > 1) {
                size--;
                result.append("&");
            }
        }
        return result.toString();
    }

    public String executeWithParams() throws IOException {
        return execute();
    }

    public String get() throws IOException{
        final BufferedReader reader;

        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }else{
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        try{
            return getReader(reader);
        }catch(IOException e){
            throw new IOException(e);
        }
    }

    public String post(String json) throws IOException{
        final BufferedReader reader;
        connection.getOutputStream().write(json.getBytes("utf-8"));
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED ){
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        }else{
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
        }
        try{
            return getReader(reader);
        }catch (IOException e){
            throw new IOException(e);
        }finally{
            connection.disconnect();
            reader.close();
        }
    }

    public String execute(JSONObject object) throws IOException {

        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

        writer.write(object.toString());
        writer.flush();
        writer.close();
        outputStream.close();

        return execute();
    }


    public String execute() throws IOException {
        final BufferedReader reader;
        Log.i("connection", String.valueOf(connection.getResponseCode()));

        if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED || connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        try {
            return reader(reader);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private String reader(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            connection.disconnect();
            reader.close();
        }
    }

    private String getReader(final BufferedReader reader) throws IOException{
        final StringBuilder stringBuilder = new StringBuilder();
        try{
            String line = null;
            while((line = reader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        }catch (IOException e){
            throw new IOException(e);
        }finally {
            connection.disconnect();
            reader.close();
        }
    }
}
