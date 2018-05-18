package android.miage.m1.uga.edu.tagreminder.feature.favoris;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel.AlarmReceiver;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment implements FavoritesItemTouchHelper.FavoritesItemTouchHelperListener {

    List<Favoris> favoritesList = new ArrayList<Favoris>();
    RecyclerView recyclerView;
    LinearLayout linearLayout;

    FavoritesAdapter favoritesAdapter;

    private Intent alarmIntent;
    private PendingIntent pendingIntent;

    private FavoritesItemClickListener favoritesItemClickListener = new FavoritesItemClickListener() {
        @Override
        public void onItemClick(Favoris favoris) {
            if(pendingIntent == null){
                startReminder(favoris);
            }
            else {
                cancelReminder(favoris);
            }
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

    public void startReminder(Favoris favoris) {
        if(pendingIntent != null){
            AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pendingIntent);
        }

        alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent.putExtra("ligneId", favoris.getLigne().getId());
        alarmIntent.putExtra("ligneType", favoris.getLigne().getType());
        alarmIntent.putExtra("ligneShortName", favoris.getLigne().getShortName());
        alarmIntent.putExtra("arretName", favoris.getArret().getName());
        alarmIntent.putExtra("arretCode", favoris.getArret().getCode());
        alarmIntent.putExtra("direction", favoris.getDirection());

        pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int interval = 30 * 1000; // 30 seconds
        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

        Toast.makeText(getActivity(), "Suivi activé", Toast.LENGTH_SHORT).show();
    }

    public void cancelReminder(Favoris favoris) {
        AlarmManager alarm = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);

        Toast.makeText(getActivity(), "Suivi désactivé", Toast.LENGTH_SHORT).show();
    }
}
