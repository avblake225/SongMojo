package com.tonyblake.songmojo;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    public static ArrayList<User> users;

    private ProgressDialog creatingAccountDialog;

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

        users = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fm = getSupportFragmentManager();

        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue(User.class) != null) {

                    if (users.size() == 0) {

                        for (DataSnapshot userID : dataSnapshot.getChildren()) {

                            User user = new User();

                            user.firstName = (String) userID.child("firstName").getValue();
                            user.lastName = (String) userID.child("lastName").getValue();
                            user.fullName = user.firstName + " " + user.lastName;
                            user.email = (String) userID.child("email").getValue();
                            user.password = (String) userID.child("password").getValue();

                            if (!users.contains(user)) {

                                users.add(user);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

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

                            intent.putExtra("user", getUser(email));

                            startActivity(intent);

                            if (!task.isSuccessful()) {

                                Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show();
                            }
                        }
            });
        }
    }

    private String getUser(String email){

        String user = "";

        for(User u: users){

            if(email.equals(u.email)){

                user = u.firstName + " " + u.lastName;
            }
        }

        return user;
    }

    @Override
    public void onCreateAccountDialogCreateClick(DialogFragment dialog, final String firstName, final String lastName, final String email, final String password) {

        if (Utils.connectedToNetwork(context)) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String fullname = firstName + " " + lastName;

                                User user = new User(firstName, lastName, fullname, email, password);

                                databaseRef.child(fullname).setValue(user);

                                String token = Utils.getDeviceToken();

                                new UserRegistrationTask(token, firstName, lastName) {

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
