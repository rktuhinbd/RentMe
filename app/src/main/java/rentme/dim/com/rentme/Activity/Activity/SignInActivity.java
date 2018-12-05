package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.CountryCodePicker;

import rentme.dim.com.rentme.Activity.Data.CheckInternetConnectionClass;
import rentme.dim.com.rentme.Activity.Data.ProtectUserPassword;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.Data.SignInInfo;
import rentme.dim.com.rentme.R;

public class SignInActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private TextView textViewForgotPassword;
    private EditText editText_phoneNo, editText_password;
    private DatabaseReference databaseReferenceSignInInfo;
    private SignInInfo signInInfo;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private String phone, password, encPassword;
    private CountryCodePicker countryCodePicker;
    private String userPhone;
    private LinearLayout linearLayout_sign_in;
    private ProgressBar progressBar_sign_in;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        countryCodePicker = (CountryCodePicker) findViewById(R.id.country_code_picker);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        editText_phoneNo = (EditText) findViewById(R.id.editText_phone);
        editText_password = (EditText) findViewById(R.id.passwordInput);
        progressBar_sign_in = (ProgressBar) findViewById(R.id.progressbar_sign_in);
        linearLayout_sign_in = (LinearLayout) findViewById(R.id.linearLayout_sign_in);

        sharedPreferencesClassObject = new SharedPreferencesClass(SignInActivity.this);
        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectionClass.IsInternetConnected(SignInActivity.this)) {
                    RemoveSignInButton();
                    VerifyUser();
                }
            }
        });
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentForgotPassword = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intentForgotPassword);
                overridePendingTransition(R.anim.slide_up_lower, R.anim.slide_up_upper);
            }
        });
    }

    private void VerifyUser() {
        String phoneCode = countryCodePicker.getSelectedCountryCodeWithPlus();
        String phoneNo = editText_phoneNo.getText().toString().trim();
        if( phoneCode.equals("+880") && phoneNo.length() == 11){
            phone = phoneNo.substring(1);
            //Log.e("Phone number", "Phone number: " + phone);
        }
        else{
            phone = phoneNo;
        }
        password = editText_password.getText().toString().trim();
        encPassword = ProtectUserPassword.SHA1(password);

        userPhone = phoneCode+phone;
        try{
            if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)){
                ShowSnackbar("Phone number and password field can't be empty");
                //Toast.makeText(SignInActivity.this, "Phone number and password field can't be empty", Toast.LENGTH_SHORT).show();
                ShowSignInButton();
            }
            else{
                Query data = databaseReferenceSignInInfo.child(userPhone);
                //Toast.makeText(this, ""+data.toString(), Toast.LENGTH_SHORT).show();
                //Log.e("query result", data.toString());
                data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            //Toast.makeText(SignInActivity.this, ""+dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                            SignInInfo signInInfo = dataSnapshot.getValue(SignInInfo.class);
                            if(signInInfo.getUserPassword().equals(encPassword)){
                                sharedPreferencesClassObject.setSharedPreferences("UserData", "UserName", signInInfo.getUserName());
                                sharedPreferencesClassObject.setSharedPreferences("UserData", "UserEmail", signInInfo.getUserEmail());
                                sharedPreferencesClassObject.setSharedPreferences("UserData", "UserId", signInInfo.getUserId());
                                sharedPreferencesClassObject.setSharedPreferences("UserData", "UserPhone", userPhone);
                                sharedPreferencesClassObject.setSharedPreferences("UserData", "UserImageAngle", signInInfo.getUserImageAngle());
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                            else{
                                ShowSignInButton();
                                ShowSnackbar("Wrong phone number or password, please enter your credentials correctly");
                            }
                        }
                        else{
                            ShowSignInButton();
                            ShowSnackbar("Phone number is not registered");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ShowSignInButton();
                        //Log.e("onCancelled", "onCancelled", databaseError.toException());
                    }
                });
            }
        }
        catch(Exception e){
            ShowSignInButton();
            ShowSnackbar("Please Enter Phone No or Password");
        }
    }

    private void ShowSnackbar(String message){
        final Snackbar snackbar = Snackbar.make(linearLayout_sign_in, ""+message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //startActivity(getIntent());
                    }
                });
        snackbar.show();
    }

    private void RemoveSignInButton(){
        progressBar_sign_in.setVisibility(View.VISIBLE);
        buttonSignIn.setVisibility(View.GONE);
        textViewForgotPassword.setVisibility(View.GONE);
        progressBar_sign_in.setVisibility(View.VISIBLE);
    }
    private void ShowSignInButton(){
        progressBar_sign_in.setVisibility(View.GONE);
        buttonSignIn.setVisibility(View.VISIBLE);
        textViewForgotPassword.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInActivity.this, FirstActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
