package android.miage.m1.uga.edu.tagreminder.feature.favoris;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.model.Favoris;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment implements FavoritesItemTouchHelper.FavoritesItemTouchHelperListener {

    List<Favoris> favoritesList = new ArrayList<Favoris>();
    RecyclerView recyclerView;
    LinearLayout linearLayout;

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

        linearLayout = (LinearLayout) view.findViewById(R.id.lin_layout_favorites);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_favorites);
        favoritesAdapter = new FavoritesAdapter(getActivity(), favoritesList, favoritesItemClickListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(favoritesAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new FavoritesItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

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
            Log.wtf("Item favoritesList", String.valueOf(entry.getKey()));
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesAdapter.FavoritesHolder) {

            final Favoris deletedItem = favoritesList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            favoritesAdapter.removeItem(viewHolder.getAdapterPosition());
            deleteFromFavorites(deletedItem);

            Snackbar snackbar = Snackbar.make(linearLayout, "Supprimé des favoris", Snackbar.LENGTH_LONG);
            snackbar.setAction("Annuler", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addToFavorites(deletedItem);
                    favoritesAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void addToFavorites(Favoris favoriteToAdd){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        Log.wtf("Favoris ajouté", favoriteToAdd.toString());
        sharedPreferences.edit().putString(favoriteToAdd.toString(), gson.toJson(favoriteToAdd)).apply();
    }

    private void deleteFromFavorites(Favoris favoriteToDelete){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        Log.wtf("Favoris supprimé", favoriteToDelete.toString());
        sharedPreferences.edit().remove(favoriteToDelete.toString()).apply();
        fetchFavorites();
    }
}
