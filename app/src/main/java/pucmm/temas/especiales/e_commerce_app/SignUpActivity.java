package pucmm.temas.especiales.e_commerce_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.asynctasks.SignupTask;
import pucmm.temas.especiales.e_commerce_app.dialog.MessageDialog;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.Constant;
import pucmm.temas.especiales.e_commerce_app.utils.FieldValidator;
import pucmm.temas.especiales.e_commerce_app.utils.RequestMethod;
import pucmm.temas.especiales.e_commerce_app.utils.SystemProperties;

public class SignUpActivity extends AppCompatActivity  {
    private EditText name;
    private EditText user;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText contact;
    private TextView forgotPassword;
    private TextView login;
    private Button signup;
    private Context applicationContext;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        updateUI();
    }

    private void updateUI() {
        this.applicationContext = this;
        this.name = (EditText) findViewById(R.id.txtName);
        this.user = (EditText) findViewById(R.id.txtUser);
        this.email = (EditText) findViewById(R.id.txtEmail);
        this.password = (EditText) findViewById(R.id.txtPassword);
        this.confirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        this.contact = (EditText) findViewById(R.id.txtContact);
        //Setting format to contact field
        this.contact.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        this.forgotPassword = (TextView) findViewById(R.id.viewForgot);
        this.login = (TextView) findViewById(R.id.viewLogin);
        this.signup = (Button) findViewById(R.id.btnSignup);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar_signup);


        //Access event for Forgot password
        this.forgotPassword.setOnClickListener(view -> showForgotPassword());

        this.login.setOnClickListener(view -> showLogin());

        //When the signup button is clicked
        this.signup.setOnClickListener(view -> registrate());
    }


    private void showForgotPassword(){
        Intent intent = new Intent(SignUpActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void showLogin(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void registrate(){
        boolean validator = true;
        if(FieldValidator.isEmpty(this.name)){
            this.name.setError("This field can not be blank");
            validator = false;
            progressBarInvisibleSignupVisible();
        }
        if(FieldValidator.isEmpty(this.user)){
            this.user.setError("This field can not be blank");
            validator = false;
            progressBarInvisibleSignupVisible();
        }
        if(FieldValidator.isEmpty(this.password)){
            this.password.setError("This field can not be blank");
            validator = false;
            progressBarInvisibleSignupVisible();
        }
        if(FieldValidator.isEmpty(this.confirmPassword)){
            this.confirmPassword.setError("This field can not be blank");
            validator = false;
            progressBarInvisibleSignupVisible();
        }
        if(FieldValidator.isEmpty(this.email)){
            this.email.setError("This field can not be blank");
            validator = false;
            progressBarInvisibleSignupVisible();
        }else if(FieldValidator.isEmailValid(this.email, this.email.getText().toString())){
            progressBarInvisibleSignupVisible();
        }

        if(validator){
            progressBarVisibleSignupInvisible();
            //verifies if the passwords provided are igual, if it's the case then do the post
            if(!this.password.getText().toString().equals(this.confirmPassword.getText().toString())){
                this.password.setError("password doesn't match");
                this.confirmPassword.setError("password doesn't match");
                progressBarInvisibleSignupVisible();
            }else{
                User user = new User(this.name.getText().toString(),
                        this.user.getText().toString(),
                        this.email.getText().toString(),
                        this.password.getText().toString(),
                        this.contact.getText().toString(),
                        "",false);
                SignupTask signupTask = new SignupTask(user, (Response.Listener<JSONObject>) response -> {
                    progressBarInvisibleSignupVisible();
                    cleanTextFields();
                    Toast toast = Toast.makeText(applicationContext,"User was created Successfuly", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,0,0);
                    toast.show();
                }, error -> {
                    MessageDialog.getInstance(applicationContext).errorDialog("["+email.getText().toString()+"] "+ error.getMessage());
                    progressBarInvisibleSignupVisible();
                });
                signupTask.execute();
            }
        }else{
            progressBarInvisibleSignupVisible();
        }
    }

    private void cleanTextFields(){
        this.name.setText(null);
        this.user.setText(null);
        this.password.setText(null);
        this.confirmPassword.setText(null);
        this.email.setText(null);
        this.contact.setText(null);
    }

    private void progressBarInvisibleSignupVisible(){
        progressBar.setVisibility(View.GONE);
        signup.setVisibility(View.VISIBLE);
    }

    private void progressBarVisibleSignupInvisible(){
        progressBar.setVisibility(View.VISIBLE);
        signup.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Login CheckPoint", "LoginActivity resumed");
        //TODO: CHECK INTERNET CONNECTION
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
