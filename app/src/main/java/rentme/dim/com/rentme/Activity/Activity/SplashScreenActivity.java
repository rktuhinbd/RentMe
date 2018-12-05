package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import rentme.dim.com.rentme.Activity.Data.CheckInternetConnectionClass;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.R;

public class SplashScreenActivity extends AppCompatActivity {

    private String userName;
    private ImageView imageView_spashScreen;
    private Animation animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_animation);

        imageView_spashScreen = (ImageView) findViewById(R.id.imageView_splash_screen);
        //imageView_spashScreen.startAnimation(animation);

        Thread splashScreenThread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(1500);

                    SharedPreferencesClass sharedPreferencesClassObject = new SharedPreferencesClass(SplashScreenActivity.this);
                    userName = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserName", "");
                    if(CheckInternetConnectionClass.IsInternetConnected(SplashScreenActivity.this)){
                        if(userName != ""){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(), FirstActivity.class));
                            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), InternetNotConnected.class));
                    }
                }
                catch(Exception e){
                    Log.e("Exception","error in splash screen activity");
                }
            }
        };

        splashScreenThread.start();
    }
}
