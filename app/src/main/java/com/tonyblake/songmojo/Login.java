package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login extends AppCompatActivity implements CreateAccountDialog.CreateAccountDialogInterface {

    private Context context;

    private FirebaseAuth mAuth;

    private EditText et_email, et_password;

    private Button btn_log_in, btn_create_account;

    private TextView btn_forgot_password;

    private String email, password;

    private FragmentManager fm;

    private CreateAccountDialog createAccountDialog;

    private ForgotPasswordDialog forgotPasswordDialog;

    private ProgressDialog creatingAccountDialog;

    private ProgressDialog loggingInDialog;

    private DBManager dbManager;

    private String first_name, last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        context = this;

        mAuth = FirebaseAuth.getInstance();

        et_email = (EditText) findViewById(R.id.et_email);

        et_password = (EditText) findViewById(R.id.et_password);

        btn_log_in = (Button) findViewById(R.id.btn_log_in);

        btn_create_account = (Button) findViewById(R.id.btn_create_account);

        btn_forgot_password = (TextView) findViewById(R.id.btn_forgot_password);

        email = "";

        password = "";

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dbManager = new DBManager(context);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fm = getSupportFragmentManager();

        btn_log_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                email = et_email.getText().toString();

                password = et_password.getText().toString();

                if("".equals(email)){

                    Toast.makeText(context, context.getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                }
                else if("".equals(password)){

                    Toast.makeText(context, context.getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                }
                else{

                    login();

                }
            }
        });

        btn_create_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createAccountDialog = new CreateAccountDialog();
                createAccountDialog.show(fm, "createAccountDialog");

            }
        });

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                forgotPasswordDialog = new ForgotPasswordDialog();
                forgotPasswordDialog.show(fm, "forgotPasswordDialog");

            }
        });
    }

    private void login(){

        if(!Utils.connectedToNetwork(context)){

            Toast.makeText(context, context.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }
        else{

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                new GetUserTask(email,password){

                                    @Override
                                    protected void onPreExecute(){

                                        loggingInDialog = new ProgressDialog(context);
                                        loggingInDialog.setIndeterminate(true);
                                        loggingInDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        loggingInDialog.setMessage(context.getString(R.string.logging_in));
                                        loggingInDialog.show();
                                    }

                                    @Override
                                    protected void onPostExecute(String userReturned){

                                        String[] names = Utils.separateWords(userReturned);

                                        first_name = names[0];
                                        last_name = names[1];

                                        if(dbManager.insertDataIntoUserDetailsTable(first_name,last_name)){

                                            Log.i("GetUserTask: ", "Stored user details in local DB");
                                        }
                                        else{

                                            Log.i("GetUserTask: ", "Error Storing user details in local DB");
                                        }
                                    }
                                }.execute();

                                String newToken = FirebaseInstanceId.getInstance().getToken();

                                new UpdateTokenTask(newToken,email,password){

                                    @Override
                                    protected void onPostExecute(Boolean result){

                                        if(result){

                                            Log.i("UpdateTokenTask: ", "Successfully updated device token");
                                        }
                                        else{

                                            Log.i("UpdateTokenTask: ", "Error updateding device token");
                                        }

                                        loggingInDialog.dismiss();

                                        Intent intent = new Intent(context, Home.class);

                                        startActivity(intent);
                                    }
                                }.execute();
                            }
                            else{

                                Toast.makeText(context, context.getString(R.string.email_password_error), Toast.LENGTH_SHORT).show();
                            }
                        }
            });
        }
    }

    @Override
    public void onCreateAccountDialogCreateClick(DialogFragment dialog, final String firstName, final String lastName, final String email, final String password) {

        if (Utils.connectedToNetwork(context)) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String token = Utils.getDeviceToken();

                                new UserRegistrationTask(token, firstName, lastName, email, password) {

                                    @Override
                                    protected void onPreExecute(){

                                        creatingAccountDialog = new ProgressDialog(context);
                                        creatingAccountDialog.setIndeterminate(true);
                                        creatingAccountDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        creatingAccountDialog.setMessage(context.getString(R.string.creating_account));
                                        creatingAccountDialog.show();
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {

                                        if (result) {

                                            creatingAccountDialog.dismiss();

                                            AccountCreatedDialog accountCreatedDialog = new AccountCreatedDialog();
                                            accountCreatedDialog.show(fm,"AccountCreatedDialog");
                                        }
                                        else {

                                            Toast.makeText(context, context.getString(R.string.failed_to_create_account), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }.execute();
                            }
                            else if(password.length() < 8) { // Google password complexity standards

                                Toast.makeText(context, context.getString(R.string.password_length_error), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, context.getString(R.string.account_already_exists_error), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else {

            Toast.makeText(context, context.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
