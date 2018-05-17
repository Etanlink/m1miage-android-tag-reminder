package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationBackgroundService extends IntentService {

    private String ligneId;
    private String ligneType;
    private String ligneShortName;
    private String arretName;
    private String arretCode;
    private String direction;

    private String timeToProchainPassage;

    public NotificationBackgroundService() {
        super("NotificationBackgroundService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        this.ligneId = intent.getStringExtra("ligneId");
        this.ligneType = intent.getStringExtra("ligneType");
        this.ligneShortName = intent.getStringExtra("ligneShortName");
        this.arretName = intent.getStringExtra("arretName");
        this.arretCode = intent.getStringExtra("arretCode");
        this.direction = intent.getStringExtra("direction");

        refreshNotification();
    }

    private void refreshNotification() {
        // TODO : get data through Retrofit
        fetchPassageData();
        // TODO : handle the null case for the Time
    }

    private String fetchPassageData() {

        MetromobiliteAPI service = RetrofitInstance.getRetrofitInstance().create(MetromobiliteAPI.class);

        Call<List<Passage>> call = service.getPassageByAStop(arretCode);

        Log.wtf("URL called", call.request().url() + "");

        call.enqueue(new Callback<List<Passage>>() {
            @Override
            public void onResponse(Call<List<Passage>> call, Response<List<Passage>> response) {
                if (response==null){
                    Toast.makeText(getApplication(), "Something Went Wrong...!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (Passage passage : response.body()){
                        if(passage.getPattern().getId().contains(ligneId) && passage.getPattern().getDesc().contains(direction)){
                            if((passage.getTimes().get(0).getRealtimeArrival() != null)){
                                timeToProchainPassage = DateUtils.formatElapsedTime(passage.getTimes().get(0).getRealtimeArrival());
                            }
                        }
                    }
                    if(timeToProchainPassage.equals(null)){
                        timeToProchainPassage = "Oups, pas de passage pour l'instant.";
                    }
                    sendNotification();
                }
            }

            public void onFailure(Call<List<Passage>> call, Throwable t) {
                if (t instanceof IOException) {
                    timeToProchainPassage = "Oups, pas de connexion internet.";
                }
                else {
                    Toast.makeText(getApplication(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return timeToProchainPassage;
    }

    private void sendNotification() {
        // TODO : setStyle for custom notification
        // TODO : parse the time in "15 min" for notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_alarm_white_24dp);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Prochain passage : " + this.timeToProchainPassage);
        builder.setContentText(ligneType + " " + ligneShortName + " - " + arretName);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());

        Log.wtf("Alarme activé", "Temps : " + SystemClock.currentThreadTimeMillis());

    }
}
