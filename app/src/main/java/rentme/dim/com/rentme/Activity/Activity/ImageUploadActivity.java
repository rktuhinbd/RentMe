package rentme.dim.com.rentme.Activity.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.R;

public class ImageUploadActivity extends AppCompatActivity {

    private LinearLayout linearLayout_image_upload;
    private Button buttonUploadImage;
    private int IMAGE_INETENT = 2, orientation = -1;;
    private String imageName, phoneNumber;
    private StorageReference storageRef;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private DatabaseReference databaseReferenceSignInInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        sharedPreferencesClassObject = new SharedPreferencesClass(ImageUploadActivity.this);

        sharedPreferencesClassObject = new SharedPreferencesClass(ImageUploadActivity.this);
        phoneNumber = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");

        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

        linearLayout_image_upload = (LinearLayout) findViewById(R.id.linearLayout_image_upload);

        storageRef = FirebaseStorage.getInstance().getReference();
        buttonUploadImage = (Button) findViewById(R.id.button_image_upload);
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageName = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");
                Upload();
            }
        });
    }

    private void Upload(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_INETENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_INETENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference filepath = storageRef.child("Photos").child(imageName);
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = managedQuery(uri, orientationColumn, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                UpdateImageAngle();
                sharedPreferencesClassObject.setSharedPreferences("UserData", "UserImageAngle", ""+orientation);
                Log.e("Orientation", orientation+"");
            }
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    //Toast.makeText(ImageUploadActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void UpdateImageAngle() {
        if(orientation != -1){
            final Query data = databaseReferenceSignInInfo.child(phoneNumber);
            Log.e("query result", data.toString());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        //SignInInfo signInInfo = dataSnapshot.getValue(SignInInfo.class);
                        //String name = signInInfo.getUserName();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("userImageAngle", ""+orientation);
                        databaseReferenceSignInInfo.child(phoneNumber).updateChildren(result);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserImageAngle", ""+orientation);
                    }
                    else{
                        ShowSnackbar("Phone number is not registered");
                        //Toast.makeText(ImageUploadActivity.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        else{

        }
    }

    private void ShowSnackbar(String message){
        Snackbar snackbar = Snackbar.make(linearLayout_image_upload, ""+message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(getIntent());
                    }
                });
        snackbar.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
