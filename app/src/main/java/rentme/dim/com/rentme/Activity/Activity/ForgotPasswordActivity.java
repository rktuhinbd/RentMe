package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.concurrent.TimeUnit;

import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.UpdatePassword;
import rentme.dim.com.rentme.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button buttonCancel, buttonResetPassword, buttonSubmit;
    private TextView textViewresendCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callback;
    private FirebaseAuth firebaseAuth;
    private EditText numberEditText, editTextVerificationCode;
    private String forgetPasswordCode;
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        numberEditText = (EditText) findViewById(R.id.editText_registered_phone_number);
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
                phoneNumber = numberEditText.getText().toString().trim();
                SendVerificationCode("+88"+phoneNumber);
                Log.e("CodeInForgetPActivity",""+forgetPasswordCode);
                //Intent intent = new Intent(ForgotPasswordActivity.this, CodeVerificationActivity.class);
                //intent.putExtra("ForgetPasswordCode",forgetPasswordCode);
                //startActivity(intent);
                numberEditText.setVisibility(View.GONE);
                buttonResetPassword.setVisibility(View.GONE);
//                buttonCancel.setVisibility(View.GONE);
                editTextVerificationCode.setVisibility(View.VISIBLE);
                buttonSubmit.setVisibility(View.VISIBLE);
                textViewresendCode.setVisibility(View.VISIBLE);

            }
        });
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentSignIn = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
//                startActivity(intentSignIn);
//            }
//        });

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
                Log.e("phone auth code",""+phoneAuthCredential.getSmsCode());
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
                Log.e("CodeInSideActivity",""+forgetPasswordCode);
            }
        };
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
                            Log.e("Log","Successfull Code Verification");
                            Intent i = new Intent(ForgotPasswordActivity.this, UpdatePassword.class);
                            i.putExtra("phone","+88"+phoneNumber);
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
       Log.e("verifyPhoneNumber",""+ verificationId);
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


