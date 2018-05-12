package android.miage.m1.uga.edu.tagreminder.feature.favoris;

import android.content.Context;
import android.content.SharedPreferences;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Favoris;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment {

    List<Favoris> favoritesList = new ArrayList<Favoris>();
    RecyclerView recyclerView;

    FavoritesAdapter favoritesAdapter;

    private FavoritesItemClickListener favoritesItemClickListener = new FavoritesItemClickListener() {
        @Override
        public void onItemClick(Favoris favoris) {

        }
    };

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_favorites);
        favoritesAdapter = new FavoritesAdapter(getActivity(), favoritesList, favoritesItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(favoritesAdapter);

        fetchFavorites();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void fetchFavorites(){
        favoritesList.clear();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        Map<String, ?> map = sharedPreferences.getAll();
        for(Map.Entry<String, ?> entry : map.entrySet()){
            favoritesList.add(gson.fromJson(String.valueOf(entry.getValue()), Favoris.class));
            Log.wtf("Item favoritesList", String.valueOf(entry.getValue()));
        }
    }

    private void deleteFromFavorites(Favoris favoriteToDelete){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        Log.wtf("Favoris supprimé", "fav" + favoriteToDelete.toString());
        sharedPreferences.edit().remove("fav" + favoriteToDelete.toString()).apply();
    }
}
