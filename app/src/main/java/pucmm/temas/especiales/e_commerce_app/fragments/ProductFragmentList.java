package pucmm.temas.especiales.e_commerce_app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.adapter.ProductAdapter;
import pucmm.temas.especiales.e_commerce_app.asynctasks.ProductRequest;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.Category;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.listener.OnItemTouchListener;
import pucmm.temas.especiales.e_commerce_app.listener.OptionsMenuListener;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.UserSession;


public class ProductFragmentList extends Fragment {
    private static final String TAG = "ProductFragmentList";

    private User user;
    private Context context;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private UserSession session;


    public ProductFragmentList() {
        // Required empty public constructor
    }

    public static Fragment newInstance(User user, Category category) {
        ProductFragmentList fragment = new ProductFragmentList();
        Bundle args = new Bundle();
        args.putSerializable(Constant.USER, user);
        args.putSerializable(Constant.CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        user = (User) getArguments().getSerializable(Constant.USER);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> FragmentNavigationManager.obtain().showProductFragmentManager(null, user));
        if (!this.user.isIsProvider()) {
            fab.setVisibility(View.INVISIBLE);
        }

        recyclerView = view.findViewById(R.id.product_recycler_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        session = new UserSession(context);

        String path = "product";
        Category category = (Category) getArguments().getSerializable(Constant.CATEGORY);
//        if (category != null) {
//            path += "/category/" + category.getId();
//        }

        ProductRequest request = new ProductRequest(path, RequestMethod.GET, (Response.Listener<JSONArray>) response -> {
            final List<Product> elements = new ArrayList<>();

            for (int i = 0; i < response.length() - 1; i++) {
                try {
                    Product product = new Product(response.getJSONObject(i));

                    if (category != null) {
                        if (product.getCategoryId() == category.getId()) {
                            elements.add(product);
                        }
                    } else {
                        elements.add(product);
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            final ProductAdapter adapter = new ProductAdapter(context, elements, getArguments());

            adapter.setOptionsMenuListener((OptionsMenuListener<Product>) (view1, element, position) -> {
                PopupMenu popup = new PopupMenu(context, view1);
                popup.inflate(R.menu.action_menu);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_manager:
                            FragmentNavigationManager.obtain().showProductFragmentManager(element, user);
                            return true;
                        case R.id.action_delete:
                            delete(element, adapter, position);
                            return true;
                        default:
                            return false;
                    }
                });
                popup.show();
            });

            adapter.setOnItemTouchListener((OnItemTouchListener<Product>) element -> {

                FragmentNavigationManager.obtain().showProductDetailsFragment(user, element);

            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

        }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
        request.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void delete(final Product product, final ProductAdapter adapter, final int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogAlert);
        builder.setTitle("Confirm dialog delete!")
                .setMessage("You are about to delete record. Do you really want to proceed?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    ProductRequest request = new ProductRequest(product, String.format("%s/%s", "product", product.getItemCode()), RequestMethod.DELETE, (Response.Listener<JSONObject>) response -> {

                        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        adapter.getElements().remove(product);
                        adapter.notifyDataSetChanged();
                        adapter.notifyItemRemoved(position);
                        FirebaseNetwork.obtain().delete(product.getPhoto(), (Response.Listener<String>) response1 -> Toast.makeText(context, response1, Toast.LENGTH_SHORT).show(), error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());

                    }, error -> Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show());
                    request.execute();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel()).show();

    }
}
