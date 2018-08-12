package rentme.dim.com.rentme.Activity.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetConnectionClass extends BroadcastReceiver {

    public static boolean IsInternetConnected(Context context){
        boolean internetConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)||
        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            internetConnected = true;
        }
        else{
            internetConnected = false;
        }
        return internetConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
            int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
            if(InNetworkAvailable(context, type)){
                return;
            }
            else{
                //Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            }
    }

    public static boolean InNetworkAvailable(Context context, int[] typeNetworks){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for(int typeNetwork: typeNetworks){
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(typeNetwork);
                if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        catch(Exception e){
            return false;
        }
        return false;
    }
}
