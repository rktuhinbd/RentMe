package rentme.dim.com.rentme.Activity.Data;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripHistory {
    private String phoneNo;
    private String TripId;
    private DatabaseReference databaseReferenceRequests;
    private Requests request, req;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private ArrayList<Requests> historyList;


    public TripHistory() {
        req = new Requests();
        historyList = new ArrayList<Requests>();
    }

    public ArrayList<Requests> GetTripsData(Context context){
        try{
            sharedPreferencesClassObject = new SharedPreferencesClass(context);
            phoneNo = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
            databaseReferenceRequests = FirebaseDatabase.getInstance().getReference("Requests");
            Query data = databaseReferenceRequests;
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            request = snapshot.getValue(Requests.class);
                            if (request.getUserPhone().equals(phoneNo)) {
                                //history data added to arraylist
                                historyList.add(request);
                                //req = request;
                            }
                        }
                    }
                    //Log.e("History List", "" + historyLists.size());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        catch(Exception e){
            //Toast.makeText(this, "Data Not FoundException", Toast.LENGTH_SHORT).show();
        }
        Log.e("Req",""+req.getUserPhone());
        return historyList;
    }
}
