package rentme.dim.com.rentme.Activity.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.AccountSettings;
import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.ErrorActivity;
import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.TermsAndConditions;
import rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems.TripsHistory;
import rentme.dim.com.rentme.Activity.Data.CheckInternetConnectionClass;
import rentme.dim.com.rentme.Activity.Data.CircleImageTransformation;
import rentme.dim.com.rentme.Activity.Data.DirectionsParser;
import rentme.dim.com.rentme.Activity.Data.PlaceAutoCompleteAdapter;
import rentme.dim.com.rentme.Activity.Data.Requests;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.Activity.DatePickerFragment;
import rentme.dim.com.rentme.R;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private GoogleMap googleMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean location_permission_granted = false;
    private static final int REQUEST_CODE = 1234;
    private static final int PLACE_PICKER_REQUEST = 1;
    private Marker marker, marker_source, marker_destination;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static float MAP_ZOOM = 17f;
    private GoogleApiClient googleApiClient;
    //private static final LatLngBounds latLngBounds = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private static final LatLngBounds latLngBounds = new LatLngBounds(new LatLng(20.86382, 88.15638), new LatLng(26.33338, 92.30153));
    private static Location current_location;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    private RelativeLayout relativeLayout_source, relativeLayout_destination, relativeLayout_main;
    private LinearLayout relativeLayout_bottom_sheet, linearLayout_start, linear_layout_bottomsheet;
    private TextView textView_bill, textView_route, textView_name, textView_phone;
    private NavigationView navigationView;
    private ImageView imageView_car_four_seater, imageView_car_micro, cancel_source, cancel_destination;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private int height, width;
    private BroadcastReceiver broadcastReceiver;
    private String addressSrc, addressDes, url;
    private SharedPreferencesClass sharedPreferencesDestinationLatlng;
    private ArrayList<Requests> tripHistoryList;


    private AutoCompleteTextView editText_search_source, editText_search_destination;
    private ImageView imageView_gps, imageView_info, imageView_place_picker, imageView_user_photo, imageView_pin;

    private String input_text_source, input_text_destination, phoneNo;
    private List<Address> list_source = new ArrayList<>();
    private List<Address> list_destination = new ArrayList<>();
    ;
    private android.location.Address address_source, address_destination;
    private Place placeSource, placeDestination;
    private Location source = new Location("A");
    private Location destination = new Location("B");
    private LatLng sourceLatlng = null, destinationLatlng = null;
    private LatLng latlngData, direction_start, direction_end;
    private Fragment fragment;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private Geocoder geocoder;
    private List<Address> addressSource;
    private List<Address> addressDestination;
    private String startLocation, endLocation;
    private String requestId;
    private ArrayList<LatLng> points;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private Button button_drawer, button_done;

    private DatabaseReference databaseReferenceRequests, databaseReferenceTrips;
    private StorageReference storageReference;

    private int count, editText, firstLaunch;
    private float dist, costCar, costMicro;
    private String showCostFourSeater, showCostMicro;
    private String districtSource, districtDestination;
    private boolean isFoursSeaterSelected = true;
    private String carType = "Four Seater";
    private String currentDateString = "";
    private int year, month, day, hour, minute;
    private String requestDate, name, phone;
    private String cost;
    private Button dialogButton, dialogButtonFromList, dialogButtonFromMap, doneButton;
    private Animation animationFadeIn;
    private boolean showCostPage = false;
    private int clicked = 0;
    private int autoCompleteEditTextClicked = 2, autoCompleteEditTextClicked1 = 0;
    private BottomSheetDialog bottomSheetDialog;
    private Marker marker_source_pin, marker_destination_pin;
    private StorageReference storageRef;
    private DatabaseReference databaseReferenceSignInInfo;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try{
            latlngData = new LatLng(0.0, 0.0);
            firstLaunch = 1;

            polylines = new ArrayList<>();
            removePolyLine();

            TimeZone timeZone = TimeZone.getTimeZone("GMT+6");
            Calendar calendar = Calendar.getInstance(timeZone);

            Toast.makeText(this, calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();

            storageRef = FirebaseStorage.getInstance().getReference();
            sharedPreferencesDestinationLatlng = new SharedPreferencesClass(HomeActivity.this);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            animationFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    relativeLayout_source = (RelativeLayout) findViewById(R.id.relativelayout_search_source);
                    relativeLayout_source.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    relativeLayout_source.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            drawerLayout.startAnimation(animationFadeIn);

            Button buttonNavigation = (Button) findViewById(R.id.btn);
            button_done = (Button) findViewById(R.id.button_done);
            buttonNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
            doneButton = (Button) findViewById(R.id.button_done);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DoneButton();
                }
            });

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


            sharedPreferencesClassObject = new SharedPreferencesClass(HomeActivity.this);
            databaseReferenceRequests = FirebaseDatabase.getInstance().getReference("Requests");
            databaseReferenceTrips = FirebaseDatabase.getInstance().getReference("TripsDetails");


            linearLayout_start = (LinearLayout) findViewById(R.id.linearLayout_start);
            linear_layout_bottomsheet = (LinearLayout) findViewById(R.id.linear_layout_bottomsheet);

            relativeLayout_destination = (RelativeLayout) findViewById(R.id.relativelayout_search_destination);
            relativeLayout_main = (RelativeLayout) findViewById(R.id.relative_layout_main);
            relativeLayout_bottom_sheet = (LinearLayout) findViewById(R.id.bottom_sheet);
            imageView_car_four_seater = (ImageView) findViewById(R.id.car_four_seater);
            imageView_car_micro = (ImageView) findViewById(R.id.car_micro);
            imageView_pin = (ImageView) findViewById(R.id.imageView_pin);
            cancel_source = (ImageView) findViewById(R.id.cancel_source);
            cancel_destination = (ImageView) findViewById(R.id.cancel_destination);
            height = getWindowManager().getDefaultDisplay().getHeight();
            //relativeLayout_destination.setVisibility(View.GONE);
            linearLayout_start.setVisibility(View.GONE);


            View headerView = navigationView.getHeaderView(0);
            imageView_user_photo = (ImageView) headerView.findViewById(R.id.imageView_user_photo);
            textView_name = (TextView) headerView.findViewById(R.id.textView_Name);
            textView_phone = (TextView) headerView.findViewById(R.id.textView_Phone);
            imageView_user_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");
            name = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserName", "");
            phone = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
            textView_name.setText(name);
            textView_phone.setText(phone);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            storageReference = FirebaseStorage.getInstance().getReference();
            String imageName = phone;
            //if(storageReference.child("Photos").child(imageName).getDownloadUrl() != null){
            try {
                storageReference.child("Photos").child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String angle = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserImageAngle", "");
                        int ang = Integer.valueOf(angle);
                        //Toast.makeText(HomeActivity.this, ""+ang, Toast.LENGTH_SHORT).show();
                        if (ang == 270) {
                            Picasso.get()
                                    .load(uri)
                                    .rotate(270)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_photo);
                        } else if (ang == 180) {
                            Picasso.get()
                                    .load(uri)
                                    .rotate(180)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_photo);
                        } else if (ang == 0) {
                            Picasso.get()
                                    .load(uri)
                                    .rotate(0)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_photo);
                        } else if (ang == 90) {
                            Picasso.get()
                                    .load(uri)
                                    .rotate(90)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_photo);
                        } else {
                            Picasso.get()
                                    .load(uri)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_photo);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.get()
                                .load(R.drawable.human_face)
                                .resize(100, 120)
                                .transform(new CircleImageTransformation())
                                .centerCrop()
                                .into(imageView_user_photo);
                    }
                });
            } catch (Exception e) {
                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                Log.e("photos url", "" + storageReference.child("Photos").child(imageName).getDownloadUrl());
                imageView_user_photo.setBackgroundResource(R.drawable.car_logo);
            }


            textView_bill = (TextView) findViewById(R.id.textView_bill);
            //textView_route = (TextView) findViewById(R.id.textView_route);
            editText_search_source = (AutoCompleteTextView) findViewById(R.id.editText_search_source);
            editText_search_destination = (AutoCompleteTextView) findViewById(R.id.editText_search_destination);
            editText_search_source.setSingleLine();
            editText_search_destination.setSingleLine();


            InputSearch();
            count = 0;
            editText = 0;

            get_Permission_For_Location();
            if (GetLocationStatus()) {
                Get_current_location();
            }
        }
        catch(Exception onCreateException){
            startActivity(new Intent(this, ErrorActivity.class));
        }

    }

    public void InputSearch() {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        editText_search_source.setOnItemClickListener(onItemClickListener);
        editText_search_destination.setOnItemClickListener(onItemClickListener);

        AutocompleteFilter filterBDMAP = new AutocompleteFilter.Builder().setCountry("BD").build();

        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, googleApiClient, latLngBounds, filterBDMAP);


        editText_search_source.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                if (id == EditorInfo.IME_ACTION_SEARCH
                        || id == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Geolocate(1);
                    editText = 1;
                }
                return false;
            }
        });
        editText_search_destination.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH
                        || id == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Geolocate(2);
                    editText = 2;
                }
                return false;
            }
        });
    }

    public void Show(View view) {
        Toast.makeText(this, "Button CLicked", Toast.LENGTH_SHORT).show();
    }

    public boolean GetLocationStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gpsEnabled && !networkEnabled) {
            Toast.makeText(this, "Please Enable Location Services or Data Connection", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void get_Permission_For_Location() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location_permission_granted = true;
                Log.e("get_Permission_Location", "Init_map called here");
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.e("onMapReady", "Map Loading");
        //editText_search_source.requestFocus();
        googleMap.setPadding(0, 0, 0, 0);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else{
            try{
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task task_location = fusedLocationProviderClient.getLastLocation();
                task_location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currLoc = (Location) task.getResult();
                            if(currLoc != null){
                                double lat = currLoc.getLatitude();
                                double lon = currLoc.getLongitude();
                                Log.e("isGpsEnabled",""+currLoc.getLatitude()+" / "+currLoc.getLongitude());
                                if(lat > 20.86382 && lat < 26.33338 && lon > 88.15638 && lon < 92.30153){
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), MAP_ZOOM));

                                }
                                else{
                                    Log.e("location","outside");
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.8434,90.4029), MAP_ZOOM));
                                }
                            }
                        }
                    }
                });
            }
            catch(Exception e){
                startActivity(new Intent(this, ErrorActivity.class));
            }
        }

        if(CheckInternetConnectionClass.IsInternetConnected(HomeActivity.this)){
            this.googleMap = googleMap;

            if(firstLaunch == 1){
                editText_search_source.requestFocus();
                imageView_pin.setBackgroundResource(R.drawable.start_pin);
                autoCompleteEditTextClicked = 1;
                autoCompleteEditTextClicked1 = 0;
                firstLaunch = 2;
            }

            googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {
                    if(HomeActivity.this.getCurrentFocus().getId() == editText_search_source.getId()){
                        imageView_pin.setBackgroundResource(R.drawable.start_pin);
                    }
                    else if(HomeActivity.this.getCurrentFocus().getId() == editText_search_destination.getId()){
                        imageView_pin.setBackgroundResource(R.drawable.destination_pin);
                    }
                    RemoveKeyboard();
                    editText_search_source.setAdapter(null);
                    editText_search_destination.setAdapter(null);
                    autoCompleteEditTextClicked1 = 0;
                }
            });

            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    LatLng latLng = googleMap.getCameraPosition().target;
                    Log.e("Latlng",""+ latLng.toString());
                    if(HomeActivity.this.getCurrentFocus().getId() == editText_search_source.getId()){
                        autoCompleteEditTextClicked = 1;
                        autoCompleteEditTextClicked1 = 0;
                        //imageView_pin.setBackgroundResource(R.drawable.start_pin);
                    }

                    cancel_source.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(HomeActivity.this, "Imageview Clicked", Toast.LENGTH_SHORT).show();
                            editText_search_source.setText("");
                            editText_search_source.requestFocus();
                            imageView_pin.setBackgroundResource(R.drawable.start_pin);
                            autoCompleteEditTextClicked = 1;
                            autoCompleteEditTextClicked1 = 0;
                            editText_search_source.setAdapter(placeAutoCompleteAdapter);
                            count = 0;
                            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(editText_search_source.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                        }
                    });

                    cancel_destination.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editText_search_destination.setText("");
                            editText_search_destination.requestFocus();
                            imageView_pin.setBackgroundResource(R.drawable.destination_pin);
                            autoCompleteEditTextClicked = 0;
                            autoCompleteEditTextClicked1 = 1;
                            editText_search_destination.setAdapter(placeAutoCompleteAdapter);
                            count = 1;
                            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(editText_search_source.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                        }
                    });

                    editText_search_source.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (hasFocus) {
                                if (destinationLatlng != null) {
                                    Log.e("edit text selected",""+destinationLatlng.toString());
                                    Log.e("edit text not selected",""+latlngData.toString());
                                    if(marker_source != null){
                                        marker_source.remove();
                                    }
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(destinationLatlng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pointer))
                                            .title("Source");
                                    marker_destination = googleMap.addMarker(markerOptions);
                                }
                                autoCompleteEditTextClicked1 = 0;
                                editText_search_source.setText("");
                                count = 0;
                                editText_search_source.setAdapter(placeAutoCompleteAdapter);
                            }
                        }
                    });



                    //}
                    if(HomeActivity.this.getCurrentFocus().getId() == editText_search_destination.getId()){
                        autoCompleteEditTextClicked = 0;
                        autoCompleteEditTextClicked1 = 1;
                    }

                    editText_search_destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if(hasFocus){
                                if (sourceLatlng != null) {
                                    if(destinationLatlng != null){
                                        marker_destination.remove();
                                    }
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(sourceLatlng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_pointer))
                                            .title("Source");
                                    marker_source = googleMap.addMarker(markerOptions);
                                }

                                editText_search_destination.setText("");
                                count = 1;
                                editText_search_destination.setAdapter(placeAutoCompleteAdapter);
                            }
                        }
                    });


                    if(autoCompleteEditTextClicked == 1){
                        try{
                            Geocoder geocoder = new Geocoder(HomeActivity.this,
                                    Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                if(destinationLatlng != null){
                                    Log.e("destinationLatlng",""+destinationLatlng.toString());
                                }
                                sourceLatlng = new LatLng(latLng.latitude, latLng.longitude);
                                startLocation = address.getAddressLine(0).toString();
                                cancel_source.setVisibility(View.VISIBLE);
                                editText_search_source.setText(address.getAddressLine(0).toString());
                                Log.e("Current Location E",address.getSubThoroughfare()+","+address.getFeatureName()+","+address.getLocality()+","+
                                        address.getSubAdminArea()+","+address.getPostalCode() +","+ address.getCountryCode()+","+address.getCountryName());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(autoCompleteEditTextClicked1 == 1){
                        Log.e("destination input ","called!!!!!!!!!!!!!!!!!!!!!!!!");
                        try{
                            editText_search_destination.requestFocus();
                            Geocoder geocoder = new Geocoder(HomeActivity.this,
                                    Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                destinationLatlng = new LatLng(latLng.latitude, latLng.longitude);
                                latlngData = destinationLatlng;
                                endLocation = address.getAddressLine(0).toString();
                                cancel_destination.setVisibility(View.VISIBLE);
                                editText_search_destination.setText(address.getAddressLine(0).toString());
                                Log.e("Current Location dest",address.getSubThoroughfare()+","+address.getFeatureName()+","+address.getLocality()+","+
                                        address.getSubAdminArea()+","+address.getPostalCode() +","+ address.getCountryCode()+","+address.getCountryName());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //}
            });
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(location_permission_granted && GetLocationStatus()){
            Log.e("onMapReady", "calling Get_current_location");
            Get_current_location();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            try{
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.custom_map));
            }
            catch(Resources.NotFoundException e){
                Log.e("Home Activity","Custom Map Not Found");
            }
            googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
            //googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            Log.e("Calling Search_bar: ", "Search_bar_enable");
        }
        else{
            startActivity(new Intent(HomeActivity.this, GpsOff.class));
        }
        //googleMap.setMyLocationEnabled(true);
    }

    public Location Get_current_location() {


        try {
            if (location_permission_granted) {
              }
        }
        catch (SecurityException e){}
        return current_location;
    }

    public void Move_camera(LatLng latLngSource,LatLng latLngDestination, float zoom, String title){
        if(googleMap !=null){
            googleMap.clear();
        }
        List<Marker> markerList = new ArrayList<Marker>();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLngSource)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_pointer))
                .title(title);
        markerOptions.anchor(0.5f, 0.8f);
       // markerOptions.anchor(0.350f, 0.8f);
        marker_source_pin = googleMap.addMarker(markerOptions);
        MarkerOptions markerOptions1 = new MarkerOptions()
                .position(latLngDestination)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pointer))
                .title(title);
        //markerOptions1.anchor(0.375f,0.8f);
        markerOptions1.anchor(0.5f,0.775f);
        marker_destination_pin = googleMap.addMarker(markerOptions1);

        //LatLngBounds bn = googleMap.getProjection().getVisibleRegion().latLngBounds;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLngSource);
        builder.include(latLngDestination);
        LatLngBounds bounds = builder.build();

        //googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 2000, null);
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 20);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250,250,5));
        //googleMap.animateCamera(cameraUpdate);
    }

    public void Geolocate( int id ){
        if(id == 1){
            Log.e("Calling Geolocate: ", "Geolocating");
            input_text_source = editText_search_source.getText().toString();
            Geocoder geocoder = new Geocoder(HomeActivity.this);
            try{
                list_source = geocoder.getFromLocationName(input_text_source, 1);
            }
            catch(IOException e){
            }
        }
        else if(id == 2){
            Log.e("Calling Geolocate: ", "Geolocating");
            input_text_destination = editText_search_destination.getText().toString();
            Geocoder geocoder = new Geocoder(HomeActivity.this);
            try{
                list_destination = geocoder.getFromLocationName(input_text_destination, 1);
            }
            catch(IOException e){
            }
        }

        if(list_source.size() > 0 && list_destination.size() > 0){
            address_source = list_source.get(0);
            address_destination = list_destination.get(0);
            Log.e("Geolocate: ", address_source.toString());
            Log.e("Geolocate: ", address_destination.toString());

        }
    }

        public static void SetPinMarginTop(View v, int top) {
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams)v.getLayoutParams();
        params.setMargins(params.leftMargin, top,
                params.rightMargin, params.bottomMargin);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        location_permission_granted = false;

        if(requestCode == REQUEST_CODE){
            if(grantResults.length >0){
                for(int i = 0; i< grantResults.length; i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        location_permission_granted = false;
                        return;
                    }
                }
                location_permission_granted = true;
                startActivity(getIntent());
                Log.e("onRequestPermisioResult", "Init_map called here");
            }
        }
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final AutocompletePrediction autocompletePrediction_place = placeAutoCompleteAdapter.getItem(i);
                String placeId = autocompletePrediction_place.getPlaceId();

                PendingResult<PlaceBuffer> place_result = Places.GeoDataApi
                        .getPlaceById(googleApiClient, placeId);
                place_result.setResultCallback(resultCallback);

            }

    };

    private ResultCallback<PlaceBuffer> resultCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.e("ResultCallBack:", "Error on ResultCallBack");
                places.release();
                return;
            }
            Log.e("Place Result",places.get(0).getAddress().toString());
                if(count == 0){
                    Toast.makeText(HomeActivity.this, "called 1", Toast.LENGTH_SHORT).show();
                    startLocation = places.get(0).getAddress().toString();
                    placeSource = places.get(0);
                    sourceLatlng = new LatLng(placeSource.getLatLng().latitude, placeSource.getLatLng().longitude);
                    Log.e("source",""+source.toString());

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(placeSource.getLatLng().latitude, placeSource.getLatLng().longitude));
                    LatLngBounds bounds = builder.build();
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placeSource.getLatLng().latitude, placeSource.getLatLng().longitude), MAP_ZOOM));
                    RemoveKeyboard();
                    count = -1;
                }
                else if(count == 1){
                    Toast.makeText(HomeActivity.this, "called 2", Toast.LENGTH_SHORT).show();
                    endLocation = places.get(0).getAddress().toString();
                    placeDestination = places.get(0);
                    destinationLatlng = new LatLng(placeDestination.getLatLng().latitude, placeDestination.getLatLng().longitude);
                    Log.e("destination-1",""+destinationLatlng.toString());

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(placeDestination.getLatLng().latitude, placeDestination.getLatLng().longitude));
                    LatLngBounds bounds = builder.build();
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placeDestination.getLatLng().latitude, placeDestination.getLatLng().longitude), MAP_ZOOM));
                    RemoveKeyboard();
                    count = -2;
                }
            places.release();
        }
    };

    public void DoneButton(){
        tripHistoryList = new ArrayList<Requests>();
        sharedPreferencesClassObject = new SharedPreferencesClass(HomeActivity.this);
        phoneNo = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference("Requests");
        Query data = databaseReferenceRequests;
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Requests request = snapshot.getValue(Requests.class);
                        if (request.getUserPhone().equals(phoneNo)) {
                            if(HoursDifference(request) > -1 ){
                                Log.e("HoursDiff",""+HoursDifference(request));
                                tripHistoryList.add(request);
                            }
                        }
                    }
                    if(tripHistoryList.size() <1 || tripHistoryList == null){
                        DoneButtonConfirmation();
                    }
                    else if(tripHistoryList.size()>0){
                        ShowSnackbar("You Have already a trip request pending");
                    }
                }
                else{
                    Toast.makeText(HomeActivity.this, ""+dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                    DoneButtonConfirmation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void DoneButtonConfirmation(){
        if(sourceLatlng == null || destinationLatlng == null || editText_search_source.getText().toString() == "" || editText_search_destination.getText().toString() == ""){
            Toast.makeText(this, "Error source or destination", Toast.LENGTH_SHORT).show();
            Log.e("Source done",""+source);
            Log.e("Destination done",""+destination);
        }
        else{
            showCostPage = true;
            Log.e("Source",""+source);
            Log.e("Destination",""+destination);
            source.setLatitude(sourceLatlng.latitude);
            source.setLongitude(sourceLatlng.longitude);

            destination.setLatitude(destinationLatlng.latitude);
            destination.setLongitude(destinationLatlng.longitude);

            dist = source.distanceTo(destination);
            Toast.makeText(this, ""+dist, Toast.LENGTH_SHORT).show();
            if(dist/1000 > 40){
                String distance = new DecimalFormat("##.##").format(dist/1000);

                costCar = dist * 45;
                costMicro = dist * 60;
                showCostFourSeater = new DecimalFormat("##.##").format(costCar/1000);

                relativeLayout_source.setVisibility(View.GONE);
                relativeLayout_destination.setVisibility(View.GONE);
                button_done.setVisibility(View.GONE);
                imageView_pin.setVisibility(View.GONE);

                url = GetRequestUrl(source, destination);
                Log.e("url",""+url);
                RemoveKeyboard();

                Thread taskRequestThread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(300);
                            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                            taskRequestDirections.execute(url);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                taskRequestThread.start();

                height = getWindowManager().getDefaultDisplay().getHeight();
                width = getWindowManager().getDefaultDisplay().getWidth();

                Picasso.get()
                        .load(R.drawable.car_four_seater_selected)
                        .rotate(0)
                        .resize(96, 96)
                        .centerCrop()
                        .into(imageView_car_four_seater);
                Picasso.get()
                        .load(R.drawable.car_icon_micro)
                        .rotate(0)
                        .resize(96, 96)
                        .centerCrop()
                        .into(imageView_car_micro);

                if(isFoursSeaterSelected){
                    textView_bill.setText(showCostFourSeater+" "+"BDT");
                }
                ShowRouteTextview(source, destination);

                ViewGroup.LayoutParams layoutParams = relativeLayout_main.getLayoutParams();
                layoutParams.height = (height/2);

                relativeLayout_bottom_sheet.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams_bottomSheet = relativeLayout_bottom_sheet.getLayoutParams();
                layoutParams_bottomSheet.height = (height/2);
                relativeLayout_bottom_sheet.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));

                count = 0;
            }
            else{
                ShowSnackbar("Distance is Too Short for a Trip");
            }
        }
    }

    private void ShowSnackbar(String message){
        Snackbar snackbar = Snackbar.make(drawerLayout, ""+message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(getIntent());
                    }
                });
        snackbar.show();
    }

    private String GetRequestUrl(Location placeSource, Location placeDestination){
        //value of origin
        String str_org = "origin=" + placeSource.getLatitude() + "," + placeSource.getLongitude();
        //value of destination
        String str_dest = "destination=" + placeDestination.getLatitude() + "," + placeDestination.getLongitude();
        //set value enable for direction
        String sensor = "sensor=false";
        //mode for find direction
        String mode = "mode=driving";
        //build the full parameter
        String param = str_org +"&" + str_dest +"&" + sensor + "&" + mode;
        String output = "json";
        String url = "http://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String RequestDirection(String reqUrl){
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
    public void CalculateCostForCar(View view){
        //Toast.makeText(this, "CalculateCostForCar", Toast.LENGTH_SHORT).show();
        Picasso.get()
                .load(R.drawable.car_four_seater_selected)
                .rotate(0)
                .resize(96, 96)
                .centerCrop()
                .into(imageView_car_four_seater);
        Picasso.get()
                .load(R.drawable.car_icon_micro)
                .rotate(0)
                .resize(96, 96)
                .centerCrop()
                .into(imageView_car_micro);
        isFoursSeaterSelected = true;
        carType = "Four Seater";
        showCostFourSeater = new DecimalFormat("##.##").format(costCar/1000);
        textView_bill.setText(showCostFourSeater+" "+"BDT");
        //textView_bill.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_animation));
    }
    public void CalculateCostForMicro(View view){
        //Toast.makeText(this, "CalculateCostForMicro", Toast.LENGTH_SHORT).show();
        Picasso.get()
                .load(R.drawable.car_four_seater)
                .rotate(0)
                .resize(96, 96)
                .centerCrop()
                .into(imageView_car_four_seater);
        Picasso.get()
                .load(R.drawable.car_icon_micro_selected)
                .rotate(0)
                .resize(96, 96)
                .centerCrop()
                .into(imageView_car_micro);
        isFoursSeaterSelected = false;
        carType = "Micro";
        showCostMicro = new DecimalFormat("##.##").format(costMicro/1000);
        textView_bill.setText(showCostMicro+" "+"BDT");
        //textView_bill.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_animation));
    }
    public void ConfirmRequest(View view){

        requestId = databaseReferenceRequests.push().getKey();

        Log.e("source",""+ districtSource);
        Log.e("destiantion",""+ districtDestination);
        if(isFoursSeaterSelected){
            cost = showCostFourSeater;
        }
        else{
            cost = showCostMicro;
        }
        android.support.v4.app.DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date Picker");

    }

    private void ShowRouteTextview(Location source, Location destination) {
       geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try
        {
            addressSource = geocoder.getFromLocation(sourceLatlng.latitude, sourceLatlng.longitude, 1);
            addressDestination = geocoder.getFromLocation(destinationLatlng.latitude, destinationLatlng.longitude, 1);
            if ((addressSource != null && addressSource.size() > 0) && addressDestination != null && addressDestination.size() > 0)
            {
                addressSrc = addressSource.get(0).getAddressLine(0);
                addressDes = addressDestination.get(0).getAddressLine(0);
                districtSource = addressSource.get(0).getLocality();
                districtDestination = addressDestination.get(0).getLocality();
                Log.e("From_Locality",""+districtSource);
                Log.e("To_Locality",""+districtDestination);
                Log.e("From... ",""+startLocation.toString());
                Log.e("To... ",""+endLocation.toString());
            }
        }
        catch (IOException e) {
        }
    }

    public void RemoveKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)HomeActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(relativeLayout_destination.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void ShowDistanceDialog(String showCost){
        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
        alertDialog.setTitle("Cost: ");

        alertDialog.setMessage(showCost + " BDT");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    @Override
//    public void onRoutingFailure(RouteException e) {
//        if(e != null) {
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }else {
//            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onRoutingStart() {
//
//    }
//
//    @Override
//    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }
//
//        polylines = new ArrayList<>();
//        //add route(s) to the map.
//        for (int i = 0; i <route.size(); i++) {
//
//            PolylineOptions polyOptions = new PolylineOptions();
//            polyOptions.color(Color.parseColor("#000000"));
//            polyOptions.width(5);
//            polyOptions.addAll(route.get(i).getPoints());
//            Polyline polyline = googleMap.addPolyline(polyOptions);
//            polylines.add(polyline);
//        }
//    }
//
//    @Override
//    public void onRoutingCancelled() {
//
//    }

    private void removePolyLine(){
        if(polylines.size() > 0){
            for(Polyline line: polylines){
                line.remove();
            }
            polylines.clear();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause","pause state called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mapFragment !=null){
            mapFragment.onResume();
        }
        CheckInternetConnectivity();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if(showCostPage) {
            startActivity(getIntent());
        }

        else{
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_button_my_trips) {
            drawerLayout.closeDrawer(GravityCompat.START, false);
            Intent i = new Intent(HomeActivity.this, TripsHistory.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if (id == R.id.navigation_button_account_settings) {
            drawerLayout.closeDrawer(GravityCompat.START, false);
            Intent i = new Intent(HomeActivity.this, AccountSettings.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_down_upper, R.anim.slide_down_lower);
        }

        else if (id == R.id.navigation_button_terms_and_condition) {
            drawerLayout.closeDrawer(GravityCompat.START, false);
            Intent i = new Intent(HomeActivity.this, TermsAndConditions.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if (id == R.id.navigation_button_about) {
            drawerLayout.closeDrawer(GravityCompat.START, false);
            Toast.makeText(HomeActivity.this, "About Selected", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.navigation_button_log_out) {
            sharedPreferencesClassObject.setSharedPreferences("UserData", "UserName", "");
            sharedPreferencesClassObject.setSharedPreferences("UserData", "UserEmail", "");
            sharedPreferencesClassObject.setSharedPreferences("UserData", "UserPhone", "");
            sharedPreferencesClassObject.setSharedPreferences("UserData", "UserId", "");
            sharedPreferencesClassObject.setSharedPreferences("UserData", "UserImageAngle", "");
            startActivity(new Intent(HomeActivity.this, FirstActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month + 1;
        this.day = dayOfMonth;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("BD"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this, HomeActivity.this, hour, minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hour = hourOfDay;
        this.minute = minute;

        requestDate = day+"/"+month+"/"+year+ "  " + hour+":"+minute;
        long hoursDiff = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm");
        try {
            TimeZone timeZone = TimeZone.getTimeZone("GMT+6");
            Calendar calendar = Calendar.getInstance(timeZone);

            Date currentDate = simpleDateFormat.parse(calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
            Date requestedDate = simpleDateFormat.parse(requestDate);

            long differenceBetweenDates = requestedDate.getTime() - currentDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            hoursDiff = differenceBetweenDates / hoursInMilli;
            differenceBetweenDates = differenceBetweenDates % hoursInMilli;

            if(hoursDiff >= 2){
                String userName = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserName", "");
                String userEmail = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserEmail", "");
                String userPhone = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
                String userId = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserId", "");

                Requests request = new Requests(requestId, userName, userEmail,userPhone,userId, addressSrc, addressDes, cost, carType, requestDate);
                databaseReferenceRequests.child(requestId).setValue(request);

                showDialog();
            }
            else{
                Toast.makeText(getApplicationContext(),"You can't request for cars less than 2 hours", Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(){
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        dialogButton = (Button)dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                relativeLayout_bottom_sheet.setVisibility(View.GONE);

                startActivity(getIntent());
            }
        });

        dialog.show();
    }

    public String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd MM yyyy, HH:mm");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        return date;
    }

//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        Log.e("onRestart","restart state called");
//        finish();
//        startActivity(getIntent());
//    }


    public  class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            responseString = RequestDirection(strings[0]);
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            try{
                points = null;
                PolylineOptions polylineOptions = null;
                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        points.add(new LatLng(lat, lon));

                    }
                    Move_camera(points.get(0),points.get(points.size()-1),MAP_ZOOM,null);
                    //direction_start = points.get(0);
                    //direction_end = points.get(points.size()-1);
                    Log.e("pointstart",""+direction_start);
                    Log.e("pointsEnd",""+direction_end);
                    polylineOptions.addAll(points);
                    polylineOptions.width(7);
                    polylineOptions.color(Color.BLACK);
                    polylineOptions.geodesic(true);
                }

                if (polylineOptions != null) {
                    googleMap.addPolyline(polylineOptions);
                } else {
                    //Toast.makeText(HomeActivity.this, "Direction Not Found", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e){
                Toast.makeText(HomeActivity.this, "please try again", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
            }
        }
    }

    public void CheckInternetConnectivity(){
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
                if(CheckInternetConnectionClass.InNetworkAvailable(HomeActivity.this, type)){
                    return;
                }
                else{
                    Snackbar snackbar = Snackbar
                            .make(drawerLayout, "No Internet Connection", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    startActivity(new Intent(HomeActivity.this, InternetNotConnected.class));
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}