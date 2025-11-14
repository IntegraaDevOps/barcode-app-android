package net.integraa.read.network;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IntegraaApi {
    @Headers("Cache-Control: no-cache")
    @Multipart
    @POST("login_read")
    Call<LoginData> doLogin(@Part("username") RequestBody username,
                               @Part("password") RequestBody password);

    @Headers("Cache-Control: no-cache")
    @GET("login_read")
    Call<ReadData> getReads(@Header("token") String token);

    @Headers("Cache-Control: no-cache")
    @Multipart
    @POST("login_read")
    Call<ReadData> addRead(@Header("token") String token,
                            @Part("serial") RequestBody serial,
                            @Part("read") RequestBody read);
}
