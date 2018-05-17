package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.Favoris;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAReminderFragmentRefactor extends Fragment {

    private static LigneTransport ligne;
    private static Arret arret;
    private Favoris fav;

    private List<String> lignes = new ArrayList<String>();
    private List<String> directions = new ArrayList<String>();
    private List<Passage> passages = new ArrayList<Passage>();
    private List<Favoris> favoritesList = new ArrayList<Favoris>();

    private TextView txtLigneName;
    private TextView txtProchainPassage;
    private TextView txtPassageSuivant;

    private Spinner spinLignes;
    private Spinner spinDirections;

    private CheckBox checkActivateReminder;
    private CheckBox checkAddToFavorites;

    private String selectedLigne;
    private String selectedDirection;
    private String timeToProchainPassage;
    private String timeToPassageSuivant;

    public static CreateAReminderFragmentRefactor newInstance(LigneTransport ligneToAdd, Arret arretToAdd) {
        Bundle args = new Bundle();
        args.putSerializable(ligneToAdd.getId(), (Serializable) ligneToAdd);
        args.putSerializable(arretToAdd.getCode(), (Serializable) arretToAdd);

        CreateAReminderFragmentRefactor fragment = new CreateAReminderFragmentRefactor();
        fragment.setArguments(args);

        ligne = (LigneTransport) args.getSerializable(ligneToAdd.getId());
        arret = (Arret) args.getSerializable(arretToAdd.getCode());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lignes.add(ligne.getShortName());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_a_reminder, container, false);

        txtLigneName = (TextView) view.findViewById(R.id.txt_arret_name);
        txtLigneName.setText(arret.getName());

        txtProchainPassage = (TextView) view.findViewById(R.id.txt_time_to_prochain_passage);
        txtPassageSuivant = (TextView) view.findViewById((R.id.txt_time_to_prochain_prochain_passage));

        checkActivateReminder = (CheckBox) view.findViewById(R.id.check_activer_rappel);
        handleOnClickListenerActivateReminderCheckbox();
        checkAddToFavorites = (CheckBox) view.findViewById(R.id.check_ajouter_favoris);
        handleOnClickListenerAddToFavoritesCheckbox();

        spinLignes = (Spinner) view.findViewById(R.id.spin_lignes);
        spinDirections = (Spinner) view.findViewById((R.id.spin_directions));

        /* Fetch favorites and check if the selected passage is a favorite or not */
        fetchFavorites();
        /* Fetch the alert for the given stop */
        fetchAlert();
        /* Fetch passages */
        fetchPassageData();

        /* Refresh needed passages data every 30 sec */
        // TODO : refresh data every 30sec
        // refreshDataEvery30sec();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO : refresh data every 30sec
        // refreshDataEvery30sec();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fetchFavorites() {
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

    private void fetchAlert() {
        // TODO : Avertir l'utilisateur lorsqu'un incident se produit impactant la ligne surveillée. Utilisez l'API "Données dynamiques" avec le paramètre evtTC.
    }

    private void fetchPassageData() {
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
                        passages.add(passage);
                        Log.wtf("Passage récupéré", passage.getPattern().getId() + " " + passage.getPattern().getDesc());
                    }

                    if(passages.size() > 0){
                        // update the UI
                        updateLignes();
                    }
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onFailure(Call<List<Passage>> call, Throwable t) {
                if (t instanceof IOException) {
                    String message = "Pas de connexion internet";
                    final Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.create_reminder_content), message, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Réessayer", new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            if(snackbar != null && snackbar.isShown()){
                                snackbar.dismiss();
                                fetchFavorites();
                                fetchPassageData();

                                handleOnClickListenerActivateReminderCheckbox();
                                handleOnClickListenerAddToFavoritesCheckbox();
                            }
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
                else {
                    Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }favoritesList.clear();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                Gson gson = new Gson();

                Map<String, ?> map = sharedPreferences.getAll();
                for(Map.Entry<String, ?> entry : map.entrySet()){
                    favoritesList.add(gson.fromJson(String.valueOf(entry.getValue()), Favoris.class));
                }
            }
        });
    }

    private void updateLignes() {
        // TODO : init spinLignes with ligne given and fill it with lignes gathered
        for(Passage passage : passages){
            if(!passage.getPattern().getId().contains(ligne.getId())){
                lignes.add(passage.getPattern().getId()); // TODO : split the string with regex
            }
        }
        ArrayAdapter<String> ligneAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lignes);
        ligneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLignes.setAdapter(ligneAdapter);

        spinLignes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLigne = lignes.get(position);
                updateDirectionsByLigne(selectedLigne);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateDirectionsByLigne(String selectedLigne) {
        directions.clear();
        for(Passage passage : passages){
            if(passage.getPattern().getId().contains(selectedLigne)){
                directions.add(passage.getPattern().getDesc());
            }
        }

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, directions);
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDirections.setAdapter(directionAdapter);

        spinDirections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDirection = directions.get(position);
                updateTimePassagesByDirection(selectedDirection);

                fav = new Favoris(arret, ligne, directions.get(position));
                toogleCheckFavorites(isFavorite(fav));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateTimePassagesByDirection(String selectedDirection) {

        timeToProchainPassage = String.valueOf(R.string.time_to_prochain_passage);
        timeToPassageSuivant = String.valueOf(R.string.time_to_prochain_passage);

        for(Passage passage : passages){
            if(passage.getPattern().getId().contains(selectedLigne) && passage.getPattern().getDesc().toString().contains(selectedDirection)){
                if(passage.getTimes().get(0).getRealtimeArrival() != null){
                    timeToProchainPassage = getTimeInMinutes(DateUtils.formatElapsedTime(passage.getTimes().get(0).getRealtimeArrival()));
                }
                // TODO : check if the second time is not more than the amount of millis in a day
                if(passage.getTimes().size() > 1 && passage.getTimes().get(1).getRealtimeArrival() != null){
                    timeToPassageSuivant = getTimeInMinutes(DateUtils.formatElapsedTime(passage.getTimes().get(1).getRealtimeArrival()));
                }
            }
        }
        txtProchainPassage.setText(timeToProchainPassage);
        txtPassageSuivant.setText(timeToPassageSuivant);
    }

    private String getTimeInMinutes(String timeToParse) {

        // TODO : get the remaining time in minutes if less than an hour (ex : 45min), else get the realtime in hour (ex : 6h05)

        return timeToParse;
    }

    private void handleOnClickListenerActivateReminderCheckbox() {
        checkActivateReminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checkActivateReminder.isChecked()){
                    // TODO : start a background service that udpate the next passages
                    if(timeToPassageSuivant == null){
                        checkActivateReminder.toggle();
                    }
                    else{
                        //startReminder();
                    }
                }
                else{
                    // TODO : kill the background service
                    //cancelReminder();
                }
            }
        });

    }

    private void handleOnClickListenerAddToFavoritesCheckbox() {
        checkAddToFavorites.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                if(checkAddToFavorites.isChecked()){
                    fav = new Favoris(arret, ligne, selectedDirection);
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

    private void toogleCheckFavorites(boolean favorite){
        if(favorite == true){
            checkAddToFavorites.setChecked(true);
        }
        else{
            checkAddToFavorites.setChecked(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addToFavorites(Favoris favoriteToAdd){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        sharedPreferences.edit().putString(favoriteToAdd.toString(), gson.toJson(favoriteToAdd)).apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void deleteFromFavorites(Favoris favoriteToDelete){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(favoriteToDelete.toString()).apply();
    }

}