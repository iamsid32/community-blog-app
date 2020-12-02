package com.example.communityblogapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.communityblogapp.Activities.ui.home.HomeFragment;
import com.example.communityblogapp.Activities.ui.profile.ProfileFragment;
import com.example.communityblogapp.Activities.ui.settings.SettingsFragment;
import com.example.communityblogapp.Models.Post;
import com.example.communityblogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Home extends AppCompatActivity  {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;

    ImageView popup_user_image,popup_post_img,popup_add;
    TextView popup_title,popup_description;
    ProgressBar popup_progressBar;

    private static final int PReqCode = 2;
    private static final int REQUESTCODE = 2;

    private AppBarConfiguration mAppBarConfiguration;
    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //ini popup
        iniPopup();
        setupPopupImageClick();
        
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_settings,R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavHeader();

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginActivity);
                finish();
                return true;
            }
        });

    }

    private void setupPopupImageClick() {
        popup_post_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when image is clicked we need to open the gallery
                //before opening the gallery we need to check if app has permission to access to it\
                //we have done this before in the register activity

                checkAndRequestForPermission();
            }
        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else{
            openGallery();
        }
    }

    private void openGallery() {
        //oprn gallery intent and until user pick an image

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    //when user picked an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data !=null){
            //user has successfully picked an image
            //we need to save its reference to a uri variable
            pickedImgUri = data.getData();
            popup_post_img.setImageURI(pickedImgUri);
        }
    }

    private void iniPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //ini popup widget
        popup_user_image = popAddPost.findViewById(R.id.popup_user_image);
        popup_post_img = popAddPost.findViewById(R.id.popup_post_img);
        popup_add = popAddPost.findViewById(R.id.popup_add);
        popup_title = popAddPost.findViewById(R.id.popup_title);
        popup_description = popAddPost.findViewById(R.id.popup_description);
        popup_progressBar = popAddPost.findViewById(R.id.popup_progressBar);

        //load current user profile photo
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popup_user_image);

        //Add post click listner
        popup_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup_add.setVisibility(View.INVISIBLE);
                popup_progressBar.setVisibility(View.VISIBLE);

                //we need test whether all fields are field or not

                if(!popup_title.getText().toString().equals("")&&!popup_description.getText().toString().equals("")&&pickedImgUri!=null){
                    //everything is okay
                    //now add it to the firebase
                    //first we need to upload the posted image

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    //creating post object
                                    Post post = new Post(popup_title.getText().toString(),popup_description.getText().toString(),imageDownloadLink,currentUser.getUid(),currentUser.getPhotoUrl().toString());
                                    //add post to firebase database
                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //something went wrong while uploading the picture
                                    showMessage(e.getMessage());
                                    popup_progressBar.setVisibility(View.INVISIBLE);
                                    popup_add.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
                else {
                    popup_add.setVisibility(View.VISIBLE);
                    popup_progressBar.setVisibility(View.INVISIBLE);
                    showMessage("Please verify all the input fields and add image to be posted");
                }
            }
        });
    }
    

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference("Posts").push();
        //get post unique id and update post key
        String key = mRef.getKey();
        post.setPostKey(key);
        //add post data to firebase database
        mRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Posted");
                popup_progressBar.setVisibility(View.INVISIBLE);
                popup_add.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_username = headerView.findViewById(R.id.nav_username);
        TextView nav_user_mail = headerView.findViewById(R.id.nav_user_mail);
        ImageView nav_user_photo = headerView.findViewById(R.id.nav_user_photo);

        nav_user_mail.setText(currentUser.getEmail());
        nav_username.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library
        Glide.with(this).load(currentUser.getPhotoUrl()).into(nav_user_photo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}