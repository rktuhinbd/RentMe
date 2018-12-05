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

import java.util.HashMap;

import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.R;

public class ChangeEmail extends AppCompatActivity {

    private Button buttonBack, buttonSave;
    private EditText editTextEmail;
    private String email, phoneNumber;
    private LinearLayout linearLayout_activity_change_email;
    private DatabaseReference databaseReferenceSignInInfo;
    private SharedPreferencesClass sharedPreferencesClassObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        //buttonBack = (Button)findViewById(R.id.button_back_to_information);
        linearLayout_activity_change_email = (LinearLayout) findViewById(R.id.linearLayout_activity_change_email);
        buttonSave = (Button)findViewById(R.id.button_save_info);
        editTextEmail = (EditText)findViewById(R.id.editText_email);

        sharedPreferencesClassObject = new SharedPreferencesClass(ChangeEmail.this);
        phoneNumber = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

        Intent i = getIntent();
        if(i != null){
            email = i.getStringExtra("Email");
            editTextEmail.setText(email);
        }

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(ChangeEmail.this, AccountSettings.class);
//                startActivity(i);
//            }
//        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                UpdateName();
                //Toast.makeText(ChangeEmail.this, "Information's saved", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ChangeEmail.this, AccountSettings.class));
            }
        });
    }

    private void UpdateName() {
        if(email != null){
            final Query data = databaseReferenceSignInInfo.child(phoneNumber);
            //Log.e("query result", data.toString());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        //SignInInfo signInInfo = dataSnapshot.getValue(SignInInfo.class);
                        //String name = signInInfo.getUserName();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("userEmail", email);
                        databaseReferenceSignInInfo.child(phoneNumber).updateChildren(result);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserEmail", email);
                    }
                    else{
                        ShowSnackbar("Phone number is not registered");
                        //Toast.makeText(ChangeEmail.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        else{
            ShowSnackbar("Email Not Found");
            //Toast.makeText(ChangeEmail.this, "Email Not Found", Toast.LENGTH_SHORT).show();
        }
    }
    private void ShowSnackbar(String message){
        Snackbar snackbar = Snackbar.make(linearLayout_activity_change_email, ""+message, Snackbar.LENGTH_LONG)
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
        Intent i = new Intent(ChangeEmail.this, AccountSettings.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
