package pucmm.temas.especiales.e_commerce_app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.asynctasks.CategoryRequest;
import pucmm.temas.especiales.e_commerce_app.asynctasks.ProductRequest;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.Category;
import pucmm.temas.especiales.e_commerce_app.entities.Product;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FieldValidator;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.KProgressHUDUtils;
import pucmm.temas.especiales.e_commerce_app.utils.PhotoOptions;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;


public class ProductFragmentManager extends Fragment {
    private static final String TAG = "ProductFragmentManager";
    private static final int PICK_IMAGE = 1;
    private static final int CHOOSE_GALLERY = 2;
    private static final int TAKE_PHOTO = 3;

    private Context context;
    private Uri uri;
    private boolean profileDefault = true;
    private boolean updated = false;

    private Product element;
    private User user;
    private ImageView profile;
    private EditText itemName;
    private EditText price;
    private Spinner category;
    private CheckBox active;
    private Button save;
    private Button back;

    public ProductFragmentManager() {
        // Required empty public constructor
    }

    public static ProductFragmentManager newInstance(Product element, User user) {
        ProductFragmentManager fragment = new ProductFragmentManager();
        Bundle args = new Bundle();
        args.putSerializable(Constant.PRODUCT, element);
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
        element = (Product) getArguments().getSerializable(Constant.PRODUCT);
        user = (User) getArguments().getSerializable(Constant.USER);

        View view = inflater.inflate(R.layout.fragment_product_manager, container, false);

        profile = view.findViewById(R.id.profile);
        save = view.findViewById(R.id.manager);
        back = view.findViewById(R.id.back);

        active = view.findViewById(R.id.active);
        itemName = view.findViewById(R.id.itemName);
        price = view.findViewById(R.id.price);
        category = view.findViewById(R.id.category);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        new CategoryRequest("category", RequestMethod.GET, (Response.Listener<JSONArray>) response -> {
            final List<Category> elements = new ArrayList<>();

            for (int i = 0; i < response.length() - 1; i++) {
                try {
                    elements.add(new Category(response.getJSONObject(i)));
                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            ArrayAdapter<Category> adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, elements);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            category.setAdapter(adapter);
            if (element != null) {
                int spinnerPosition = adapter.getPosition(new Category(element.getCategoryId()));
                category.setSelection(spinnerPosition);
            }
        }, error -> {

        }).execute();

        active.setChecked(true);

        if (element != null) {
            updated = true;
            itemName.setText(element.getItemName());
            price.setText(String.valueOf(element.getPrice()));
            active.setChecked(element.isActive());

            FirebaseNetwork.obtain().download(element.getPhoto(), (Response.Listener<Bitmap>) response -> profile.setImageBitmap(response), error -> Log.e(TAG, error.getMessage()));
        }

        save.setOnClickListener(v -> {
            if (FieldValidator.isEmpty(getContext(), itemName, price)) {
                return;
            }
            createOrUpdate();
        });

        back.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());

        profile.setOnClickListener(v -> photoOptions());

    }

    private void createOrUpdate() {
        if (element == null) {
            element = new Product();
        }

        RequestMethod method;
        if (updated) {
            method = RequestMethod.PUT;
        } else {
            method = RequestMethod.POST;
            element.setUserId(user.getId());
            updated = true;
            element.setItemCode(SystemProperties.generateItemCode());
        }

        element.setItemName(itemName.getText().toString());
        element.setPrice(Double.valueOf(price.getText().toString()));
        element.setActive(active.isChecked());

        Category cat = (Category) category.getSelectedItem();
        element.setCategoryId(cat.getId());


        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();

        new ProductRequest(element, "product", method, (Response.Listener<JSONObject>) response -> {
            progressDialog.dismiss();

            if (!profileDefault) {

                element = new Product(response);


                FirebaseNetwork.obtain().upload(uri, String.format("product/%s.jpg", element.getItemCode()), (Response.Listener<String>) response12 -> {
                    element.setPhoto(response12);

                    new ProductRequest(element, "product", RequestMethod.PUT, response1 -> Log.i(TAG, "ProductRequest:upload"), error -> {
                        progressDialog.dismiss();
                        FirebaseNetwork.obtain().delete(element.getPhoto(), (Response.Listener<String>) response121 -> Toast.makeText(getContext(), response121, Toast.LENGTH_SHORT).show(), error1 -> Toast.makeText(getContext(), error1.getMessage(), Toast.LENGTH_SHORT).show());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }).execute();

                }, error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
            }
            Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
            profileDefault = true;
        }, error -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void photoOptions() {
        profileDefault = true;
        final CharSequence[] options = SystemProperties.getOptions();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your category picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals(PhotoOptions.TAKE_PHOTO.getValue())) {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, TAKE_PHOTO);

            } else if (options[item].equals(PhotoOptions.CHOOSE_GALLERY.getValue())) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, CHOOSE_GALLERY);

            } else if (options[item].equals(PhotoOptions.CHOOSE_FOLDER.getValue())) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            } else if (options[item].equals(PhotoOptions.CANCEL.getValue())) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE || requestCode == CHOOSE_GALLERY) && resultCode == Activity.RESULT_OK) {
            try {
                uri = data.getData();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileDefault = false;
                profile.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profile.setImageBitmap(bitmap);
            profileDefault = false;
        }
    }
}
