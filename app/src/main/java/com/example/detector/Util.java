package com.example.detector;

public class Util {
    private String token = "2f45f84c32d4382e345e2ec3e9222508b1bcb7be";
    private String baseUrl = "https://api.platerecognizer.com/v1/plate-reader/";
    private String CaptchaUrl = "https://nameplatedetector1.herokuapp.com/";
    private String DataUrl= "https://nameplatedetector1.herokuapp.com/getdata";
    private String CountryCode= "in";


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCaptchaUrl() {
        return CaptchaUrl;
    }

    public void setCaptchaUrl(String captchaUrl) {
        CaptchaUrl = captchaUrl;
    }

    public String getDataUrl() {
        return DataUrl;
    }

    public void setDataUrl(String dataUrl) {
        DataUrl = dataUrl;
    }
}
