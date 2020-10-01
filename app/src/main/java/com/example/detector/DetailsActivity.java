package com.example.detector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.security.acl.Owner;

public class DetailsActivity extends AppCompatActivity{

    String[] infoArr;
    private TextView NumberPlate;
    private EditText mOwnerName, mRegDate, mInsUpTo,mVehicleClass, mFuelType, mFuelNorms, mRoadTextPaidUTo, mFitnessUpTo, mEngNumber, mChasNumber, mMarker, mNocDetail;
    private String OwnerName="", RegDate="", RegNumber="", InsUpTo="", VehicleClass="", FuelType="", FuelNorms="", NocDetail="", RoadTextPaidUTo="", FitnessUpTo="", EngNumber="", ChasNumber="", Marker="";

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initialise();

        bundle = getIntent().getExtras();

        infoArr= bundle.getStringArray("Data");


        assert infoArr != null;
        OwnerName= infoArr[0];
        RegDate= infoArr[1];
        RegNumber= infoArr[2];
        InsUpTo= infoArr[3];
        VehicleClass= infoArr[4];
        FuelType= infoArr[5];
        FuelNorms= infoArr[6];
        NocDetail= infoArr[7];
        RoadTextPaidUTo= infoArr[8];
        FitnessUpTo= infoArr[9];
        EngNumber= infoArr[10];
        ChasNumber= infoArr[11];
        Marker= infoArr[12];

        SetData();
    }

    void initialise(){
        NumberPlate= findViewById(R.id.textView7);
        mOwnerName= findViewById(R.id.edit1);
        mRegDate= findViewById(R.id.edit2);
        mInsUpTo= findViewById(R.id.edit3);
        mVehicleClass= findViewById(R.id.edit4);
        mFuelType= findViewById(R.id.edit5);
        mFuelNorms= findViewById(R.id.edit6);
        mRoadTextPaidUTo= findViewById(R.id.edit7);
        mFitnessUpTo= findViewById(R.id.edit8);
        mEngNumber= findViewById(R.id.edit9);
        mChasNumber= findViewById(R.id.edit10);
        mMarker= findViewById(R.id.edit11);
        mNocDetail= findViewById(R.id.edit12);
    }

    void SetData(){
        if(RegNumber.equals("")){
            NumberPlate.setText("NA");
        }else{
            NumberPlate.setText(RegNumber);
        }

        if(OwnerName.equals("")){
            mOwnerName.setText("NA");
        }else{
            mOwnerName.setText(OwnerName);
        }

        if(RegDate.equals("")){
            mRegDate.setText("NA");
        }else{
            mRegDate.setText(RegDate);
        }

        if(InsUpTo.equals("")){
            mInsUpTo.setText("NA");
        }else{
            mInsUpTo.setText(InsUpTo);
        }

        if(VehicleClass.equals("")){
            mVehicleClass.setText("NA");
        }else{
            mVehicleClass.setText(VehicleClass);
        }

        if(FuelType.equals("")){
            mFuelType.setText("NA");
        }else{
            mFuelType.setText(FuelNorms);
        }

        if(FuelNorms.equals("")){
            mFuelNorms.setText("NA");
        }else{
            mFuelNorms.setText(FuelNorms);
        }

        if(RoadTextPaidUTo.equals("")){
            mRoadTextPaidUTo.setText("NA");
        }else{
            mRoadTextPaidUTo.setText(RoadTextPaidUTo);
        }

        if(FitnessUpTo.equals("")){
            mFitnessUpTo.setText("NA");
        }else{
            mFitnessUpTo.setText(FitnessUpTo);
        }




        if(EngNumber.equals("")){
            mEngNumber.setText("NA");
        }else{
            mEngNumber.setText(EngNumber);
        }

        if(ChasNumber.equals("")){
            mChasNumber.setText("NA");
        }else{
            mChasNumber.setText(ChasNumber);
        }


        if(Marker.equals("")){
            mMarker.setText("NA");
        }else{
            mMarker.setText(Marker);
        }

        if(NocDetail.equals("")){
            mNocDetail.setText("NA");
        }else{
            mNocDetail.setText(NocDetail);
        }
    }
}