package pucmm.temas.especiales.e_commerce_app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.listener.OnItemTouchListener;
import pucmm.temas.especiales.e_commerce_app.listener.OptionsMenuListener;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> implements Serializable {
    private static final String TAG = "ProductAdapter";

    private Context context;

    private User user;
    private List<Product> elements;
    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView itemName;
        public TextView itemCode;
        public TextView price;
        public ImageView action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            itemCode = itemView.findViewById(R.id.itemCode);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            action = itemView.findViewById(R.id.manager);
        }

    }

    public ProductAdapter(Context context, List<Product> elements, Bundle bundle) {
        this.context = context;
        this.elements = elements;
        this.user = (User) bundle.getSerializable(Constant.USER);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Product element = elements.get(position);

        if (!this.user.isIsProvider()) {
            holder.action.setVisibility(View.INVISIBLE);
        }

        holder.action.setOnClickListener(v -> {
            if (optionsMenuListener != null) {
                optionsMenuListener.onCreateOptionsMenu(holder.action, element, position);
            }
        });

        holder.avatar.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });

        holder.itemCode.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });

        holder.itemName.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });


        holder.itemCode.setText(element.getItemCode());
        holder.itemName.setText(element.getItemName());
        holder.price.setText(SystemProperties.getDecimalFormat(element.getPrice()));


        if (element.getPhoto() != null && !element.getPhoto().isEmpty()) {
            FirebaseNetwork.obtain().download(element.getPhoto(), (Response.Listener<Bitmap>) response -> holder.avatar.setImageBitmap(response), error -> Log.e(TAG, error.getMessage()));
        }
    }

    public List<Product> getElements() {
        return elements;
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void setOptionsMenuListener(OptionsMenuListener optionsMenuListener) {
        this.optionsMenuListener = optionsMenuListener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }
}
