package com.example.detector;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiCaptcha extends AsyncTask<Void, Void, String[]>{

    private Util util;
    private ProgressDialog dialog;
    private WeakReference<Context> contextRef;
    public AysncResponse asyncResponse= null;
    private String Req;
    private String Res="";

    ApiCaptcha(Context context, String req){
        util= new Util();
        contextRef =new WeakReference<> (context);
        this.Req= req;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context sContext= contextRef.get();

        dialog = new ProgressDialog(sContext);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected String[] doInBackground(Void... voids) {

        String[] infoArr;
        asyncResponse= (AysncResponse) contextRef.get();

        String URL= util.getCaptchaUrl();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject= new JSONObject();

        try {
            jsonObject.put("nameplateno",Req);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, jsonObject.toString());
        //Log.e("Body", jsonObject.toString());
        Request request = new Request.Builder()
                .url(URL).post(body).build();

        String test= request.toString();
        Log.e("TEST", test);

        Response response;
        JSONObject ResObj;

        String ImageString = null, id= null;
        try {
            response = client.newCall(request).execute();
            Log.e("response", Res);
            assert response.body() != null;
            Res= response.body().string();

            ResObj= new JSONObject(Res);

            ImageString= ResObj.getString("msg");
            id= ResObj.getString("id");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //Log.e("Response", id + " " + ImageString);

        infoArr= new String[]{id, ImageString};

        return infoArr;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        //Log.e("Post Execute", strings[0] + " " + strings[1]);

        if(dialog.isShowing())
            dialog.hide();
        asyncResponse.processFinish(strings);
    }
}
