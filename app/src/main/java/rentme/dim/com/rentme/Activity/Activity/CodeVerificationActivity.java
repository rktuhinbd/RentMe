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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.UpdatePassword;
import rentme.dim.com.rentme.R;

public class CodeVerificationActivity extends AppCompatActivity {

    private Button buttonVerifyCode;
    private TextView textviewResendCode;
    private EditText editTextVerificationCode;
    private LinearLayout linearLayout_activity_code_verification;
    private String code;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        linearLayout_activity_code_verification = (LinearLayout) findViewById(R.id.linearLayout_activity_code_verification);
        textviewResendCode = (TextView) findViewById(R.id.textViewResendCode);
        editTextVerificationCode = (EditText) findViewById(R.id.editText_verification_code);
        buttonVerifyCode = (Button) findViewById(R.id.button_verify_code);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent i= getIntent();

        if(i != null){
            code = i.getStringExtra("ForgetPasswordCode");
            //Toast.makeText(this, ""+code, Toast.LENGTH_SHORT).show();
            //Log.e("Code",""+code);
        }

        buttonVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = editTextVerificationCode.getText().toString().trim();
                verifyPhoneNumberWithCode(code, inputCode);
                if(inputCode.equals(code)){
                    Intent intent = new Intent(CodeVerificationActivity.this, UpdatePassword.class);
                    startActivity(intent);
                }
                else{
                    ShowSnackbar("Invalid Code!!");
                    //Toast.makeText(CodeVerificationActivity.this, "Invalid Code!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                           //startActivity(new Intent(CodeVerificationActivity.this, UpdatePassword.class));
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
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void ShowSnackbar(String message){
        Snackbar snackbar = Snackbar.make(linearLayout_activity_code_verification, ""+message, Snackbar.LENGTH_LONG)
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
}