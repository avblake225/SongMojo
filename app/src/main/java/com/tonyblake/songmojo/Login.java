package com.tonyblake.songmojo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements CreateAccountDialog.CreateAccountDialogInterface {

    private Context context;

    private EditText et_username, et_password;

    private Button btn_log_in, btn_create_account;

    private String username, password;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    private FragmentManager fm;

    private CreateAccountDialog createAccountDialog;

    private User user;

    private ArrayList<User> users;

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

        databaseRef = database.getReference();

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

                if(dataSnapshot.getValue(User.class) != null){

                    for (DataSnapshot userID: dataSnapshot.getChildren()) {

                        user = new User();

                        user.firstName = (String) userID.child("firstName").getValue();
                        user.lastName = (String) userID.child("lastName").getValue();
                        user.username = (String) userID.child("username").getValue();
                        user.password = (String) userID.child("password").getValue();

                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

    @Override
    public void onCreateAccountDialogSearchClick(DialogFragment dialog, String firstName, String lastName, String username, String password) {

        User user = new User(firstName, lastName, username, password);

        Map<String, User> map = new HashMap<>();

        map.put(username, user);

        databaseRef.setValue(map);

        Toast.makeText(context, context.getString(R.string.account_created), Toast.LENGTH_LONG);
    }
}
