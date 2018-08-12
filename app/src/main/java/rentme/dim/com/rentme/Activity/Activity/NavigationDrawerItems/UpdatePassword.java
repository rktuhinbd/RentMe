package rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import rentme.dim.com.rentme.Activity.Activity.FirstActivity;
import rentme.dim.com.rentme.Activity.Data.ProtectUserPassword;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.Data.SignInInfo;
import rentme.dim.com.rentme.R;


public class UpdatePassword extends AppCompatActivity {

    private Button buttonBack, buttonUpdatePassword;
    private EditText editTextNewPassword1, editTextNewPassword2;
    private DatabaseReference databaseReferenceUsers, databaseReferenceSignInInfo;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private String phoneNumber;
    private String inputPassword, inputReTypePassword;
    private boolean withIntentValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        //buttonBack = (Button)findViewById(R.id.button_back_to_information);
        buttonUpdatePassword = (Button)findViewById(R.id.button_verify_old_password);

        sharedPreferencesClassObject = new SharedPreferencesClass(UpdatePassword.this);

        editTextNewPassword1 = (EditText)findViewById(R.id.editText_new_password1) ;
        editTextNewPassword2 = (EditText)findViewById(R.id.editText_new_password2) ;


        phoneNumber = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");

        if(phoneNumber == ""){
            withIntentValue = true;
            Intent data = getIntent();
            if(data != null){
                phoneNumber = data.getStringExtra("phone");
            }
        }

        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(UpdatePassword.this, AccountSettings.class);
//                //startActivity(i);
//            }
//        });

        buttonUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword();
                Toast.makeText(UpdatePassword.this, "Password Updated", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void UpdatePassword() {
        inputPassword = editTextNewPassword1.getText().toString();
        inputReTypePassword = editTextNewPassword2.getText().toString();
        if(inputPassword != inputReTypePassword){
            Log.e("phone",""+phoneNumber);
            final Query data = databaseReferenceSignInInfo.child(phoneNumber);
            Log.e("query result", data.toString());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        SignInInfo signInInfo = dataSnapshot.getValue(SignInInfo.class);
                        String password = signInInfo.getUserPassword();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("userPassword", ProtectUserPassword.SHA1(inputPassword));
                        databaseReferenceSignInInfo.child(phoneNumber).updateChildren(result);
                        ChangePage();
                    }
                    else{
                        Toast.makeText(UpdatePassword.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        else{
            Toast.makeText(UpdatePassword.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
        }
    }

    private void ChangePage(){
        if(withIntentValue){
            withIntentValue = false;
            Intent i = new Intent(UpdatePassword.this, FirstActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        else{
            Intent i = new Intent(UpdatePassword.this, AccountSettings.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        ChangePage();
    }
}
