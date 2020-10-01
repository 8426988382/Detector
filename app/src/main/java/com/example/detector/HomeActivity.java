package com.example.detector;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity  implements IPickResult,View.OnClickListener, AysncResponse, AsynchResponseGetDetails{

    private Button mConfirmBtn, mCheckBtn;
    private EditText PlateEdit;
    private ImageView mClickedImage;
    private ImageView singOut;
    private ProgressBar progressBar;
    private Context context;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;

    private String token = "";
    private String PlateText= "";
    private Date date;
    private DateFormat df;
    private Util util;
    private View popupInputDialogView;
    private EditText captchaView;
    private ImageView captchaImg;
    private Button ConfirmBtn;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Using Full Screen
        final Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this , R.color.login));

        // initialise components
        initialise();


        // Setting an alert dialog for Captcha
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        initPopupViewControls();
        builder.setView(popupInputDialogView);
        alertDialog = builder.create();

        // Sign Out the User
        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singOutUser();
            }
        });

        // This is for selecting Image
        mClickedImage.setOnClickListener(this);

        // Using Confirm Button
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmImage();
            }
        });

        // Check Using Text (directly check using text without any  image)
        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(PlateEdit.getText().toString().trim().equals("")){
                    PlateEdit.setError("Invalid Input");
                }else{
                    PlateText=  PlateEdit.getText().toString().trim();
                    ConfirmImage();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        PickImageDialog.build(setup).show(HomeActivity.this);
    }

    // Dialog box setup
    @SuppressLint("WrongConstant")
    PickSetup setup = new PickSetup()
            .setTitle("Choose")
            .setCancelText("Cancel")
            .setFlip(true)
            .setMaxSize(50)
            .setWidth(50)
            .setHeight(50)
            .setProgressText("Loading Image")
            .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
            .setCameraButtonText("Camera")
            .setGalleryButtonText("Gallery")
            .setIconGravity(Gravity.TOP)
            .setButtonOrientation(LinearLayoutCompat.HORIZONTAL)
            .setSystemDialog(false)
            .setGalleryIcon(R.drawable.photo)
            .setCameraIcon(R.drawable.cam);


    @SuppressLint("SimpleDateFormat")
    private void initialise(){
        util= new Util();
        context= HomeActivity.this;
        date = new Date();
        df = new SimpleDateFormat("MM/dd/");
        token= util.getToken();
        mClickedImage= findViewById(R.id.clicked_img);
        singOut= findViewById(R.id.sign_out);
        mCheckBtn= findViewById(R.id.button);
        progressBar= findViewById(R.id.homeprogress);
        mConfirmBtn= findViewById(R.id.button2);
        // Use London time zone to format the date in
        df.setTimeZone(TimeZone.getTimeZone("Etc/IST"));
        PlateEdit= findViewById(R.id.DirectText);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (token.equals("")){
            Toast.makeText(context, "Some error has occurred", Toast.LENGTH_SHORT).show();
        }else {
            WebRequest.client.addHeader("Authorization","Token "+token);
        }
    }


    //pick result method to get image after getting image form gallery or camera
    @Override
    public void onPickResult(PickResult r){

        if(r.getError() == null){
            RequestParams params= new RequestParams();
            String file= r.getPath();
            String compressed= compressImage(file);

            String CountryCode= util.getCountryCode();
            String baseUrl= util.getBaseUrl();

            try {
                params.put("upload", new File(compressed));
                mClickedImage.setImageBitmap(BitmapFactory.decodeFile(compressed));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            params.put("regions", CountryCode);

            WebRequest.post(context, baseUrl, params, new JsonHttpResponseHandler()
            {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                   // mClickedImage.setImageResource(R.drawable.add3);
                    super.onStart();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONArray jsonArray= response.getJSONArray("results");
                        if(jsonArray.length() > 0){
                            for(int i =0;i < jsonArray.length();i ++){
                                JSONObject jsonObject= jsonArray.getJSONObject(i);
                                PlateText= jsonObject.getString("plate");
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mConfirmBtn.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.e("ERROR ", "onFailure: " + errorResponse + " ");

                    progressBar.setVisibility(View.GONE);
                    mClickedImage.setImageResource(R.drawable.add3);
                    mConfirmBtn.setVisibility(View.GONE);

                    Toast.makeText(HomeActivity.this, errorResponse+"", Toast.LENGTH_SHORT).show();
                }

            });
        }
        else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public String compressImage(String filePath) {

        int resized=60;

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight =resized*7.0f;
        float maxWidth = resized*12.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth)
        {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;

            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth,
                actualHeight);
        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth,
                    actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        //      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, resized, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }


    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), ".Foldername/PlateRecognizerHistory");

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height/ (float)
                    reqHeight);

            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = Math.min(heightRatio, widthRatio);

        }       final float totalPixels = width * height;

        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }


    public void ConfirmImage(){
        // Perform Your Action

        if(PlateText.equals("")){
            Toast.makeText(HomeActivity.this, "Plate Number is Invalid", Toast.LENGTH_SHORT).show();
        }else{
            ApiCaptcha apiCaptcha= new ApiCaptcha(context, PlateText);
            apiCaptcha.execute();
        }
    }


    @Override
    public void processFinish(String[] object) {

        alertDialog.show();

        final String[] ResString= object;
        final String id= ResString[0];

        Bitmap imageBit= StringToBitMap(ResString[1]);


        captchaImg.setImageBitmap(imageBit);

//        Log.e("Captcha", inputCaptcha);

        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(captchaView.getText().toString().trim().equals("")){
                    Toast.makeText(HomeActivity.this, "Please Verify", Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("Res ", id + " " + ResString[1]);

                    // Json Structure
                    //{"id":"1","ans":"kbedy"}

                    JSONObject jsonObject= new JSONObject();


                    try {
                        jsonObject.put("id", id);
                        jsonObject.put("ans", captchaView.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String JsonString= jsonObject.toString();

                    ApiGetDetail apiGetDetail= new ApiGetDetail(HomeActivity.this, JsonString);
                    apiGetDetail.execute();

                }
            }
        });

    }

    public void initPopupViewControls(){
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);

        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.captcha, null);
        captchaImg= popupInputDialogView.findViewById(R.id.imageView3);
        captchaView= popupInputDialogView.findViewById(R.id.editText);
        ConfirmBtn= popupInputDialogView.findViewById(R.id.button3);

    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
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

    @Override
    public void getResults(String[] object) {

        if(object == null || object.length == 0 ){
            Toast.makeText(HomeActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent= new Intent(HomeActivity.this, DetailsActivity.class);
            intent.putExtra("Data", object);
            startActivity(intent);
            alertDialog.dismiss();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(alertDialog.isShowing())
        alertDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(alertDialog.isShowing())
        alertDialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }
}