package android.miage.m1.uga.edu.tagreminder.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel.NotificationBackgroundService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String ligneId = intent.getStringExtra("ligneId");
        String ligneType = intent.getStringExtra("ligneType");
        String ligneShortName = intent.getStringExtra("ligneShortName");
        String arretName = intent.getStringExtra("arretName");
        String arretCode = intent.getStringExtra("arretCode");
        String direction = intent.getStringExtra("direction");

        Intent i = new Intent(context, NotificationBackgroundService.class);
        i.putExtra("ligneId", ligneId);
        i.putExtra("ligneType", ligneType);
        i.putExtra("ligneShortName", ligneShortName);
        i.putExtra("arretName", arretName);
        i.putExtra("arretCode", arretCode);
        i.putExtra("direction", direction);
        context.startService(i);
    }
}
