package com.tonyblake.songmojo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements CreateAccountDialog.CreateAccountDialogInterface {

    private Context context;

    private EditText et_username, et_password;

    private Button btn_log_in, btn_create_account;

    private String username, password;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    private FragmentManager fm;

    private CreateAccountDialog createAccountDialog;

    public static ArrayList<User> users;

    private ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        context = this;

        et_username = (EditText) findViewById(R.id.et_username);

        et_password = (EditText) findViewById(R.id.et_password);

        btn_log_in = (Button) findViewById(R.id.btn_log_in);

        btn_create_account = (Button) findViewById(R.id.btn_create_account);

        username = "";

        password = "";

        database = FirebaseDatabase.getInstance();

        databaseRef = database.getReference().child("users");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        users = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fm = getSupportFragmentManager();

        btn_log_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                username = et_username.getText().toString();

                password = et_password.getText().toString();

                new LoginTask(users){

                    @Override
                    protected void onPreExecute(){

                        loginProgressDialog = new ProgressDialog(context);

                        loginProgressDialog.setProgressStyle(loginProgressDialog.STYLE_SPINNER);
                        loginProgressDialog.setIndeterminate(true);
                        loginProgressDialog.setMessage(context.getString(R.string.logging_in));
                        loginProgressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(User user){

                        loginProgressDialog.dismiss();

                        if(user != null){

                            login(user);
                        }
                        else if(!Utils.connectedToNetwork(context)){

                            Utils.showToastMessage(context, context.getString(R.string.no_network_connection));
                        }
                        else{

                            Utils.showToastMessage(context, context.getString(R.string.no_account_found));
                        }
                    }

                }.execute(username,password);
            }
        });

        btn_create_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createAccountDialog = new CreateAccountDialog();
                createAccountDialog.show(fm, "createAccountDialog");

            }
        });

        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue(User.class) != null) {

                    if(users.size() == 0){

                        for (DataSnapshot userID : dataSnapshot.getChildren()) {

                            User user = new User();

                            user.firstName = (String) userID.child("firstName").getValue();
                            user.lastName = (String) userID.child("lastName").getValue();
                            user.fullName = user.firstName + " " + user.lastName;
                            user.username = (String) userID.child("username").getValue();
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
    }

    private void login(User user){

        Intent intent = new Intent(this, Home.class);

        intent.putExtra("firstName", user.firstName);
        intent.putExtra("getRecentActivity", false);

        startActivity(intent);
    }

    @Override
    public void onCreateAccountDialogCreateClick(DialogFragment dialog, String firstName, String lastName, String username, String password) {

        if(Utils.connectedToNetwork(context)){

            User newUser = new User(firstName, lastName, username, password);

            databaseRef.child(username).setValue(newUser);

            Utils.showToastMessage(context, context.getString(R.string.account_created));
        }
        else{

            Utils.showToastMessage(context, context.getString(R.string.no_network_connection));
        }
    }
}
