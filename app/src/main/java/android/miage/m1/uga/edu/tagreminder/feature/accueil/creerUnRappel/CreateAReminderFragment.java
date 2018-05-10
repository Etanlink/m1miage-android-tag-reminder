package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.model.passage.Passage;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    List<LigneTransport> ligneList = new ArrayList<LigneTransport>();
    List<Passage> passageList = new ArrayList<Passage>();

    List<String> spinLigne = new ArrayList<String>();
    List<String> spinDirection = new ArrayList<String>();

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
        View view = inflater.inflate(R.layout.fragment_create_a_reminder, container, false);

        /* INIT */
        TextView txtLigneName = (TextView) view.findViewById(R.id.txt_arret_name);
        txtLigneName.setText(arret.getName());

        Spinner spinLignes = (Spinner) view.findViewById(R.id.spin_lignes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinLigne);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLignes.setAdapter(adapter);

        fetchPassageData(view);

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
                        passageList.add(passage);
                    }
                    updateDirection(view);
                    updatePassage(view);
                }
            }

            public void onFailure(Call<List<Passage>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR: ", t.getMessage());
            }
        });
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

    private void updatePassage(View view) {
        // TODO : handle null case
        TextView txtProchainPassage = (TextView) view.findViewById(R.id.txt_time_to_prochain_passage);
        TextView txtPassageSuivant = (TextView) view.findViewById((R.id.txt_time_to_prochain_prochain_passage));
        String timeToProchainPassage = null;
        String timeToPassageSuivant = null;

        for(int i = 0; i < passageList.size(); i++){
            if(passageList.get(i).getPattern().getId().contains(ligne.getId()) && passageList.get(i).getPattern().getDir().equals(1)){
                if(!passageList.get(i).getTimes().get(0).getRealtimeArrival().equals(null)){
                    timeToProchainPassage = DateUtils.formatElapsedTime(passageList.get(i).getTimes().get(0).getRealtimeArrival());
                }
                if(!passageList.get(i).getTimes().get(1).getRealtimeArrival().equals(null)){
                    Log.wtf("Prochain prochain pasage : ", passageList.get(i).getTimes().get(1).toString());
                    timeToPassageSuivant = DateUtils.formatElapsedTime(passageList.get(i).getTimes().get(1).getRealtimeArrival());
                }
            }
        }
        txtProchainPassage.setText(timeToProchainPassage);
        txtPassageSuivant.setText(timeToPassageSuivant);
    }
}
