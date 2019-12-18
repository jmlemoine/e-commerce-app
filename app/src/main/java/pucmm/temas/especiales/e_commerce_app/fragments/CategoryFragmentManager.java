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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.asynctasks.CategoryRequest;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.Category;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FieldValidator;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.KProgressHUDUtils;
import pucmm.temas.especiales.e_commerce_app.utils.PhotoOptions;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class CategoryFragmentManager extends Fragment {
    private static final String TAG = "CategoryFragmentList";
    private static final int PICK_IMAGE = 1;
    private static final int CHOOSE_GALLERY = 2;
    private static final int TAKE_PHOTO = 3;

    private Context context;
    private Uri uri;
    private boolean profileDefault = true;

    private Category element;
    private User user;
    private ImageView profile;
    private CheckBox active;
    private Button save;
    private Button back;
    private EditText name;

    public CategoryFragmentManager() {
        // Required empty public constructor
    }

    public static CategoryFragmentManager newInstance(Category element, User user) {
        CategoryFragmentManager fragment = new CategoryFragmentManager();
        Bundle args = new Bundle();
        args.putSerializable(Constant.CATEGORY, element);
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
        element = (Category) getArguments().getSerializable(Constant.CATEGORY);
        user = (User) getArguments().getSerializable(Constant.USER);

        View view = inflater.inflate(R.layout.fragment_category_manager, container, false);

        profile = view.findViewById(R.id.profile);
        active = view.findViewById(R.id.active);
        name = view.findViewById(R.id.nameCat);
        save = view.findViewById(R.id.manager);
        back = view.findViewById(R.id.back);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        active.setChecked(true);

        if (element != null) {
            name.setText(element.getName());
            active.setChecked(element.isActive());
            FirebaseNetwork.obtain().download(element.getPhoto(), (Response.Listener<Bitmap>) response -> profile.setImageBitmap(response), error -> Log.e(TAG, error.getMessage()));
        }

        save.setOnClickListener(v -> {
            if (FieldValidator.isEmpty(getContext(), name)) {
                return;
            }
            createOrUpdate();
        });

        back.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());

        profile.setOnClickListener(v -> photoOptions());

    }

    private void createOrUpdate() {
        if (element == null) {
            element = new Category();
        }

        RequestMethod method;
        if (Integer.valueOf(element.getId()).equals(0)) {
            method = RequestMethod.POST;
            element.setUserId(user.getId());
        } else {
            method = RequestMethod.PUT;
        }

        element.setName(name.getText().toString());
        element.setActive(active.isChecked());

        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();

        CategoryRequest request = new CategoryRequest(element, "category", method, (Response.Listener<JSONObject>) response -> {
            progressDialog.dismiss();

            if (!profileDefault) {

                element = new Category(response);


                FirebaseNetwork.obtain().upload(uri, String.format("category/%s.jpg", element.getId()), (Response.Listener<String>) response12 -> {
                    element.setPhoto(response12);

                    new CategoryRequest(element, "category", RequestMethod.PUT, response1 -> Log.i(TAG, "CategoryRequest:upload"), error -> {
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
        });
        request.execute();
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
