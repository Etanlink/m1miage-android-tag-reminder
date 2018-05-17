package android.miage.m1.uga.edu.tagreminder.feature.accueil.choisirArretParLigne;

import android.annotation.SuppressLint;
import android.content.Context;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel.CreateAReminderFragment;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel.CreateAReminderFragmentRefactor;
import android.miage.m1.uga.edu.tagreminder.model.Arret;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickAStopFragment extends Fragment {

    LinearLayout pickAStopContent;
    LinearLayout noConnectivityContent;

    static LigneTransport ligne;

    ArretAdapter arretAdapter;
    RecyclerView recyclerView;
    List<Arret> dataList = new ArrayList<Arret>();

    private ArretItemClickListener arretItemClickListener = new ArretItemClickListener() {
        @SuppressLint("ResourceType")
        @Override
        public void onItemClick(Arret arret) {
            CreateAReminderFragmentRefactor createAReminderFragmentRefactor = CreateAReminderFragmentRefactor.newInstance(ligne, arret);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.transition.enter_from_right, R.transition.exit_to_left);
            transaction.replace(R.id.to_replace, createAReminderFragmentRefactor);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };

    public static PickAStopFragment newInstance(LigneTransport ligneToAdd) {
        Bundle args = new Bundle();
        args.putSerializable(ligneToAdd.getId(), (Serializable) ligneToAdd);

        PickAStopFragment fragment = new PickAStopFragment();
        fragment.setArguments(args);

        ligne = (LigneTransport) args.getSerializable(ligneToAdd.getId());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_a_stop, container, false);

        pickAStopContent = (LinearLayout) view.findViewById(R.id.pick_a_stop_content);
        noConnectivityContent = (LinearLayout) view.findViewById(R.id.no_connectivity_content);
        noConnectivityContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noConnectivityContent.setVisibility(v.INVISIBLE);
                fetchStopData();
                pickAStopContent.setVisibility(View.VISIBLE);
            }
        });

        TextView txtLigneName = (TextView) view.findViewById((R.id.txt_ligne_name));
        txtLigneName.setText(ligne.getMode() + " " + ligne.getShortName());

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_pick_a_stop);
        arretAdapter = new ArretAdapter(getActivity(), dataList, arretItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(arretAdapter);

        fetchStopData();

        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onDetach(){
        super.onDetach();
    }

    public void fetchStopData() {
        dataList.clear();

        MetromobiliteAPI service = RetrofitInstance.getRetrofitInstance().create(MetromobiliteAPI.class);

        Call<List<Arret>> call = service.getArretsByALigne(ligne.getId());

        Log.wtf("URL called", call.request().url() + "");

        call.enqueue(new Callback<List<Arret>>() {
            @Override
            public void onResponse(Call<List<Arret>> call, Response<List<Arret>> response) {
                if (response==null){
                    Toast.makeText(getActivity(), "Something Went Wrong...!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.wtf("RESPONSE", response.body().toString());
                    for (Arret arret : response.body()){
                        dataList.add(arret);
                    }
                    arretAdapter.notifyDataSetChanged();
                }
            }

            public void onFailure(Call<List<Arret>> call, Throwable t) {
                if (t instanceof IOException) {
                    pickAStopContent.setVisibility(View.INVISIBLE);
                    noConnectivityContent.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
