package rentme.dim.com.rentme.Activity.Data;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class Notification extends Application {

    public static final String channel_1_id = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel Channel1 = new NotificationChannel(channel_1_id, "Channel1", NotificationManager.IMPORTANCE_HIGH);
            Channel1.enableVibration(true);
            Channel1.setLightColor(Color.GREEN);
            Channel1.enableLights(true);
            Channel1.setDescription("This is channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(Channel1);
        }
    }
}

