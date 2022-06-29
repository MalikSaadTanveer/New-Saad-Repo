package com.saad.genderandemotionrecognizer.mvvm.interfaces;


import com.saad.genderandemotionrecognizer.mvvm.capsules.response.PredictionResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NetworkCalls {

    @Multipart
    @POST("predict")
    Call<PredictionResponse> upload(
            @Part MultipartBody.Part file
    );
}
