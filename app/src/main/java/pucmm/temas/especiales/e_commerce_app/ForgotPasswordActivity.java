package pucmm.temas.especiales.e_commerce_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pucmm.temas.especiales.e_commerce_app.asynctasks.ForgotTask;
import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;
import pucmm.temas.especiales.e_commerce_app.dialog.MessageDialog;
import pucmm.temas.especiales.e_commerce_app.entities.ErrorMessage;
import pucmm.temas.especiales.e_commerce_app.utils.FieldValidator;
import pucmm.temas.especiales.e_commerce_app.utils.Networking;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView back;
    private Button send;
    private EditText email;
    private ProgressBar progressBar;
    private Context applicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        applicationContext = this;

        updateUI();
    }

    private void updateUI() {
        this.email = (EditText) findViewById(R.id.txtForgotPassword);
        this.send = (Button) findViewById(R.id.btnSend);
        this.back = (TextView) findViewById(R.id.btnBack);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Send request to get the password
        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotEmail();
            }
        });

        //Go back to login
        this.back.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View view){
                goBack();
            }
        }));
    }

    private void sendForgotEmail(){
        progressBarVisibleSendInvisible();
        if(FieldValidator.isEmpty(this.email)){
            this.email.setError("This field can not be blank");
            progressBarInvisibleSendVisible();
        }else if(FieldValidator.isEmailValid(this.email, this.email.getText().toString())){
            //verify if the internet is available before request the forgotpassword
            if(Networking.getConnectionStatus(this)){
                ForgotTask forgotTask = new ForgotTask(this.email.getText().toString(),
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                progressBarInvisibleSendVisible();
                                Gson gson = new Gson();
                                ErrorMessage result = gson.fromJson(response.toString(), ErrorMessage.class);

                                //Call a custum dialog message to provide the forgot password
                                MessageDialog.getInstance(applicationContext).
                                        informationDialog(result.getMessage());

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(Exception error) {
                        MessageDialog.getInstance(applicationContext).
                                errorDialog(email.getText().toString()+" "+ error.getMessage());
                        progressBarInvisibleSendVisible();
                    }
                });
                forgotTask.execute();
            }else{
                Toast.makeText(this,"No Internet Connection", Toast.LENGTH_LONG).show();
                progressBarInvisibleSendVisible();
            }
        }else{
            progressBarInvisibleSendVisible();
        }
    }

    private void progressBarInvisibleSendVisible() {
        progressBar.setVisibility(View.GONE);
        send.setVisibility(View.VISIBLE);
    }

    private void progressBarVisibleSendInvisible() {
        progressBar.setVisibility(View.VISIBLE);
        this.send.setVisibility(View.GONE);
    }

    private void goBack(){
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
