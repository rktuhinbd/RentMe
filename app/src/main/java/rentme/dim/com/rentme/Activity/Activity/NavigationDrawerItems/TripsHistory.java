package rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import rentme.dim.com.rentme.Activity.Activity.HomeActivity;
import rentme.dim.com.rentme.Activity.Data.Requests;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.Data.TripHistory;
import rentme.dim.com.rentme.R;


public class TripsHistory extends AppCompatActivity {

    private RecyclerView tRecyclerView;
    private HistoryAdapter tAdapter;
    private RecyclerView.LayoutManager tLayoutManager;
    private ArrayList<Requests> historyLists = new ArrayList<>();

    private DatabaseReference databaseReferenceTrips, databaseReferenceRequests;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private String phoneNo;
    private TripHistory trip;
    private Requests request, req;
    private boolean removeItemCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        req = null;

        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference("Requests");
        databaseReferenceTrips = FirebaseDatabase.getInstance().getReference("Trips");
        sharedPreferencesClassObject = new SharedPreferencesClass(TripsHistory.this);
        phoneNo = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");

        TripDetails();
    }

    public void RemoveItem(int position){
        removeItemCalled = true;
        historyLists.remove(position);
//        tRecyclerView.removeViewAt(position);
//        tAdapter.notifyItemRemoved(position);
//        tAdapter.notifyItemRangeChanged(position, historyLists.size());
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
    }

    private void TripDetails() {
        try{
            Query data = databaseReferenceRequests;
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            request = snapshot.getValue(Requests.class);
                            if(request.getUserPhone().equals(phoneNo)){
                                //history data added to arraylist
                                if(!removeItemCalled){
                                    Log.e("History List","Inserted");
                                    historyLists.add(request);
                                }
                            }
                        }
                    }
                    Log.e("History List",""+historyLists.size());

                    tRecyclerView = findViewById(R.id.recyclerView);
                    tRecyclerView.setHasFixedSize(true);
                    tLayoutManager = new LinearLayoutManager(TripsHistory.this);
                    tAdapter = new HistoryAdapter(historyLists);

                    tRecyclerView.setLayoutManager(tLayoutManager);
                    tRecyclerView.setAdapter(tAdapter);

                    tAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Toast.makeText(TripsHistory.this, ""+ position, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDeleteClick(int position) {

                            if(HoursDifference(historyLists.get(position)) >=4 ){
                                Requests requestData = historyLists.get(position);
                                RemoveItem(position);
                                removeFromDatabase(requestData, position);
                            }
                            else if(HoursDifference(historyLists.get(position)) <4 && HoursDifference(historyLists.get(position)) >=0){
                                Toast.makeText(TripsHistory.this, "You Cannot cancel trip request within 4 hours before starting trip", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        catch(Exception e){
            Toast.makeText(this, "Please Enter Phone No or Password", Toast.LENGTH_SHORT).show();
        }
    }

    private long HoursDifference(Requests requestItem){
        long hoursDiff = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm");
        try{
            TimeZone timeZone = TimeZone.getTimeZone("GMT+6");
            Calendar calendar = Calendar.getInstance(timeZone);

            Date currentDate = simpleDateFormat.parse(calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            Date requestedDate = simpleDateFormat.parse(requestItem.getRequestDate());

            long differenceBetweenDates = requestedDate.getTime() - currentDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            hoursDiff = differenceBetweenDates / hoursInMilli;
            differenceBetweenDates = differenceBetweenDates % hoursInMilli;

            Log.e("Hours Differenc",""+hoursDiff+"-----"+differenceBetweenDates);
        }
        catch(Exception e){}
        return hoursDiff;
    }

      public void removeFromDatabase(Requests requestItem, final int position){
        final Query data = databaseReferenceRequests.child(requestItem.getRequestId().toString());
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataSnapshot.getRef().removeValue();
                    //RemoveItem(position);
                }
                else{
                    Toast.makeText(TripsHistory.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TripsHistory.this, HomeActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}