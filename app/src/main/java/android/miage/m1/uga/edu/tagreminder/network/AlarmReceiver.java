package android.miage.m1.uga.edu.tagreminder.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel.NotificationBackgroundService;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        LigneTransport ligne = (LigneTransport) intent.getSerializableExtra("ligne");
        Arret arret = (Arret) intent.getSerializableExtra("arret");
        String direction = intent.getStringExtra("drection");

        Log.wtf("Alarm receiver", ligne.getId() + " " + arret.getName() + " " + direction);

        Intent i = new Intent(context, NotificationBackgroundService.class);
        i.putExtra("ligne", ligne);
        i.putExtra("arret", arret);
        i.putExtra("direction", direction);
        context.startService(i);
    }
}
