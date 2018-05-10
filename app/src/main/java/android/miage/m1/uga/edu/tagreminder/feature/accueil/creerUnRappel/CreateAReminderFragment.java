package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.app.PendingIntent;
import android.content.Intent;
import android.miage.m1.uga.edu.tagreminder.MainActivity;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;
import android.miage.m1.uga.edu.tagreminder.model.passage.Time;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAReminderFragment extends Fragment {

    static LigneTransport ligne;
    static Arret arret;

    List<Passage> passageList = new ArrayList<Passage>();

    List<String> spinLigne = new ArrayList<String>();
    List<String> spinDirection = new ArrayList<String>();

    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtProchainPassage;
    TextView txtPassageSuivant;

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

        // DEBUG FOR SPINNER
        spinLigne.add(ligne.getType() + " " + ligne.getShortName());
        // END DEBUG
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_a_reminder, container, false);

        /* INIT */
        TextView txtLigneName = (TextView) view.findViewById(R.id.txt_arret_name);
        txtLigneName.setText(arret.getName());

        txtProchainPassage = (TextView) view.findViewById(R.id.txt_time_to_prochain_passage);
        txtPassageSuivant = (TextView) view.findViewById((R.id.txt_time_to_prochain_prochain_passage));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_passage);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                fetchPassageData(view);
            }
        });

        Spinner spinLignes = (Spinner) view.findViewById(R.id.spin_lignes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinLigne);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLignes.setAdapter(adapter);

        fetchPassageData(view);

        initButtons(view);

        return view;
    }

    private void fetchPassageData(final View view) {
        passageList.clear();

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
                            passageList.add(passage);
                        }
                    }
                    txtProchainPassage.setText(updateProchainPassageByDirection(2));
                    txtPassageSuivant.setText(updatePassageSuivantByDirection(2));
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            public void onFailure(Call<List<Passage>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

    private String updateProchainPassageByDirection(int direction){
        String timeToProchainPassage = String.valueOf(R.string.time_to_prochain_passage);
        for(Passage passage : passageList){
            if((passage.getPattern().getDir().equals(direction)) && (passage.getTimes().get(0).getRealtimeArrival() != null)){
                timeToProchainPassage = DateUtils.formatElapsedTime(passage.getTimes().get(0).getRealtimeArrival());
                break;
            }
        }
        return timeToProchainPassage;
    }

    private String updatePassageSuivantByDirection(int direction){
        String timeToPassageSuivant = String.valueOf(R.string.time_to_prochain_passage);
        for(Passage passage : passageList){
            if((passage.getPattern().getDir().equals(direction)) && (passage.getTimes().size() > 1)){
                timeToPassageSuivant = DateUtils.formatElapsedTime(passage.getTimes().get(1).getRealtimeArrival());
                break;
            }
        }
        return timeToPassageSuivant;
    }

    private void updateDirection(View view) {
        Spinner spinDirections = (Spinner) view.findViewById((R.id.spin_directions));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinDirection);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDirections.setAdapter(adapter);

        for (int i = 0; i < passageList.size(); i++) {
            if (passageList.get(i).getPattern().getId().contains(ligne.getId())) {
                spinDirection.add(String.valueOf(passageList.get(i).getPattern().getDesc()));
            }
        }
    }

    /** BUTTONS **/

    private void initButtons(View view) {
        final CheckBox checkActivateReminder = (CheckBox) view.findViewById(R.id.check_activer_rappel);
        checkActivateReminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.wtf("Create reminder", "Bouton clické !");
                if(checkActivateReminder.isChecked()){
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

//                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
//                            .setSmallIcon(R.drawable.ic_bus_clock_24dp)
//                            .setContentTitle("Prochains passages")
//                            .setContentText("Prochain passage : " + timeToProchainPassage)
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            // Set the intent that will fire when the user taps the notification
//                            .setContentIntent(pendingIntent)
//                            .setAutoCancel(true);
                }
                else{
                    Log.wtf("Activate reminder", "Uncheck !");
                }
            }
        });

        final CheckBox checkAddToFavorites = (CheckBox) view.findViewById(R.id.check_ajouter_favoris);
        checkAddToFavorites.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.wtf("Create reminder", "Bouton clické !");
                if(checkAddToFavorites.isChecked()){
                    Log.wtf("Add to favorites", "Check !");
                }
                else{
                    Log.wtf("Add to favorites", "Uncheck !");
                }
            }
        });

    }
}
