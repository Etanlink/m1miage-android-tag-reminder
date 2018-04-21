package android.miage.m1.uga.edu.tagreminder.network.api;

import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MetromobiliteAPI {

    @GET("routers/default/index/routes/")
    Call<List<LigneTransport>> getLigneTransportData();

}
