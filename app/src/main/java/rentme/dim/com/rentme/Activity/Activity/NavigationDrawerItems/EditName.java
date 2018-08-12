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

import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.Data.SignInInfo;
import rentme.dim.com.rentme.R;

public class EditName extends AppCompatActivity {

    private Button buttonBack, buttonSave;
    private EditText editTextName;
    private String newName, fullName, phoneNumber;
    private DatabaseReference  databaseReferenceSignInInfo;
    private SharedPreferencesClass sharedPreferencesClassObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        //buttonBack = (Button)findViewById(R.id.button_back_to_information);
        buttonSave = (Button)findViewById(R.id.button_save_info);
        editTextName = (EditText)findViewById(R.id.editText_name);

        sharedPreferencesClassObject = new SharedPreferencesClass(EditName.this);
        phoneNumber = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");

        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

        Intent i = getIntent();
        if(i != null){
            fullName = i.getStringExtra("FullName");
            editTextName.setText(fullName);
        }

//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(EditName.this, AccountSettings.class);
//                startActivity(i);
//            }
//        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = editTextName.getText().toString();
                editTextName.setText(newName);
                UpdateName();
                Toast.makeText(EditName.this, "Name has been updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditName.this, AccountSettings.class));
            }
        });
    }

    private void UpdateName() {
        if(editTextName != null){
            final Query data = databaseReferenceSignInInfo.child(phoneNumber);
            Log.e("query result", data.toString());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        SignInInfo signInInfo = dataSnapshot.getValue(SignInInfo.class);
                        //String name = signInInfo.getUserName();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("userName", newName);
                        databaseReferenceSignInInfo.child(phoneNumber).updateChildren(result);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserName", newName);
                    }
                    else{
                        Toast.makeText(EditName.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        else{
            Toast.makeText(EditName.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(EditName.this, AccountSettings.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}