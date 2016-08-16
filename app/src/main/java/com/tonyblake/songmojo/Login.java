package com.tonyblake.songmojo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements CreateAccountDialog.CreateAccountDialogInterface {

    private Context context;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    private EditText et_email, et_password;

    private Button btn_log_in, btn_create_account;

    private String email, password;

    private FragmentManager fm;

    private CreateAccountDialog createAccountDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        context = this;

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        databaseRef = database.getReference().child("users");

        et_email = (EditText) findViewById(R.id.et_email);

        et_password = (EditText) findViewById(R.id.et_password);

        btn_log_in = (Button) findViewById(R.id.btn_log_in);

        btn_create_account = (Button) findViewById(R.id.btn_create_account);

        email = "";

        password = "";

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

                if("".equals(email) | "".equals(password)){

                    Toast.makeText(context, context.getString(R.string.please_enter_email_and_password), Toast.LENGTH_SHORT).show();
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

                            Intent intent = new Intent(context, Home.class);

                            intent.putExtra("user", "some user");

                            startActivity(intent);

                            if (!task.isSuccessful()) {

                                Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show();
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

                            if (!task.isSuccessful()) {

                                Toast.makeText(context, "failed to create account", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                Toast.makeText(context, context.getString(R.string.account_created), Toast.LENGTH_SHORT).show();

                                String fullname = firstName + " " + lastName;

                                User user = new User(firstName, lastName, fullname);

                                databaseRef.child(fullname).setValue(user);
                            }
                        }
                    });

        } else {

            Toast.makeText(context, context.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
