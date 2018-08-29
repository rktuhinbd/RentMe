package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rilixtech.CountryCodePicker;

import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.TermsAndConditions;
import rentme.dim.com.rentme.Activity.Data.ProtectUserPassword;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.Data.SignInInfo;
import rentme.dim.com.rentme.R;

public class RegistrationActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReferenceUsers, databaseReferenceSignInInfo;

    private Button button_register;
    private TextView terms_and_conditions;
    private EditText editText_name, editText_phone, editText_email, editText_password;
    private String name, email, phone, password, encPassword;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private CountryCodePicker countryCodePicker;
    private String phoneNo;
    private int GALLERY_INTENT = 100;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPreferencesClassObject = new SharedPreferencesClass(RegistrationActivity.this);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.country_code_picker);
        button_register  = (Button) findViewById(R.id.button_register);
        terms_and_conditions  = (TextView) findViewById(R.id.textView_terms_and_conditions);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

        checkBox = (CheckBox)findViewById(R.id.checkBox);

        terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, TermsAndConditions.class));
            }
        });

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_phone = (EditText) findViewById(R.id.editText_phone);
        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_password = (EditText) findViewById(R.id.editText_password);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");
    }

    private void RegisterUser() {
        phoneNo = editText_phone.getText().toString().trim();
        name = editText_name.getText().toString().trim();
        email = editText_email.getText().toString().trim();
        password = editText_password.getText().toString().trim();

        Log.e("Email", email);
        Log.e("Password", password);

        encPassword = ProtectUserPassword.SHA1(password);

        if (editText_name.getText().length() < 5) {
            Toast.makeText(getApplicationContext(), "Your name should be at least 4 characters or more", Toast.LENGTH_LONG).show();
        } else if (editText_phone.getText().length() < 10 || editText_phone.getText().length() > 11) {
            Toast.makeText(getApplicationContext(), "Phone number length must be 11", Toast.LENGTH_LONG).show();
        } else if (editText_password.getText().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters or more", Toast.LENGTH_LONG).show();
        }
        else if (!editText_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            editText_email.setError("Invalid Email Address");
        }
        else if(checkBox.isChecked() == false){
            Toast.makeText(getApplicationContext(), "You need to accept the Terms and Conditions", Toast.LENGTH_LONG).show();
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if(phoneNo.length() == 11){
                            phone = phoneNo.substring(1);
                            Log.e("Phone number", "Phone number: " + phone);
                        }
                        else{
                            phone = phoneNo;
                            Log.e("Phone number", "Phone number: " + phone);
                        }
                        String phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus().toString()+phone;
                        Toast.makeText(RegistrationActivity.this, "User Registration Successfull", Toast.LENGTH_SHORT).show();
                        sharedPreferencesClassObject = new SharedPreferencesClass(RegistrationActivity.this);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserName", name);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserEmail", email);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserPhone", phoneNumber);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserId", firebaseUser.getUid().toString());
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserImageAngle", "0");
                        UploadDefaultImage(phoneNumber);
                        SignInInfo signInInfo = new SignInInfo(firebaseUser.getUid().toString(), encPassword, name, email, "0");
                        databaseReferenceSignInInfo.child(phoneNumber).setValue(signInInfo);

                        startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));

                    } else {
                        Toast.makeText(RegistrationActivity.this, "User Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void UploadDefaultImage(String phoneNumber){
        StorageReference filepath = storageReference.child("Photos").child(phoneNumber);
        Uri uri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/drawable/human_face");
        Log.e("URI data", ""+uri);
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    public void LogOut(View view){
        startActivity(new Intent(RegistrationActivity.this, FirstActivity.class));
        SharedPreferencesClass sharedPreferencesClassObject = new SharedPreferencesClass(RegistrationActivity.this);
        sharedPreferencesClassObject.setSharedPreferences("SignInData", "UserName", "");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(RegistrationActivity.this, FirstActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
