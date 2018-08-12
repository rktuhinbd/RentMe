package rentme.dim.com.rentme.Activity.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import rentme.dim.com.rentme.R;

public class InternetNotConnected extends AppCompatActivity {

    private TextView textViewOpenInternetSettings, refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_not_connected);

        textViewOpenInternetSettings = (TextView) findViewById(R.id.textView_redirect_to_data_settings);
        refresh = (TextView)findViewById(R.id.textView_refresh);

        textViewOpenInternetSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open Cellular data Settings
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InternetNotConnected.this, SplashScreenActivity.class));
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
        startActivity(new Intent(InternetNotConnected.this, SplashScreenActivity.class));
    }
}
