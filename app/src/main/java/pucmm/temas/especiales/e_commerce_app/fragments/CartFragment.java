package pucmm.temas.especiales.e_commerce_app.fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.adapter.CartAdapter;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.listener.OnItemTouchCartListener;
import pucmm.temas.especiales.e_commerce_app.receiver.BroadcastReceiverManager;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;
import pucmm.temas.especiales.e_commerce_app.utils.UserSession;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";

    private User user;
    private Context context;
    private RecyclerView recyclerView;
    private Button checkout;
    private TextView subTotal;
    private TextView subTotalLabel;
    private UserSession session;


    public CartFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(User user) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.USER, user);
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
        View view = inflater.inflate(R.layout.fragment_cart_list, container, false);

        user = (User) getArguments().getSerializable(Constant.USER);

        recyclerView = view.findViewById(R.id.cart_recycler_view);
        checkout = view.findViewById(R.id.checkout);
        subTotal = view.findViewById(R.id.subTotal);
        subTotalLabel = view.findViewById(R.id.subTotalLabel);

        checkout.setOnClickListener(v -> Snackbar.make(getView(), "Coming soon", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        session = new UserSession(context);

        subTotalLabel.setText(String.format("Sub total (%s items): ", session.getCartCount()));
        subTotal.setText(SystemProperties.getDecimalFormat(session.getCartTotal()));

        CartAdapter adapter = new CartAdapter(context, session.getCart(), getArguments());

        adapter.setOnItemTouchCartListener(new OnItemTouchCartListener<JSONObject>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDelete(int position) {
                session.removeCart(position);

                reload(adapter, position);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onAdd(JSONObject element, int position) {
                session.addToCart(new Product(element), 1);

                reload(adapter, position);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRemove(JSONObject element, int position) {
                session.addToCart(new Product(element), -1);

                reload(adapter, position);
            }

        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    private void reload(CartAdapter adapter, int position) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(BroadcastReceiverManager.obtain().getReceiverBadge(), filter);

        adapter.setElements(session.getCart());
        adapter.notifyDataSetChanged();
        adapter.notifyItemChanged(position);

        subTotalLabel.setText(String.format("Sub total (%s items): ", session.getCartCount()));
        subTotal.setText(SystemProperties.getDecimalFormat(session.getCartTotal()));
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
