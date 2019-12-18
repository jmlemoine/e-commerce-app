package pucmm.temas.especiales.e_commerce_app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.listener.OnItemTouchCartListener;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> implements Serializable {
    private static final String TAG = "CartAdapter";

    private Context context;
    private OnItemTouchCartListener onItemTouchCartListener;

    private User user;
    private JSONArray elements;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView itemName;
        public TextView qty;
        public TextView price;

        public Button remove;
        public Button add;
        public Button action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            itemName = itemView.findViewById(R.id.itemName);
            qty = itemView.findViewById(R.id.qty);
            price = itemView.findViewById(R.id.price);

            remove = itemView.findViewById(R.id.remove);
            add = itemView.findViewById(R.id.add);
            action = itemView.findViewById(R.id.action);

        }

    }

    public CartAdapter(Context context, JSONArray elements, Bundle bundle) {
        this.context = context;
        this.elements = elements;
        this.user = (User) bundle.getSerializable(Constant.USER);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            final JSONObject element = elements.getJSONObject(position);

            holder.remove.setOnClickListener(v -> {
                if (onItemTouchCartListener != null) {
                    onItemTouchCartListener.onRemove(element, position);
                }
            });

            holder.add.setOnClickListener(v -> {
                if (onItemTouchCartListener != null) {
                    onItemTouchCartListener.onAdd(element, position);
                }
            });

            holder.action.setOnClickListener(v -> {
                if (onItemTouchCartListener != null) {
                    onItemTouchCartListener.onDelete(position);
                }
            });

            holder.qty.setText(String.valueOf(element.getInt("qty")));

            Product product = new Product(element);

            holder.itemName.setText(product.getItemName());
            holder.price.setText(SystemProperties.getDecimalFormat(product.getPrice()));

            if (product.getPhoto() != null && !product.getPhoto().isEmpty()) {
                FirebaseNetwork.obtain().download(product.getPhoto(), (Response.Listener<Bitmap>) response -> holder.avatar.setImageBitmap(response), error -> Log.e(TAG, error.getMessage()));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public JSONArray getElements() {
        return elements;
    }

    public void setElements(JSONArray elements) {
        this.elements = elements;
    }

    @Override
    public int getItemCount() {
        return elements.length();
    }

    public void setOnItemTouchCartListener(OnItemTouchCartListener onItemTouchCartListener) {
        this.onItemTouchCartListener = onItemTouchCartListener;
    }
}
