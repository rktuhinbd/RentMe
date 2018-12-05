package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.UpdatePassword;
import rentme.dim.com.rentme.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button buttonCancel, buttonResetPassword, buttonSubmit;
    private TextView textViewresendCode,textview_forgot_password_header, textview_forgot_password_description;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    private FirebaseAuth firebaseAuth;
    private EditText numberEditText, editTextVerificationCode, editText_phone_forgot_password;
    private String forgetPasswordCode;
    private String phoneNumber, phone;
    private LinearLayout linearlayout_forgot_password_input_number, linearlayout_forgot_password;
    private CountryCodePicker countryCodePicker;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private DatabaseReference databaseReferenceSignInInfo;
    private List<String> phoneNoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        phoneNoList = new ArrayList<String>();
        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");
        textview_forgot_password_header = (TextView) findViewById(R.id.textview_forgot_password_header);
        textview_forgot_password_description = (TextView) findViewById(R.id.textview_forgot_password_description);
        linearlayout_forgot_password_input_number = (LinearLayout) findViewById(R.id.linearlayout_forgot_password_input_number);
        linearlayout_forgot_password = (LinearLayout) findViewById(R.id.linearlayout_forgot_password);
        editText_phone_forgot_password = (EditText) findViewById(R.id.editText_phone_forgot_password);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.country_code_picker_forgot_password);
        editTextVerificationCode = (EditText) findViewById(R.id.editText_verification_code);
        editTextVerificationCode.setVisibility(View.GONE);
        //buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonResetPassword = (Button) findViewById(R.id.button_reset_password);
        buttonSubmit = (Button) findViewById(R.id.button_submit);
        textViewresendCode = (TextView) findViewById(R.id.textView_Resend_Code);
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                phone = editText_phone_forgot_password.getText().toString().trim();
                if(countryCodePicker.getSelectedCountryCodeWithPlus().toString().equals("+880") && phone.length() == 11){
                    phoneNumber = phone.substring(1);
                    Log.e("Phone number", "Phone number: " + phone);
                }
                else{
                    phoneNumber = phone;
                }
                //SendVerificationCode(countryCodePicker.getSelectedCountryCodeWithPlus()+phoneNumber);
                AuthUserCheck(countryCodePicker.getSelectedCountryCodeWithPlus()+phoneNumber);
                Log.e("CodeInForgetPActivity",""+forgetPasswordCode);
                //Intent intent = new Intent(ForgotPasswordActivity.this, CodeVerificationActivity.class);
                //intent.putExtra("ForgetPasswordCode",forgetPasswordCode);
                //startActivity(intent);
//                numberEditText.setVisibility(View.GONE);

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = editTextVerificationCode.getText().toString().trim();
                verifyPhoneNumberWithCode(forgetPasswordCode, inputCode);
            }
        });

        textViewresendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerificationCode(phoneNumber);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //String inputCode = editTextVerificationCode.getText().toString().trim();
                //Log.e("phone auth code",""+phoneAuthCredential.getSmsCode());
                //Toast.makeText(ForgotPasswordActivity.this, "Code Sent"+forgetPasswordCode, Toast.LENGTH_SHORT).show();
                //Toast.makeText(ForgotPasswordActivity.this, ""+forgetPasswordCode, Toast.LENGTH_SHORT).show();
                //signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                forgetPasswordCode = s;
                //Log.e("CodeInSideActivity",""+forgetPasswordCode);
            }
        };
    }

    private void AuthUserCheck(final String phoneNo){
        try{
            Query data = databaseReferenceSignInInfo;
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        for(DataSnapshot datasnap: dataSnapshot.getChildren()){
                            if(datasnap.getKey().equals(phoneNo)){
                                phoneNoList.add(datasnap.getKey());
                                SendVerificationCode(phoneNo);
                            }
                        }
                        if(phoneNoList.size() == 0){
                            ShowSnackbar("Phone Number is not Registered");
                        }
                        else{
                            linearlayout_forgot_password_input_number.setVisibility(View.GONE);
                            buttonResetPassword.setVisibility(View.GONE);
                            textview_forgot_password_header.setText("Enter SMS Code");
                            textview_forgot_password_description.setText("Please check your phone, We've just sent you a verification code");
//                          buttonCancel.setVisibility(View.GONE);
                            editTextVerificationCode.setVisibility(View.VISIBLE);
                            buttonSubmit.setVisibility(View.VISIBLE);
                            textViewresendCode.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                            ShowSnackbar("Phone Number is not Registered");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch(Exception e){

        }
    }

    private void ShowSnackbar(String message){
        Snackbar snackbar = Snackbar.make(linearlayout_forgot_password, ""+message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(getIntent());
                    }
                });
        snackbar.show();
    }

    private void SendVerificationCode(String phoneNumber){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                callbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            //Log.e("Log","Successfull Code Verification");
                            Intent i = new Intent(ForgotPasswordActivity.this, UpdatePassword.class);
                            i.putExtra("phone",countryCodePicker.getSelectedCountryCodeWithPlus().toString()+phoneNumber);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
       //Log.e("verifyPhoneNumber",""+ verificationId);
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
        overridePendingTransition(R.anim.slide_down_upper, R.anim.slide_down_lower);
    }
}


