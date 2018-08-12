package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import rentme.dim.com.rentme.R;

public class GpsOff extends AppCompatActivity {

    private TextView textViewOpenGpsSettings, refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_off);

        textViewOpenGpsSettings = (TextView) findViewById(R.id.textView_redirect_to_gps_settings);

        refresh = (TextView)findViewById(R.id.textView_refresh);
        textViewOpenGpsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open GPS settings
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GpsOff.this, SplashScreenActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(GpsOff.this, HomeActivity.class));
    }
}
