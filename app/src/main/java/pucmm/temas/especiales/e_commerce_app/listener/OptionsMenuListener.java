package pucmm.temas.especiales.e_commerce_app.listener;

import android.view.View;

public interface OptionsMenuListener<T> {
    public void onCreateOptionsMenu(View view, T element, int position);
}
