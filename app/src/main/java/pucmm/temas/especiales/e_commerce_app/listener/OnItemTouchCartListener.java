package pucmm.temas.especiales.e_commerce_app.listener;

public interface OnItemTouchCartListener<T> {
    void onDelete(int position);

    void onAdd(T element, int position);

    void onRemove(T element, int position);
}
