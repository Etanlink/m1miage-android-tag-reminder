package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationBackgroundService extends IntentService {

    private LigneTransport ligne;
    private Arret arret;
    private String direction;

    public NotificationBackgroundService() {
        super("NotificationBackgroundService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        this.ligne = (LigneTransport) intent.getSerializableExtra("ligne");
        this.arret = (Arret) intent.getSerializableExtra("arret");
        this.direction = intent.getStringExtra("drection");

        Log.wtf("Service notification", ligne.getId() + " " + arret.getName() + " " + direction);
    }

//    private void sendNotification() {
//        // TODO : setStyle for custom notification
//        // TODO : parse the time in "15 min" for notification
//
//        PendingIntent stopAlarmIntent = null;
//        Notification notification = new NotificationCompat().Builder()
//                // Show controls on lock screen even when user hides sensitive content.
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
//                // Add media control buttons that invoke intents in your media service
//                .addAction(R.drawable.ic_alarm_off_24dp, "Stop", stopAlarmIntent) // #0
//                .setContentTitle("Coucou")
//                .setContentText("Prochain passage dans XX min")
//                .setAutoCancel(true)
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(1,notification);
//
//        Log.wtf("Alarme activé", "Temps : " + SystemClock.currentThreadTimeMillis());
//
//    }
}
