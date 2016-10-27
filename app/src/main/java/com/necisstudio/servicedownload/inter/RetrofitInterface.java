package com.necisstudio.servicedownload.inter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface RetrofitInterface {

    @GET("d/WRMMKkSZ/1685162/NS480ID-SAMEHADAKU.NET.mp4")
    @Streaming
    Call<ResponseBody> downloadFile();
}
