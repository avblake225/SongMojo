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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements CreateAccountDialog.CreateAccountDialogInterface{

    private Context context;

    private EditText et_username, et_password;

    private Button btn_log_in, btn_create_account;

    private String username, password;

    private FirebaseDatabase database;

    private DatabaseReference databaseRef;

    private FragmentManager fm;

    private CreateAccountDialog createAccountDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        context = this;

        et_username = (EditText)findViewById(R.id.et_username);

        et_password = (EditText)findViewById(R.id.et_password);

        btn_log_in = (Button)findViewById(R.id.btn_log_in);

        btn_create_account = (Button)findViewById(R.id.btn_create_account);

        username = "";

        password = "";

        database = FirebaseDatabase.getInstance();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume(){
        super.onResume();

        fm = getSupportFragmentManager();

        btn_create_account.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createAccountDialog = new CreateAccountDialog();
                createAccountDialog.show(fm, "createAccountDialog");

            }
        });

        // Read from the database
        if(databaseRef != null){

            databaseRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String user = dataSnapshot.getValue(String.class);

                    int dummy = 0;

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });
        }
    }

    @Override
    public void onCreateAccountDialogSearchClick(DialogFragment dialog, String username, String password) {

        databaseRef = database.getReference(username);

        databaseRef.setValue(password);
    }
}
