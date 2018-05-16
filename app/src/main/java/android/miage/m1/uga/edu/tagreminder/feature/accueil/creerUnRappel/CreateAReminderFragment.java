package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.Favoris;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;
import android.miage.m1.uga.edu.tagreminder.network.AlarmReceiver;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CreateAReminderFragment extends Fragment {

    private static LigneTransport ligne;
    private static Arret arret;
    private Favoris fav;

    private List<Passage> passages = new ArrayList<Passage>();
    private List<String> lignes = new ArrayList<String>();
    private List<String> directions = new ArrayList<String>();
    private List<Favoris> favoritesList = new ArrayList<Favoris>();

    private Spinner spinDirections;
    private TextView txtLigneName;
    private TextView txtProchainPassage;
    private TextView txtPassageSuivant;
    private CheckBox checkAddToFavorites;
    private CheckBox checkActivateReminder;

    private String direction;
    private String timeToProchainPassage;
    private String timeToPassageSuivant;

    private Intent alarmIntent;
    private PendingIntent pendingIntent;

    public static CreateAReminderFragment newInstance(LigneTransport ligneToAdd, Arret arretToAdd) {
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

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_a_reminder, container, false);

        txtLigneName = (TextView) view.findViewById(R.id.txt_arret_name);
        txtLigneName.setText(arret.getName());

        txtProchainPassage = (TextView) view.findViewById(R.id.txt_time_to_prochain_passage);
        txtPassageSuivant = (TextView) view.findViewById((R.id.txt_time_to_prochain_prochain_passage));

        checkActivateReminder = (CheckBox) view.findViewById(R.id.check_activer_rappel);
        checkAddToFavorites = (CheckBox) view.findViewById(R.id.check_ajouter_favoris);

        Spinner spinLignes = (Spinner) view.findViewById(R.id.spin_lignes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lignes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLignes.setAdapter(adapter);

        spinDirections = (Spinner) view.findViewById((R.id.spin_directions));

        fetchFavorites();
        fetchPassageData(view);
        initCheckBoxListener();

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
                if (t instanceof IOException) {
                    String message = "Pas de connexion internet";
                    final Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.create_reminder_content), message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Réessayer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(snackbar != null && snackbar.isShown()){
                                snackbar.dismiss();
                                fetchFavorites();
                                fetchPassageData(view);
                                initCheckBoxListener();
                            }
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
                else {
                    Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fetchFavorites(){
        favoritesList.clear();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        Map<String, ?> map = sharedPreferences.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()){
            favoritesList.add(gson.fromJson(String.valueOf(entry.getValue()), Favoris.class));
        }
    }

    private boolean isFavorite(Favoris favorisToCheck){
        boolean res = false;
        for(Favoris favoris : favoritesList){
            if(favorisToCheck.getArret().toString().equals(favoris.getArret().toString())
                    && favorisToCheck.getLigne().toString().equals(favoris.getLigne().toString())
                    && favorisToCheck.getDirection().equals(favoris.getDirection())){
                res = true;
                break;
            }
        }
        return res;
    }

    private void updateProchainPassageByDirection(String direction){
        timeToProchainPassage = String.valueOf(R.string.time_to_prochain_passage);
        for(Passage passage : passages){
            if((passage.getPattern().getDesc().toString().contains(direction)) && (passage.getTimes().get(0).getRealtimeArrival() != null)){
                timeToProchainPassage = DateUtils.formatElapsedTime(passage.getTimes().get(0).getRealtimeArrival());
                break;
            }
        }
        this.direction = direction;
        txtProchainPassage.setText(timeToProchainPassage);
    }

    private void updatePassageSuivantByDirection(String direction){
        timeToPassageSuivant = String.valueOf(R.string.time_to_prochain_passage);
        for(Passage passage : passages){
            if((passage.getPattern().getDesc().toString().contains(direction)) && (passage.getTimes().size() > 1)){
                timeToPassageSuivant = DateUtils.formatElapsedTime(passage.getTimes().get(1).getRealtimeArrival());
                break;
            }
        }
        this.direction = direction;
        txtPassageSuivant.setText(timeToPassageSuivant);
    }

    private void updateDirections(View view) {
        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, directions);
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDirections.setAdapter(directionAdapter);

        spinDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProchainPassageByDirection(directions.get(position));
                updatePassageSuivantByDirection(directions.get(position));

                fav = new Favoris(arret, ligne, directions.get(position));
                toogleCheckFavorites(isFavorite(fav));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCheckBoxListener() {
        checkActivateReminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checkActivateReminder.isChecked()){
                    // TODO : start a background service that udpate the next passages
                    if(timeToPassageSuivant == null){
                        checkActivateReminder.toggle();
                    }
                    else{
                        // DEBUG
                        // startReminder();
                        initNotification();
                        // DEBUG
                    }
                }
                else{
                    // TODO : kill the background service
                    cancelReminder();
                }
            }
        });

        checkAddToFavorites.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(checkAddToFavorites.isChecked()){
                    fav = new Favoris(arret, ligne, direction);
                    addToFavorites(fav);
                    fetchFavorites();
                }
                else{
                    deleteFromFavorites(fav);
                    fetchFavorites();
                }
            }
        });
    }

    public void startReminder() {
        alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("ligne", ligne);
        alarmIntent.putExtra("arret", arret);
        alarmIntent.putExtra("direction", direction);

        pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 30);
        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), calendar.getTimeInMillis(), pendingIntent);

        initNotification();

        Toast.makeText(getActivity(), "Suivi activé", Toast.LENGTH_SHORT).show();
    }

    private void initNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());
        builder.setSmallIcon(R.drawable.ic_alarm_white_24dp);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Prochain passage : " + timeToProchainPassage);
        builder.setContentText(ligne.getType() + " " + ligne.getShortName() + " - " + arret.getName());

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }

    public void cancelReminder() {
        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);

        Toast.makeText(getActivity(), "Suivi désactivé", Toast.LENGTH_SHORT).show();
    }

    private void toogleCheckFavorites(boolean favorite){
        if(favorite == true){
            checkAddToFavorites.setChecked(true);
        }
        else{
            checkAddToFavorites.setChecked(false);
        }
    }

    private void addToFavorites(Favoris favoriteToAdd){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        sharedPreferences.edit().putString(favoriteToAdd.toString(), gson.toJson(favoriteToAdd)).apply();
    }

    private void deleteFromFavorites(Favoris favoriteToDelete){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(favoriteToDelete.toString()).apply();
    }
}
