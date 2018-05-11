package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.miage.m1.uga.edu.tagreminder.MainActivity;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.Favoris;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CreateAReminderFragment extends Fragment {

    static LigneTransport ligne;
    static Arret arret;

    List<Passage> passages = new ArrayList<Passage>();

    List<String> lignes = new ArrayList<String>();
    List<String> directions = new ArrayList<String>();

    TextView txtProchainPassage;
    TextView txtPassageSuivant;
    int direction;

    public static CreateAReminderFragment newInstance(LigneTransport ligneToAdd, Arret arretToAdd) {
        // DEBUG
        Log.wtf("Ligne récupérée", ligneToAdd.toString());
        Log.wtf("Arrêt récupéré", arretToAdd.toString());

        Bundle args = new Bundle();
        args.putSerializable(ligneToAdd.getId(), (Serializable) ligneToAdd);
        args.putSerializable(arretToAdd.getCode(), (Serializable) arretToAdd);

        CreateAReminderFragment fragment = new CreateAReminderFragment();
        fragment.setArguments(args);

        ligne = (LigneTransport) args.getSerializable(ligneToAdd.getId());
        arret = (Arret) args.getSerializable(arretToAdd.getCode());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lignes.add(ligne.getType() + " " + ligne.getShortName());

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_a_reminder, container, false);

        /* INIT */
        TextView txtLigneName = (TextView) view.findViewById(R.id.txt_arret_name);
        txtLigneName.setText(arret.getName());

        txtProchainPassage = (TextView) view.findViewById(R.id.txt_time_to_prochain_passage);
        txtPassageSuivant = (TextView) view.findViewById((R.id.txt_time_to_prochain_prochain_passage));

        Spinner spinLignes = (Spinner) view.findViewById(R.id.spin_lignes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lignes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLignes.setAdapter(adapter);

        fetchPassageData(view);

        initButtons(view);

        return view;
    }

    private void fetchPassageData(final View view) {
        passages.clear();

        MetromobiliteAPI service = RetrofitInstance.getRetrofitInstance().create(MetromobiliteAPI.class);

        Call<List<Passage>> call = service.getPassageByAStop(arret.getCode());

        Log.wtf("URL called", call.request().url() + "");

        call.enqueue(new Callback<List<Passage>>() {
            @Override
            public void onResponse(Call<List<Passage>> call, Response<List<Passage>> response) {
                if (response==null){
                    Toast.makeText(getActivity(), "Something Went Wrong...!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (Passage passage : response.body()){
                        if(passage.getPattern().getId().contains(ligne.getId())){
                            passages.add(passage);
                            directions.add(String.valueOf(passage.getPattern().getDesc()));
                        }
                    }
                    updateDirections(view);
                }
            }

            public void onFailure(Call<List<Passage>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

    private void updateProchainPassageByDirection(int direction){
        String timeToProchainPassage = String.valueOf(R.string.time_to_prochain_passage);
        for(Passage passage : passages){
            if((passage.getPattern().getDir().equals(direction)) && (passage.getTimes().get(0).getRealtimeArrival() != null)){
                timeToProchainPassage = DateUtils.formatElapsedTime(passage.getTimes().get(0).getRealtimeArrival());
                break;
            }
        }
        txtProchainPassage.setText(timeToProchainPassage);
    }

    private void updatePassageSuivantByDirection(int direction){
        String timeToPassageSuivant = String.valueOf(R.string.time_to_prochain_passage);
        for(Passage passage : passages){
            if((passage.getPattern().getDir().equals(direction)) && (passage.getTimes().size() > 1)){
                timeToPassageSuivant = DateUtils.formatElapsedTime(passage.getTimes().get(1).getRealtimeArrival());
                break;
            }
        }
        txtPassageSuivant.setText(timeToPassageSuivant);
    }

    private void updateDirections(View view) {
        Spinner spinDirections = (Spinner) view.findViewById((R.id.spin_directions));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, directions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDirections.setAdapter(adapter);

        spinDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProchainPassageByDirection(position + 1);
                updatePassageSuivantByDirection(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void sendNotification(View view){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.ic_alarm_24dp);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Prochain passage :");
        builder.setContentText("15min");
        builder.setSubText("30min");

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());

    }

    /** BUTTONS **/

    private void initButtons(final View view) {
        final CheckBox checkActivateReminder = (CheckBox) view.findViewById(R.id.check_activer_rappel);
        checkActivateReminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checkActivateReminder.isChecked()){

                    // TODO : start a background service that udpate the next passages
                    sendNotification(view);
                }
                else{
                    // TODO : kill the background service
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager) getContext().getSystemService(ns);
                    nMgr.cancel(1);
                }
            }
        });

        final CheckBox checkAddToFavorites = (CheckBox) view.findViewById(R.id.check_ajouter_favoris);
        checkAddToFavorites.setOnClickListener(new View.OnClickListener() {

            Favoris favoris = null;

            public void onClick(View v) {
                if(checkAddToFavorites.isChecked()){
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    Gson gson = new Gson();

                    favoris = new Favoris(arret, ligne, direction);

                    sharedPreferences.edit()
                            .putString("fav" + String.valueOf(favoris.hashCode()), gson.toJson(favoris))
                            .apply();
                }
                else{
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove("fav" + String.valueOf(favoris.hashCode()));
                }
            }
        });

    }
}
