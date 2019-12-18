package pucmm.temas.especiales.e_commerce_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pucmm.temas.especiales.e_commerce_app.asynctasks.LoginTask;
import pucmm.temas.especiales.e_commerce_app.dialog.MessageDialog;
import pucmm.temas.especiales.e_commerce_app.entities.User;
import pucmm.temas.especiales.e_commerce_app.utils.FieldValidator;
import pucmm.temas.especiales.e_commerce_app.utils.Networking;
import pucmm.temas.especiales.e_commerce_app.utils.UserSession;

public class LoginActivity extends AppCompatActivity {
    private EditText user;
    private EditText password;
    private Button login;
    private TextView signup;
    private TextView forgotPassword;
    private Context applicationContext;

    private UserSession session;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        updateUI();
    }

    private void updateUI() {
        this.applicationContext = this;
        this.user = (EditText) findViewById(R.id.txtUser);
        this.password = (EditText) findViewById(R.id.txtPassword);
        this.login = (Button) findViewById(R.id.btnLogin);
        this.signup = (TextView) findViewById(R.id.viewSignup);
        this.forgotPassword = (TextView) findViewById(R.id.viewForgot);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar_login);

        session = new UserSession(getApplicationContext());

        //access event for login authentication
        this.login.setOnClickListener(view -> auth());

        //Access event for signup
        this.signup.setOnClickListener(view -> showSignup());

        //Access event for Forgot password
        this.forgotPassword.setOnClickListener(view -> showForgotPassword());
    }

    private void auth(){
        progressBarVisibleLoginInvisible();
        boolean validator = true;
        if(FieldValidator.isEmpty(this.user)){
            progressbarInvisibleLoginButtonVisible();
            this.user.setError("This field can not be blank");
            validator = false;
        }else if(!FieldValidator.isEmailValid(this.user, this.user.getText().toString())){
            progressbarInvisibleLoginButtonVisible();
            validator = false;
        }

        if(FieldValidator.isEmpty(this.password)){
            progressbarInvisibleLoginButtonVisible();
            this.password.setError("This field can not be blank");
            validator = false;
        }else if(!FieldValidator.isPasswordValid(this.password, this.password.getText().toString())){
            progressbarInvisibleLoginButtonVisible();
            validator = false;
        }

        if(validator){
            //verify if the internet is available before request the login
            if(Networking.getConnectionStatus(this)){
                LoginTask loginTask = new LoginTask(this.user.getText().toString(),
                        this.password.getText().toString(),
                        response -> {
                            Gson gson = new Gson();
                            User userInformation = gson.fromJson(response.toString(), User.class);

                            //store data in the shred preference to have a session
                            session.createLoginSession(userInformation.getId(),
                                    userInformation.getEmail(),
                                    userInformation.getUser(),
                                    userInformation.getName(),
                                    userInformation.getToken(),
                                    userInformation.isIsProvider(),
                                    userInformation.getPhoto(),
                                    userInformation.getContact());

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }, error -> {
                            progressbarInvisibleLoginButtonVisible();
                            //send error message with Toast Class
                            MessageDialog.getInstance(applicationContext).
                                    errorDialog("Resource " + error.getMessage() + ", email or password Incorrect" );
                            user.setError("Verify email ["+user.getText().toString()+"], may be incorrect");
                            password.setError("Verify password, may be incorrect");
                        });
                loginTask.execute();
            }else{
                progressbarInvisibleLoginButtonVisible();
                Toast.makeText(this,"No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void progressBarVisibleLoginInvisible() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.login.setVisibility(View.GONE);
    }

    private void progressbarInvisibleLoginButtonVisible() {
        this.progressBar.setVisibility(View.GONE);
        this.login.setVisibility(View.VISIBLE);
    }

    private void showSignup(){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void showForgotPassword(){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
