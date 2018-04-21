package android.miage.m1.uga.edu.tagreminder.feature.accueil;

import android.content.Context;
import android.miage.m1.uga.edu.tagreminder.network.api.MetromobiliteAPI;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.LigneTransport;
import android.miage.m1.uga.edu.tagreminder.network.RetrofitInstance;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    LigneTransportAdapter ligneTransportAdapter;
    RecyclerView recyclerView;
    List<LigneTransport> dataList = new ArrayList<LigneTransport>();

    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener() {
        @Override
        public void onItemClick(LigneTransport ligneTransport) {
            /* TODO : start a new fragment with the ligneTransport information*/
            Toast.makeText(getActivity(),"Click :  " + ligneTransport.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public static HomeFragment newInstance(){
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

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_home_recycler_view);
        ligneTransportAdapter = new LigneTransportAdapter(getActivity(), dataList, recyclerItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(ligneTransportAdapter);

        getData();

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

    public void getData() {
        MetromobiliteAPI service = RetrofitInstance.getRetrofitInstance().create(MetromobiliteAPI.class);

        Call<List<LigneTransport>> call = service.getLignesData();

        Log.wtf("URL called", call.request().url() + "");

        call.enqueue(new Callback<List<LigneTransport>>() {
            @Override
            public void onResponse(Call<List<LigneTransport>> call, Response<List<LigneTransport>> response) {
                if (response==null){
                    Toast.makeText(getActivity(), "Somthing Went Wrong...!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.wtf("RESPONSE", response.body().toString());
                    for (LigneTransport ligne : response.body()){
                        /* TODO : sort the list from the REST api to display only SEM ligne */
                        if(ligne.getId().contains("SEM:")){
                            dataList.add(ligne);
                        }
                    }
                    ligneTransportAdapter.notifyDataSetChanged();
                }
            }

            public void onFailure(Call<List<LigneTransport>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ERROR: ", t.getMessage());
            }
        });
    }

}
