package android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel;

import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class CreateAReminderFragment extends Fragment {

    static LigneTransport ligne;
    static Arret arret;

    List<LigneTransport> dataList = new ArrayList<LigneTransport>();


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
        dataList.add(ligne);
        // END DEBUG
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_a_reminder, container, false);

        /* INIT */
        TextView txtLigneName = (TextView) view.findViewById(R.id.txt_arret_name);
        txtLigneName.setText(arret.getName());

        Spinner spinLignes = (Spinner) view.findViewById(R.id.spin_lignes);
        ArrayAdapter<LigneTransport> adapter = new ArrayAdapter<LigneTransport>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLignes.setAdapter(adapter);

        /* DATA */
        getArretsOfALigneData();
        getNextUpComingLineData();


        return view;
    }

    private void getArretsOfALigneData() {
        
    }

    private void getNextUpComingLineData() {

    }

}
