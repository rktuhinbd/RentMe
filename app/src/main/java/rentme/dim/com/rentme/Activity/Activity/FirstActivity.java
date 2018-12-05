package rentme.dim.com.rentme.Activity.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import rentme.dim.com.rentme.R;

public class FirstActivity extends AppCompatActivity {

    private Button buttonCreateAccount;
    private Button buttonSignIn;
    private LinearLayout linearLayout_first;
    private TextView textView_register_here;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //buttonCreateAccount = (Button) findViewById(R.id.button_create_account);
        buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        linearLayout_first = (LinearLayout) findViewById(R.id.linearLayout_first);
        textView_register_here = (TextView) findViewById(R.id.textView_register_here);

//        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentToRegistrationActivity = new Intent(FirstActivity.this, RegistrationActivity.class);
//                startActivity(intentToRegistrationActivity);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//            });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSignInActivity = new Intent(FirstActivity.this, SignInActivity.class);
                startActivity(intentToSignInActivity);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            });

        textView_register_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrationActivityIntent = new Intent(FirstActivity.this, RegistrationActivity.class);
                startActivity(registrationActivityIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        }



        public void revealButton(){
        int cx = buttonSignIn.getWidth()/2;
        int cy = buttonSignIn.getHeight()/2;

        float finalRadius = Math.max(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
            //Log.e("final Radius",""+finalRadius);
            //Log.e("cx",""+cx);
            //Log.e("cy",""+cy);
        if(Build.VERSION.SDK_INT >= 21){
            Animator revealButtonAnimation = ViewAnimationUtils.createCircularReveal(linearLayout_first, getWindowManager().getDefaultDisplay().getWidth()-cx, getWindowManager().getDefaultDisplay().getHeight()-2*cy, 0, finalRadius);
            revealButtonAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Intent intentToSignInActivity = new Intent(FirstActivity.this, SignInActivity.class);
                    startActivity(intentToSignInActivity);
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            revealButtonAnimation.start();
        }
        }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
        System.exit(0);
    }
}
