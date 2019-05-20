package com.android.shamim.taketour;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shamim.taketour.helper.Utility;
import com.android.shamim.taketour.pojjoclass.Events;
import com.android.shamim.taketour.pojjoclass.Moments;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class TakeCameraPhoto extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSAVE;
    private ImageView ivImage;
    EditText caption;
    private String userChoosenTask;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Uri uri;
    private StorageReference mStorageRef;
    private String eventid;
    private String userid;
    private  String photoname;
    private Uri picUri;
    private TextView show;
    private Events event;
    private  Intent intent;
    FirebaseUser user;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_camera_photo);
        ivImage = findViewById(R.id.imageView);
        caption = findViewById(R.id.caption);
        btnSAVE = findViewById(R.id.saveBtn);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        photoname = String.valueOf(System.currentTimeMillis());
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Moments");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Take Photo");
        setSupportActionBar(toolbar);
        show = findViewById(R.id.showmessage);
        Intent in = getIntent();
        event= (Events) in.getSerializableExtra("obj");
        eventid = event.getEventID();
        userid = event.getUserID();
        selectImage();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if(userChoosenTask.equals("Take Photo"))
                    cameraIntent();
                    // else if(userChoosenTask.equals("Choose from Library"))
                    //    galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {

        boolean result=Utility.checkPermission(TakeCameraPhoto.this);
        if(result)
            cameraIntent();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){

//check if have permmission to write to external + permmision to camera

            if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){

//if its have permission then new intent to capture image

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//if android version >= 24 (Android Nugget)

                if(Build.VERSION.SDK_INT>=24) {

//create new String to pictures directory and set the file name to current_time.jpg
                    String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/" + photoname + ".jpg";
                    File file1 = new File(file);

//set GLOBAL variable Uri from the file using FileProvider

                    picUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file1);

//add the file location the intent (the picture will be save to this file)

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                }

//else - dont have permission, request for permission
            }else{
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA},REQUEST_CAMERA);
            }

        }else{
// same thing only without FileProvider (FileProvider only required since Nugget)

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/" +photoname+ ".jpg";
            File file1 = new File(file);
            picUri = Uri.fromFile(file1);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,picUri);

        }

//if phone can handle the intent, start the intent for resault.

        if(intent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(intent,REQUEST_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                ivImage.setImageURI(picUri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void onCaptureImageResult(Intent data) {


        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        photoname = String.valueOf(System.currentTimeMillis());
        File destination = new File(Environment.getExternalStorageDirectory(),
                "DCIM/Camera/"+ photoname + ".jpg");



        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        Uri uri2 = (Uri) extras.get("data");
        Toast.makeText(this, "inside uri: "+ uri2, Toast.LENGTH_SHORT).show();

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    public void uploadToServer(View view) {
        StorageReference riversRef = mStorageRef.child("gallery/" +photoname + ".jpg");
       // Toast.makeText(this, "photo uri is : "+ picUri, Toast.LENGTH_SHORT).show();
        riversRef.putFile(picUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();


                        //  String url=downloadUrl.getPath();
                        uploadDatabaseFilename(downloadUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        show.setText("Photo uupload fail " +  exception.getMessage().toString());
                        show.setVisibility(View.VISIBLE);

                    }
                });


    }
    public  void uploadDatabaseFilename(String url) {

        String pcaption = caption.getText().toString();
        String key= databaseReference.push().getKey();
        databaseReference.child(key).setValue(new Moments(key,eventid,url,pcaption));
        Toast.makeText(this, "Photo upload Successfull", Toast.LENGTH_SHORT).show();
    }

    public void takeAnotherPhoto(View view) {
        selectImage();
    }


    public void gobackpage(View view) {
        startActivity(new Intent(this,EventDetail.class).putExtra("obj",event));
    }

    /************************************** Menu Item Stsrt Here ************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem Searcc = menu.findItem(R.id.search);
        MenuItem CelsiusItem = menu.findItem(R.id.tempc);
        MenuItem FahrenheitItem = menu.findItem(R.id.tempf);
        MenuItem EventItem = menu.findItem(R.id.events);
        MenuItem MapItem = menu.findItem(R.id.location_map);
        MenuItem NearPlaceItem = menu.findItem(R.id.nearplace);
        MenuItem MapDirectionItem = menu.findItem(R.id.direction);
        MenuItem WeatherItem = menu.findItem(R.id.weather_info);
        MenuItem LogoutItem = menu.findItem(R.id.logout);
        MenuItem Myprofile = menu.findItem(R.id.profile);
        Searcc.setVisible(false);
        CelsiusItem.setVisible(false);
        FahrenheitItem.setVisible(false);
        EventItem.setVisible(true);
        MapItem.setVisible(true);
        NearPlaceItem.setVisible(true);
        MapDirectionItem.setVisible(true);
        WeatherItem.setVisible(true);
        if(user != null) {
            LogoutItem.setVisible(true);
            Myprofile.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hm:
                startActivity(new Intent(this,EventList.class));
                break;
            case R.id.events:
                startActivity(new Intent(this,EventList.class));
                break;
            case R.id.location_map:
                startActivity(new Intent(this,LocationMap.class));
                break;
            case R.id.nearplace:

                startActivity(new Intent(this,NearestPlace.class));
                break;
            case R.id.direction:
                startActivity(new Intent(this,DirectionMap.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this,UserProfile.class));
                break;
            case R.id.weather_info:
                startActivity(new Intent(this,WeatherInfo.class));
                break;
            case R.id.logout:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /************************************** Menu Item End Here ************************************/
    public void logoutUser() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent (TakeCameraPhoto.this,MainActivity.class));
                finish();
            }
        });
    }
}
