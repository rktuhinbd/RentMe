package rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rentme.dim.com.rentme.Activity.Activity.FirstActivity;
import rentme.dim.com.rentme.Activity.Activity.SplashScreenActivity;
import rentme.dim.com.rentme.R;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SplashScreenActivity.class));
    }
}
