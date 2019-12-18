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
import pucmm.temas.especiales.e_commerce_app.entities.Category;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.listener.OnItemTouchListener;
import pucmm.temas.especiales.e_commerce_app.listener.OptionsMenuListener;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements Serializable {
    private static final String TAG = "CategoryAdapter";

    private Context context;

    private User user;
    private List<Category> elements;
    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView name;
        public ImageView action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            action = itemView.findViewById(R.id.manager);
        }

    }

    public CategoryAdapter(Context context, List<Category> elements, Bundle bundle) {
        this.context = context;
        this.elements = elements;
        this.user = (User) bundle.getSerializable(Constant.USER);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Category element = elements.get(position);

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

        holder.name.setOnClickListener(v -> {
            if (onItemTouchListener != null) {
                onItemTouchListener.onClick(element);
            }
        });


        holder.name.setText(element.getName());

        if (element.getPhoto() != null && !element.getPhoto().isEmpty()) {
            FirebaseNetwork.obtain().download(element.getPhoto(), (Response.Listener<Bitmap>) response -> holder.avatar.setImageBitmap(response), error -> Log.e(TAG, error.getMessage()));
        }
    }

    public List<Category> getElements() {
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
