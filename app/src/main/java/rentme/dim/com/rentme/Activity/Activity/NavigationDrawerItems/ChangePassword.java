package rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import rentme.dim.com.rentme.Activity.Data.ProtectUserPassword;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.Data.SignInInfo;
import rentme.dim.com.rentme.R;

public class ChangePassword extends AppCompatActivity {

    private Button buttonBack, buttonVerifyOldPassword;
    private LinearLayout linearLayout_activity_change_password;
    private EditText editTextPassword;
    private DatabaseReference databaseReferenceSignInInfo;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private String inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //buttonBack = (Button)findViewById(R.id.button_back_to_information);
        linearLayout_activity_change_password = (LinearLayout) findViewById(R.id.linearLayout_activity_change_password);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        buttonVerifyOldPassword = (Button)findViewById(R.id.button_verify_old_password);
        sharedPreferencesClassObject = new SharedPreferencesClass(ChangePassword.this);
        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(ChangePassword.this, AccountSettings.class);
//                startActivity(i);
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//            }
//        });

        buttonVerifyOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPassword();
            }
        });

    }

    private void CheckPassword() {
        inputPassword = editTextPassword.getText().toString();
        if(!inputPassword.isEmpty()){
            final String phoneNumber = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
            final Query data = databaseReferenceSignInInfo.child(phoneNumber);
            //Log.e("query result", data.toString());
            //Log.e("query result", phoneNumber.toString());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        SignInInfo signInInfo = dataSnapshot.getValue(SignInInfo.class);
                        String password = signInInfo.getUserPassword();
                        if(password.equals(ProtectUserPassword.SHA1(inputPassword))){
                            //Toast.makeText(ChangePassword.this, "Old Password Verified", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ChangePassword.this, UpdatePassword.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        else{
                            ShowSnackbar("Phone number is not registered");
                            //Toast.makeText(ChangePassword.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        ShowSnackbar("Please Try Again");
                        //Toast.makeText(ChangePassword.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        else{
            ShowSnackbar("Enter Correct Password");
            //Toast.makeText(ChangePassword.this, "Enter Correct Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowSnackbar(String message){
        Snackbar snackbar = Snackbar.make(linearLayout_activity_change_password, ""+message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(getIntent());
                    }
                });
        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChangePassword.this, AccountSettings.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
