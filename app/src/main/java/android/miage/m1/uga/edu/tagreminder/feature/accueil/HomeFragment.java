package android.miage.m1.uga.edu.tagreminder.feature.accueil;

import android.content.Context;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.choisirArretParLigne.PickAStopFragment;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    LinearLayout homeContent;
    LinearLayout noConnectivityContent;

    RecyclerView tramListRecyclerView;
    RecyclerView busListRecyclerView;

    LigneTransportAdapter tramListTransportAdapter;
    LigneTransportAdapter busListTransportAdapter;

    List<LigneTransport> tramList = new ArrayList<LigneTransport>();
    List<LigneTransport> busList = new ArrayList<LigneTransport>();
    List<LigneTransport> chronoList = new ArrayList<LigneTransport>();

    private LigneItemClickListener ligneItemClickListener = new LigneItemClickListener() {
        @Override
        public void onItemClick(LigneTransport ligneTransport) {
            PickAStopFragment pickAStopFragment = PickAStopFragment.newInstance(ligneTransport);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.transition.enter_from_right, R.transition.exit_to_left);
            transaction.replace(R.id.to_replace, pickAStopFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeContent = (LinearLayout) view.findViewById(R.id.home_content);
        noConnectivityContent = (LinearLayout) view.findViewById(R.id.no_connectivity_content);
        noConnectivityContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noConnectivityContent.setVisibility(v.INVISIBLE);
                fetchLigneTransportData();
                homeContent.setVisibility(View.VISIBLE);
            }
        });

        initRecyclerView(view);
        fetchLigneTransportData();

        return view;
    }

    private void initRecyclerView(View view) {
        tramListRecyclerView = (RecyclerView) view.findViewById(R.id.tram_recycler_view);
        busListRecyclerView = (RecyclerView) view.findViewById(R.id.bus_recycler_view);

        tramListTransportAdapter = new LigneTransportAdapter(getActivity(), tramList, ligneItemClickListener);
        busListTransportAdapter = new LigneTransportAdapter(getActivity(), chronoList, ligneItemClickListener);

        RecyclerView.LayoutManager tramLayoutManager = new GridLayoutManager(getActivity(), 5);
        tramListRecyclerView.setLayoutManager(tramLayoutManager);

        RecyclerView.LayoutManager busLayoutManager = new GridLayoutManager(getActivity(), 6);
        busListRecyclerView.setLayoutManager(busLayoutManager);

        tramListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        busListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        tramListRecyclerView.setAdapter(tramListTransportAdapter);
        busListRecyclerView.setAdapter(busListTransportAdapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void fetchLigneTransportData() {
        tramList.clear();
        busList.clear();
        chronoList.clear();

        MetromobiliteAPI service = RetrofitInstance.getRetrofitInstance().create(MetromobiliteAPI.class);

        Call<List<LigneTransport>> call = service.getLignesData();

        Log.wtf("URL called", call.request().url() + "");

        call.enqueue(new Callback<List<LigneTransport>>() {
            @Override
            public void onResponse(Call<List<LigneTransport>> call, Response<List<LigneTransport>> response) {
                if (response == null) {
                    Toast.makeText(getActivity(), "Something Went Wrong...!!", Toast.LENGTH_SHORT).show();
                } else {
                    for (LigneTransport ligne : response.body()) {
                        /* TODO : sort the list from the REST api to display only SEM ligne */
                        if ((ligne.getId().contains("SEM:")) && !(ligne.getId().contains("NAV"))) {
                            if(ligne.getType().contains("TRAM")) {
                                tramList.add(ligne);
                            }
                            else if(ligne.getType().contains("CHRONO")) {
                                chronoList.add(ligne);
                            }
                            else {
                                busList.add(ligne);
                            }
                        }
                    }
                    sortData(tramList);
                    sortData(busList);
                    sortData(chronoList);
                    chronoList.addAll(busList);
                    tramListTransportAdapter.notifyDataSetChanged();
                    busListTransportAdapter.notifyDataSetChanged();
                }
            }

            public void onFailure(Call<List<LigneTransport>> call, Throwable t) {
                if (t instanceof IOException) {
                    homeContent.setVisibility(View.INVISIBLE);
                    noConnectivityContent.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private List<LigneTransport> sortData(List<LigneTransport> listToSort){
        Collections.sort(listToSort, new Comparator<LigneTransport>() {
            @Override
            public int compare(LigneTransport o1, LigneTransport o2) {
                return o1.getShortName().compareTo(o2.getShortName());
            }
        });
        return listToSort;
    }
}
