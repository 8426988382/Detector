package com.example.detector;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiGetDetail extends AsyncTask<Void, Void, String[]> {

    private Util util;
    private ProgressDialog dialog;
    private WeakReference<Context> contextRef;
    public AsynchResponseGetDetails asyncResponse= null;
    private String Req;
    private String Res="";

    ApiGetDetail(Context context, String Req){
        contextRef= new WeakReference<>(context);
        this.Req= Req;
        util= new Util();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context sContext= contextRef.get();
        asyncResponse= (AsynchResponseGetDetails) contextRef.get();
        dialog = new ProgressDialog(sContext);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
    }


    @Override
    protected String[] doInBackground(Void... voids) {

        String[] infoArr = new String[0];
        String ReqUrl= util.getDataUrl();

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES); // read timeout

        client= builder.build();

        RequestBody body = RequestBody.create(MEDIA_TYPE, Req);
        Log.e("Body", Req);
        Request request = new Request.Builder()
                .url(ReqUrl).post(body).build();

       // String test= request.toString();
       // Log.e("TEST", test);

        Response response;
        JSONObject ResObj;
        String OwnerName, RegDate, RegNumber, InsUpTo, VehicleClass, FuelType, FuelNorms, NocDetail, RoadTextPaidUTo, FitnessUpTo, EngNumber, ChasNumber, Marker;

        try {
            response = client.newCall(request).execute();
            Log.e("response", Res);
            assert response.body() != null;
            Res= response.body().string();

            ResObj= new JSONObject(Res);

//            "chassisNumber": "ME3U3S5C2KB4XXXXX",
//                    "engineNumber": "U3S5C2KB4XXXXX",
//                    "fitnessUpto": "10-Apr-2034",
//                    "fuelNorms": "BHARAT STAGE IV",
//                    "fuelType": "PETROL",
//                    "insuranceUpto": "09-Apr-2024",
//                    "makerOrModel": "ROYAL-ENFIELD (UNIT OF EICHER LTD) / CLASSIC 350 ABS",
//                    "msg": "0",
//                    "nocDetails": "",
//                    "ownerName": "NIRANJAN",
//                    "registrationDate": "11-Apr-2019",
//                    "registrationNumber": "RJ21AS2160",
//                    "roadTaxPaidUpto": "",
//                    "vehicleClass": "M-CYCLE/SCOOTER (2WN)"

            Log.e("Data Response", ResObj.toString());

            String Msg= ResObj.getString("msg");

            if(Msg.length() > 100){
                infoArr= null;
            }else{
                ChasNumber= ResObj.getString("chassisNumber");
                EngNumber= ResObj.getString("engineNumber");
                FitnessUpTo=  ResObj.getString("fitnessUpto");
                FuelNorms=  ResObj.getString("fuelNorms");
                FuelType=  ResObj.getString("fuelType");
                InsUpTo=  ResObj.getString("insuranceUpto");
                Marker=  ResObj.getString("makerOrModel");
                NocDetail=  ResObj.getString("nocDetails");
                OwnerName=  ResObj.getString("ownerName");
                RegDate=  ResObj.getString("registrationDate");
                RegNumber= ResObj.getString("registrationNumber");
                RoadTextPaidUTo=  ResObj.getString("roadTaxPaidUpto");
                VehicleClass=  ResObj.getString("vehicleClass");

                infoArr= new String[]{OwnerName, RegDate, RegNumber, InsUpTo, VehicleClass, FuelType, FuelNorms, NocDetail, RoadTextPaidUTo, FitnessUpTo, EngNumber, ChasNumber, Marker};
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

       return infoArr;
    }

    @Override
    protected void onPostExecute(String[] aVoid) {
        super.onPostExecute(aVoid);
        if(dialog.isShowing()){
            dialog.cancel();
        }

        asyncResponse.getResults(aVoid);
    }
}
