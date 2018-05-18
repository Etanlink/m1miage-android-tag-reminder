package android.miage.m1.uga.edu.tagreminder.feature.favoris;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.miage.m1.uga.edu.tagreminder.R;
import android.miage.m1.uga.edu.tagreminder.feature.accueil.creerUnRappel.AlarmReceiver;
import android.miage.m1.uga.edu.tagreminder.model.Favoris;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder> {

    Context context;
    List<Favoris> items;
    private FavoritesItemClickListener favoritesItemClickListener;

    public FavoritesAdapter(Context context, List<Favoris> items, FavoritesItemClickListener favoritesItemClickListener) {
        this.context = context;
        this.items = items;
        this.favoritesItemClickListener = favoritesItemClickListener;
    }

    @NonNull
    @Override
    public FavoritesAdapter.FavoritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.favoris_item, parent, false);
        return new FavoritesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesHolder holder, final int position) {
        final Favoris favoris = items.get(position);

        holder.txt_stop_name.setText(favoris.getArret().getName());
        holder.txt_direction.setText(favoris.getDirection());
        holder.shortNameTxt.setText(favoris.getLigne().getShortName());

        if(favoris.getLigne().getTextColor() != null){
            holder.shortNameTxt.setTextColor(Color.parseColor("#"+favoris.getLigne().getTextColor()));
        }

        if(favoris.getLigne().getColor() != null){
            holder.logoCard.setCardBackgroundColor(Color.parseColor("#"+favoris.getLigne().getColor()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesItemClickListener.onItemClick(favoris);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favoris favorisToRestore, int position) {
        items.add(position, favorisToRestore);
        notifyItemInserted(position);
    }

    public class FavoritesHolder extends RecyclerView.ViewHolder {

        TextView txt_stop_name, txt_direction;
        TextView shortNameTxt;
        CardView logoCard;
        RelativeLayout viewBackground;
        LinearLayout viewForeground;

        public FavoritesHolder(View view) {
            super(view);
            txt_stop_name = (TextView) view.findViewById(R.id.txt_stop_name_favorites);
            txt_direction = (TextView) view.findViewById(R.id.txt_direction_favorites);
            shortNameTxt = (TextView) view.findViewById(R.id.txt_short_name_favorites);
            logoCard = (CardView) view.findViewById((R.id.crd_ligne_favorites));
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

}
