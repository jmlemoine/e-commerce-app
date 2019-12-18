package pucmm.temas.especiales.e_commerce_app.fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.receiver.BroadcastReceiverManager;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;
import pucmm.temas.especiales.e_commerce_app.utils.UserSession;

public class ProductDetailsFragment extends Fragment {
    private static final String TAG = "ProductDetailsFragment";

    private User user;
    private Product product;
    private Context context;
    private UserSession session;

    //UI
    private ImageView avatar;
    private TextView itemName;
    private TextView qty;
    private TextView price;

    private Button remove;
    private Button add;
    private Button action;


    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(User user, Product product) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.USER, user);
        args.putSerializable(Constant.PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        user = (User) getArguments().getSerializable(Constant.USER);
        product = (Product) getArguments().getSerializable(Constant.PRODUCT);

        avatar = view.findViewById(R.id.avatar);
        itemName = view.findViewById(R.id.itemName);
        qty = view.findViewById(R.id.qty);
        price = view.findViewById(R.id.price);

        remove = view.findViewById(R.id.remove);
        add = view.findViewById(R.id.add);
        action = view.findViewById(R.id.action);

        qty.setText("1");

        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        session = new UserSession(context);


        itemName.setText(product.getItemName());
        price.setText(SystemProperties.getDecimalFormat(product.getPrice()));


        if (product.getPhoto() != null && !product.getPhoto().isEmpty()) {
            FirebaseNetwork.obtain().download(product.getPhoto(), (Response.Listener<Bitmap>) response -> avatar.setImageBitmap(response), error -> Log.e(TAG, error.getMessage()));
        }

        remove.setOnClickListener(v -> {
            if ((Integer.valueOf(qty.getText().toString())) > 1) {
                qty.setText(String.valueOf(Integer.valueOf(qty.getText().toString()) - 1));
            }
        });

        add.setOnClickListener(v -> {
            qty.setText(String.valueOf(Integer.valueOf(qty.getText().toString()) + 1));
        });

        action.setOnClickListener(v -> {

            session.addToCart(product, Integer.valueOf(qty.getText().toString()));

            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(BroadcastReceiverManager.obtain().getReceiverBadge(), filter);

        });


    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
