package rentme.dim.com.rentme.Activity.Activity.NavigationDrawerItems;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import rentme.dim.com.rentme.Activity.Activity.HomeActivity;
import rentme.dim.com.rentme.Activity.Data.CircleImageTransformation;
import rentme.dim.com.rentme.Activity.Data.SharedPreferencesClass;
import rentme.dim.com.rentme.R;

public class AccountSettings extends AppCompatActivity {

    private Button buttonBack, buttonEditInfo,dialogButtonUpload,dialogButtonCancel;
    private ImageView imageView_user_profile;
    private TextView textViewFullName, textViewEmail, textViewPassword, textViewPhone;
    private SharedPreferencesClass sharedPreferencesClassObject;
    private String userName, userEmail, userPhone;
    private DatabaseReference databaseReferenceRequests, databaseReferenceSignInInfo;
    private StorageReference storageReference;
    private BottomSheetDialog bottomSheetDialog;
    private int IMAGE_INETENT = 2, orientation = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        imageView_user_profile = (ImageView) findViewById(R.id.imageView_user_profile_photo);
        textViewFullName = (TextView)findViewById(R.id.textView_full_name);
        textViewPhone = (TextView)findViewById(R.id.textView_phone);
        textViewPassword = (TextView)findViewById(R.id.textView_password);
        textViewEmail = (TextView)findViewById(R.id.textView_email);

        sharedPreferencesClassObject = new SharedPreferencesClass(AccountSettings.this);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReferenceSignInInfo = FirebaseDatabase.getInstance().getReference("SignInInfo");

        userName = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserName", "");
        userEmail = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserEmail", "");
        userPhone = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserPhone", "");

        ViewDataInTextView(userName, userEmail, userPhone);

        ShowImage();

        textViewFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountSettings.this, EditName.class);
                i.putExtra("FullName",textViewFullName.getText().toString());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountSettings.this, ChangeEmail.class);
                i.putExtra("Email",textViewEmail.getText().toString());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        textViewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountSettings.this, ChangePassword.class);
                i.putExtra("Phone",textViewPhone.getText().toString());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        imageView_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(AccountSettings.this);
                bottomSheetDialog.setContentView(R.layout.upload_image_bottom_sheet_dialog);
                bottomSheetDialog.show();
                dialogButtonUpload = (Button)bottomSheetDialog.findViewById(R.id.dialog_button_upload);
                dialogButtonCancel = (Button)bottomSheetDialog.findViewById(R.id.dialog_button_cancel);

                dialogButtonUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(AccountSettings.this, "Upload called", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        Upload();
                    }
                });
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(AccountSettings.this, "cancel", Toast.LENGTH_SHORT).show();
                    }
                });
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
            StorageReference filepath = storageReference.child("Photos").child(userPhone);
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
                    Toast.makeText(AccountSettings.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void UpdateImageAngle() {
        if(orientation != -1){
            final Query data = databaseReferenceSignInInfo.child(userPhone);
            Log.e("query result", data.toString());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("userImageAngle", ""+orientation);
                        databaseReferenceSignInInfo.child(userPhone).updateChildren(result);
                        sharedPreferencesClassObject.setSharedPreferences("UserData", "UserImageAngle", ""+orientation);
                    }
                    else{
                        Toast.makeText(AccountSettings.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("onCancelled", "onCancelled", databaseError.toException());
                }
            });
        }
        else{
            Toast.makeText(AccountSettings.this, "Password Did Not Match", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowImage(){
        if(storageReference.child("Photos").child(userPhone).getDownloadUrl() != null){
            try{
                storageReference.child("Photos").child(userPhone).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String angle = sharedPreferencesClassObject.getSharedPreferences("UserData", "UserImageAngle", "");
                        int ang = Integer.valueOf(angle);

                        if(ang == 270){
                            Picasso.get()
                                    .load(uri)
                                    .rotate(270)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_profile);
                        }
                        else if(ang == 180){
                            Picasso.get()
                                    .load(uri)
                                    .rotate(180)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_profile);
                        }
                        else if(ang == 0){
                            Picasso.get()
                                    .load(uri)
                                    .rotate(0)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_profile);
                        }
                        else if(ang == 90){
                            Picasso.get()
                                    .load(uri)
                                    .rotate(90)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_profile);
                        }
                        else{
                            Picasso.get()
                                    .load(uri)
                                    .resize(100, 120)
                                    .transform(new CircleImageTransformation())
                                    .centerCrop()
                                    .into(imageView_user_profile);
                        }
                    }
                });
            }
            catch (Exception e){
                imageView_user_profile.setBackgroundResource(R.drawable.car_logo);
            }
        }
        else{
            Toast.makeText(this, "Image Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void ViewDataInTextView(String userName, String userEmail, String userPhone){
        textViewFullName.setText(userName);
        textViewEmail.setText(userEmail);
        textViewPhone.setText(userPhone);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AccountSettings.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.slide_up_lower, R.anim.slide_up_upper);
    }
}
