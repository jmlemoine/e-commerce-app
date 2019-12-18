package pucmm.temas.especiales.e_commerce_app.asynctasks;

public class Response<T> {
    public interface Listener<T>{
        void onResponse(T response);
    }

    public interface ErrorListener{
        void onErrorResponse(Exception error);
    }
}
