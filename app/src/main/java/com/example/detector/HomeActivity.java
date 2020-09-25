package com.example.detector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private Button mCameraBtn, mConfirmBtn;
    private static final int pic_id = 1088;
    private ImageView mClickedImage;
    private ImageView singOut;

    CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this , R.color.login));

        // initialise components
        initialise();

        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchCamera();
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmTask();
            }
        });
        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singOutUser();
            }
        });
    }


    private void initialise(){
        mCameraBtn= findViewById(R.id.button2);
        mConfirmBtn= findViewById(R.id.button3);
        mClickedImage= findViewById(R.id.clicked_img);
        singOut= findViewById(R.id.sign_out);
    }

    private void LaunchCamera(){
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera_intent, pic_id);
    }

    private void mConfirmTask(){
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            mClickedImage.setImageBitmap(photo);
            mConfirmBtn.setVisibility(View.VISIBLE);
        }

    }

    private void singOutUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                        firebaseAuth = FirebaseAuth.getInstance();
                        callbackManager = CallbackManager.Factory.create();
                        mGoogleSignInClient = GoogleSignIn.getClient(HomeActivity.this,  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id)).build());
                        mGoogleSignInClient.signOut();
                        firebaseAuth.signOut();
                        LoginManager.getInstance().logOut();
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        //if user is signed in, we call a helper method to save the user details to Firebase
                        if (user == null) {
                            // User is signed in
                            // you could place other firebase code
                            //logic to save the user details to Firebase
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Logout?");
        alert.show();
    }
}