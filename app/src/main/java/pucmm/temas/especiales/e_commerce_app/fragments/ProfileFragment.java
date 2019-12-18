package pucmm.temas.especiales.e_commerce_app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import pucmm.temas.especiales.e_commerce_app.R;
import pucmm.temas.especiales.e_commerce_app.asynctasks.RegisterRequest;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FieldValidator;
import pucmm.temas.especiales.e_commerce_app.utils.FirebaseNetwork;
import pucmm.temas.especiales.e_commerce_app.utils.KProgressHUDUtils;
import pucmm.temas.especiales.e_commerce_app.utils.PhotoOptions;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int PICK_IMAGE = 1;
    private static final int CHOOSE_GALLERY = 2;
    private static final int TAKE_PHOTO = 3;

    private User user;
    private Context context;
    private Uri uri;
    private boolean profileDefault = true;

    private ImageView profile;
    private EditText name;
    private EditText contact;
    private CheckBox isProvider;
    private Button save;
    private Button back;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = (User) getArguments().getSerializable(Constant.USER);

        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.name);
        contact = view.findViewById(R.id.contact);
        isProvider = view.findViewById(R.id.isProvider);

        save = view.findViewById(R.id.manager);
        back = view.findViewById(R.id.back);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        name.setText(user.getName());
        contact.setText(user.getContact());
        isProvider.setChecked(user.isIsProvider());


        FirebaseNetwork.obtain().download(user.getPhoto(),
                (Response.Listener<Bitmap>) response -> profile.setImageBitmap(response),
                error -> Log.e(TAG, error.getMessage()));

        save.setOnClickListener(v -> {
            if (FieldValidator.isEmpty(getContext(), name, contact)) {
                return;
            }
            update();
        });

        back.setOnClickListener(v -> getFragmentManager().popBackStackImmediate());

        profile.setOnClickListener(v -> photoOptions());

    }

    private void update() {

        user.setName(name.getText().toString());
        user.setContact(contact.getText().toString());
        user.setIsProvider(isProvider.isChecked());

        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();

        RegisterRequest request = new RegisterRequest(user, "user/update", RequestMethod.PUT,
                (Response.Listener<JSONObject>) response -> {
            progressDialog.dismiss();

            if (!profileDefault) {

                user = new User(response);

                FirebaseNetwork.obtain().upload(uri, String.format("profile/%s.jpg", user.getId()),
                        (Response.Listener<String>) response1 -> {
                    user.setPhoto(response1);

                    new RegisterRequest(user, "user/update", RequestMethod.PUT,
                            response11 -> Log.i(TAG, "RegisterRequest:upload"), error -> {
                                progressDialog.dismiss();
                                FirebaseNetwork.obtain().delete(user.getPhoto(),
                                        (Response.Listener<String>) response112 -> Toast.makeText(getContext(),
                                                response112, Toast.LENGTH_SHORT).show(),
                                        error1 -> Toast.makeText(getContext(),
                                                error1.getMessage(), Toast.LENGTH_SHORT).show());
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
        builder.setTitle("Choose your profile picture");

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
            profileDefault = false;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            profile.setImageBitmap(bitmap);
        }
    }
}
