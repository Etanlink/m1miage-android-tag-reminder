package android.miage.m1.uga.edu.tagreminder.network.api;

import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MetromobiliteAPI {

    @GET("routers/default/index/routes/")
    Call<List<LigneTransport>> getLignesData();

    @GET("routers/default/index/routes/{ligneId}/clusters")
    Call<List<Arret>> getArretsByALigne(@Path("ligneId") String ligneId);

    @GET("routers/default/index/clusters/{codeArret}/stoptimes")
    Call<List<Passage>> getPassageByAStop(@Path("codeArret") String codeArret);

}
