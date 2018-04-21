package android.miage.m1.uga.edu.tagreminder.network.api;

import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MetromobiliteAPI {

    @GET("routers/default/index/routes/")
    Call<List<LigneTransport>> getLignesData();

    @GET("https://data.metromobilite.fr/api/routers/default/index/routes/{id}/clusters")
    Call<List<Arret>> getArretsOfALigne(@Path("id") String id);

}
