package com.example.splitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import androidx.core.content.ContextCompat;
import android.location.Geocoder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "bruih";
    long unixTime = System.currentTimeMillis() / 1000L;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatLong;
    private TextView textAddress;
    private TextView textCity;
    private TextView textState;

    private EditText title;
    private ImageView imageLeft;
    private ImageView imageRight;
    private Button uploadButton;
    private Button locationButton;
    private Button locationButtonSelect;
    private boolean leftRightCheck = false;
    public Uri imageUriLeft;
    public Uri imageUriRight;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    public String globalLeftImage;
    public String globalRightImage;
    public long globalUnix;
    public String tempTitle;
    FirebaseUser user;
    String userID;
    public String latitude;
    public String longitude;
    public String latitudeFunc;
    public String longitudeFunc;
    public Double longtiudeD;
    public Double latitudeD;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public String addressFunc = null;
    public String cityFunc = null;
    public String stateFunc = null;
    public String countryFunc  = null;
    public String postalCodeFunc = null;
    public String address;
    public String city;
    public String state;
    public String country;
    public String postalCode;
    public Boolean locationBoolean;





    private class Post {

        public String uID;
        public String postTitle;
        public String leftURL;
        public String rightURL;
        public long unixTimestamp;
        public int leftVotes;
        public int rightVotes;
        public int totalVotes;
        public String latitude = null;
        public String longitude = null;
        public String city = null;
        public String state = null;
        public String address = null;
        public Boolean locationBool = false;



        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Post(String uID, String postTitle, String leftURL, String rightURL, long unixTimestamp, int leftVotes, int rightVotes, int totalVotes, String latitude, String longitude, String city, String state, String address,  Boolean locationBool) {
            this.uID = uID;
            this.postTitle = postTitle;
            this.leftURL = leftURL;
            this.rightURL = rightURL;
            this.unixTimestamp = unixTimestamp;
            this.leftVotes = leftVotes;
            this.rightVotes = rightVotes;
            this.totalVotes = totalVotes;
            this.latitude = latitude;
            this.longitude = longitude;
            this.city = city;
            this.state = state;
            this.address = address;
            this.locationBool = locationBool;

        }

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title = findViewById(R.id.et_Title);
        imageLeft = findViewById(R.id.image_left);
        imageRight = findViewById(R.id.image_right);
        storage  =FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uploadButton = findViewById(R.id.bt_upload);
        locationButton = findViewById(R.id.bt_location);
        textLatLong = findViewById(R.id.tv_location);
        locationButtonSelect = findViewById(R.id.bt_location_select);
        textAddress = findViewById(R.id.tv_address);
        textCity = findViewById(R.id.tv_city);
        textState = findViewById(R.id.tv_state);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.cole));
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PostActivity.this);

        locationButtonSelect.setVisibility(View.INVISIBLE);



        firebaseAuth = FirebaseAuth.getInstance();
        String[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Pick an input");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("Gallery".equals(options[which]))
                {
                    //Toast.makeText(PostActivity.this, "Gallery selected", Toast.LENGTH_SHORT).show();
                    choosePic(options[which]);
                }
                else if("Camera".equals(options[which]))
                {
                    //Toast.makeText(PostActivity.this, "Camera selected", Toast.LENGTH_SHORT).show();
                    choosePic(options[which]);
                }
            }
        });

        //Get current location
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                   getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(PostActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            100);
                }
                locationButtonSelect.setVisibility(View.VISIBLE);
            }
        });

        locationButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longitude = longitudeFunc;
                latitude = latitudeFunc;
                city = cityFunc;
                state = stateFunc;
                address = addressFunc;
                locationBoolean = true;

            }
        });

        imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightCheck = false;
                builder.show();
            }
        });
        imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightCheck = true;
                builder.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        latitudeD = location.getLatitude();
                        longtiudeD = location.getLongitude();
                        latitudeFunc = String.valueOf(location.getLatitude());
                        longitudeFunc = String.valueOf(location.getLongitude());
                        textLatLong.setText(String.format("Latitude: %s\nLongitude: %s", latitudeFunc, longitudeFunc));

                        List<Address> addresses = null;
                        try {
                            Geocoder geocoder = new Geocoder(PostActivity.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(latitudeD, longtiudeD, 1);
                            addressFunc = addresses.get(0).getAddressLine(0);
                            cityFunc = addresses.get(0).getLocality();
                            stateFunc = addresses.get(0).getAdminArea();
                            countryFunc  = addresses.get(0).getCountryName();
                            postalCodeFunc = addresses.get(0).getPostalCode();

                            textAddress.setText(addressFunc);
                            textCity.setText(cityFunc);
                            textState.setText(stateFunc);

                        } catch (IOException e) {
                            Log.e("tag", e.getMessage());
                        }

                    } else {
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                latitudeD = location.getLatitude();
                                longtiudeD = location.getLongitude();
                                latitudeFunc = String.valueOf(location.getLatitude());
                                longitudeFunc = String.valueOf(location.getLongitude());
                                textLatLong.setText(String.format("Latitude: %s\nLongitude: %s", latitudeFunc, longitudeFunc));


                                List<Address> addresses = null;
                                try {
                                    Geocoder geocoder = new Geocoder(PostActivity.this, Locale.getDefault());
                                    addresses = geocoder.getFromLocation(latitudeD, longtiudeD, 1);
                                    addressFunc = addresses.get(0).getAddressLine(0);
                                    cityFunc = addresses.get(0).getLocality();
                                    stateFunc = addresses.get(0).getAdminArea();
                                    countryFunc  = addresses.get(0).getCountryName();
                                    postalCodeFunc = addresses.get(0).getPostalCode();

                                } catch (IOException e) {
                                    Log.e("tag", e.getMessage());
                                }




                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    private void choosePic(String choice) {
        if (choice.equals("Gallery"))
        {
            Intent intent = new Intent();
            intent.setAction(intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        }
        else
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Gallery result code
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            if(leftRightCheck)
            {
                imageUriRight = data.getData();
                imageRight.setImageURI(imageUriRight);
            }
            else
                {
                    imageUriLeft = data.getData();
                    imageLeft.setImageURI(imageUriLeft);
                }
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadpic();

                    startActivity(new Intent(PostActivity.this, SecondActivity.class));
                }

            });
        }
        //Camera request code
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            if(leftRightCheck)
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUriRight = getImageUri(this,photo);
                imageRight.setImageURI(imageUriRight);
            }
            else
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUriLeft = getImageUri(this,photo);
                imageLeft.setImageURI(imageUriLeft);
            }
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadpic();

                    startActivity(new Intent(PostActivity.this, SecondActivity.class));
                }

            });
        }
    }

    private void uploadpic() {


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        globalUnix = (unixTime * -1);
        tempTitle = title.getText().toString();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Post post = new Post(userID,tempTitle,globalLeftImage,globalRightImage,globalUnix,0 ,0, 0, latitude, longitude, city, state, address, locationBoolean );

        final String randomKeyLeft = UUID.randomUUID().toString();
        final String randomKeyRight = UUID.randomUUID().toString();
        StorageReference mountainsRef = storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix +"-" + tempTitle + "/" + randomKeyLeft);
        //left image
        mountainsRef.putFile(imageUriLeft).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
                storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix + "-" + tempTitle + "/" + randomKeyLeft).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String leftImage =task.getResult().toString();
                        post.leftURL = leftImage;
                        //post left pic to DB
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).setValue(post);
                        Log.i("left URL", leftImage);
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).child("hasVoted").child(userID).setValue(userID);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });
        //right image
        mountainsRef = storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix +"-" + tempTitle + "/" + randomKeyRight);
        mountainsRef.putFile(imageUriRight).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
                storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix + "-" + tempTitle + "/" + randomKeyRight).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String rightImage = task.getResult().toString();
                        post.rightURL = rightImage;
                        //post right pic to DB
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).setValue(post);
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).child("hasVoted").child(userID).setValue(userID);
                        Log.i("right URL", rightImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



}